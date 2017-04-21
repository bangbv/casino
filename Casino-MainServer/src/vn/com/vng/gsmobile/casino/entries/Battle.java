package vn.com.vng.gsmobile.casino.entries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.Table;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.business.Service;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGameNotice;
import vn.com.vng.gsmobile.casino.flatbuffers.GameResultInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.flatbuffers.GameType;
import vn.com.vng.gsmobile.casino.flatbuffers.RankType;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class Battle implements Runnable, IBattle {
	protected Long lId = null;
	protected Room oRoom = null;
	protected Table oData = null;
	protected List<Long> lPlayer = null;
	protected long lLastAction = System.currentTimeMillis();
	protected Long timePerTurn = 15000l; //default 15s
	protected List<GameResultInfo> lResult = new ArrayList<>();
	protected Map<Long, Long> mResultBonus = new HashMap<>();
	protected Map<Long, Long> mResultPenance = new HashMap<>();
	protected Map<Long, Integer> mExp = new HashMap<>();
	protected long betValue = 0;
	private long lStartTime = 0;
	private long lFinishTime = 0;
	
	public Battle(){
		this.lPlayer = new ArrayList<>();
	}
	public Long getTimeRemaining(){
		long lTimeRemaining = timePerTurn - (System.currentTimeMillis()-lLastAction);
		if(lTimeRemaining < 0) lTimeRemaining = 0l;
		return lTimeRemaining;
	}
	public Long setId(Long lId){
		return this.lId = lId;
	}

	public Room setRoom(Room oRoom){
		if(oRoom!=null)
			timePerTurn = GameConfig.load(false).getObject(GameConfig.GAMELIST).getObject(""+oRoom.getGameType()).getLong(GameConfig.TIME_TURNPLAY);
		return this.oRoom = oRoom;
	}
	
	public void release(){
		this.oRoom = null;
	}
	
	public Long getId(){
		return lId;
	}

	public Table getData(){
		return oData;
	}	
	public long getLastAction(){
		return lLastAction;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(Const.IS_STOPPING) return; //Nếu đang tắt server thì bỏ qua
		try{
			if(oRoom!=null){
				//1. Chia bài
				this.start();
				//2. Chơi cho đến khi ván bài kết thúc
				this.playing();
				//3. Kết thúc ván bài
				this.finish();
			}
		}catch(Exception e){
			Lib.getLogger().error(Arrays.asList("run", lId, oRoom, oData, Lib.getStackTrace(e), Battle.class.getSimpleName()));
			lStartTime = 0;
			lFinishTime = 0;
		}finally {
			//4. Chuẩn bị ván mới
			if(oRoom!=null)
				oRoom.getDealer().execute();
		}
	}
	@Override
	public void start(){
		lStartTime = System.currentTimeMillis();
		oRoom.setState(GameRoomState.Playing);
		oRoom.randomBigBet();
		betValue = oRoom.getBet() * (oRoom.isBigBet()?oRoom.getBigBet():1);
		lPlayer.clear();
		Iterator<Long> itPlayers = oRoom.getPlayers().iterator();
		while(itPlayers.hasNext()){
			Long uid = itPlayers.next();
			if(!lPlayer.contains(uid))
				lPlayer.add(uid);
		}
		List<Channel> lc = oRoom.getChannels();
		Service.sendToClient(
				Dealer.class.getSimpleName(), 
				lId+"_Start", Service.CMDTYPE_REQUEST, 
				lc,
				Arrays.asList(CMD.PUSH_UPDATEROOM.cmd,CMD.PUSH_UPDATEROOM.subcmd,CMD.PUSH_UPDATEROOM.version,(byte)0, oRoom.toRoomUpdateInfo(0l))							
			);
	}
	@Override
	public void playing(){
		lLastAction = System.currentTimeMillis();
	}
	@Override
	public void finish(){
		lFinishTime = System.currentTimeMillis();
		//push roomupdateinfo
		Long lTimeFinishBattle = GameConfig.load(false).getObject(GameConfig.GAMELIST).getObject(""+oRoom.getGameType()).getLong(GameConfig.TIME_FINISHBATTLE);
		oRoom.setState(GameRoomState.Finished);
		List<Channel> lc = oRoom.getChannels();
		Service.sendToClient(
				Dealer.class.getSimpleName(), 
				lId+"_Finish", Service.CMDTYPE_REQUEST, 
				lc,
				Arrays.asList(CMD.PUSH_UPDATEROOM.cmd,CMD.PUSH_UPDATEROOM.subcmd,CMD.PUSH_UPDATEROOM.version,(byte)0, oRoom.toRoomUpdateInfo(lTimeFinishBattle))							
			);
		//save here
		lResult.clear();
		mResultBonus.clear();
		mResultPenance.clear();
		mExp.clear();
		save();
		//log here
		log();
		//push here
		Service.sendToClient(
				Dealer.class.getSimpleName(), 
				lId+"_ListUserInfo", Service.CMDTYPE_REQUEST, 
				lc,
				Arrays.asList(CMD.USER_LIST.cmd,CMD.USER_LIST.subcmd,CMD.USER_LIST.version,(byte)0, User.toListUserInfo(oRoom.getAllUsers()))							
			);
		//wait for finish
		synchronized (this) {
			try {
				this.wait(lTimeFinishBattle);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	public boolean isPlaying(Long uid){
		return this.lPlayer.contains(uid);
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		List<String> winNotice = new ArrayList<>();
		for(GameResultInfo rs : lResult){
			String uid = String.format("%d", rs.playerId());
			JsonObject ursc = null;
			String key = User.USERESOURCE_TABLENAME+rs.playerId();
			try {
				List<?> l = Lib.getDBGame(false).getCBConnection().get(key);
				if((boolean) l.get(0) && l.get(1)!=null){
					ursc = ((JsonDocument) l.get(1)).content();
					Integer win_cnt = ursc.getInt(User.WIN);
					Integer lose_cnt = ursc.getInt(User.LOSE);					
					// add RankType_RankGlobalPlayCnt: total battles of each user
					Rank.add(uid,win_cnt+lose_cnt+1,RankType.RankGlobalPlayCnt);					
					//1. win count, lose count
					if(rs.rank()==1){
						if(win_cnt!=null){
							win_cnt += 1;
							ursc.put(User.WIN, win_cnt);
						}
					}else if(rs.rank() > 1){
						if(lose_cnt!=null){
							lose_cnt += 1;
							ursc.put(User.LOSE, lose_cnt);
						}
					}
					//2. coin: thu tiền tip với các khoản thắng
					Long main_coin = rs.gold();
					if(main_coin>0)
						main_coin = main_coin * (100-oRoom.getTip())/100;
					Long bonus_coin = mResultBonus.get(rs.playerId());
					if(bonus_coin==null)
						bonus_coin = 0l;
					else
						bonus_coin = bonus_coin * (100-oRoom.getTip())/100;
					Long penance_coin = mResultPenance.get(rs.playerId());
					if(penance_coin==null)
						penance_coin = 0l;
					
					Long result_coin = main_coin + bonus_coin + penance_coin;
					if(result_coin!=0){
						Long have_coin = ursc.getLong(User.COIN);
						if(have_coin != null){
							have_coin += result_coin;
							if(have_coin < 0) have_coin = 0l;
							ursc.put(User.COIN, have_coin);
							// add to list top money
							Rank.add(uid,have_coin.doubleValue(),RankType.RankGlobalMoney);
						}
					}
					//3. level exp
					Integer result_exp = mExp.get(rs.playerId());
					if(result_coin!=null){
						Integer have_exp = ursc.getInt(User.EXP);
						Integer have_level = ursc.getInt(User.LEVEL);
						if(have_exp!=null){
							have_exp += result_exp;
							List<Integer> new_level_exp = LevelExp.getNewLevelExp(have_level, have_exp);
							ursc.put(User.LEVEL, new_level_exp.get(0));
							ursc.put(User.EXP, new_level_exp.get(1));
							// add to list top level - exp
							Integer sort_level_exp = LevelExp.getSort(new_level_exp);
							Rank.add(uid, sort_level_exp, RankType.RankGlobalLevel);
						}
						
					}
					//4. save DB and update localCache
					Lib.getDBGame(false).getCBConnection().upsert(JsonDocument.create(key, ursc));
					LocalCache.put(key, ursc);
					//5. update UserCondition
					User u = new User(rs.playerId());
					List<List<?>> conds = new ArrayList<>();
					long lBattleTime = (lFinishTime-lStartTime)/1000;
					conds.add(Arrays.asList(String.format(User.PATTERN_COND, oRoom.getGameType().toString(), User.COND_GAME_PLAY_TIME), lBattleTime, CondUpdateType.Increase));
					conds.add(Arrays.asList(User.COND_PLAY_TIME, lBattleTime, CondUpdateType.Increase));
					conds.add(Arrays.asList(User.COND_DAILY_PLAY_TIME, lBattleTime, CondUpdateType.Increase));
					conds.add(Arrays.asList(User.COND_DAILY_GAME_PLAY_TIME, lBattleTime, CondUpdateType.Increase));
					if(rs.rank()==ResultType.NHAT){
						conds.add(Arrays.asList(String.format(User.PATTERN_COND, oRoom.getGameType().toString(), User.COND_GAME_WIN_CNT), 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(String.format(User.PATTERN_COND, oRoom.getGameType().toString(), User.COND_GAME_CONSECUTIVE_WINS_CNT), 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(String.format(User.PATTERN_COND, oRoom.getGameType().toString(), User.COND_GAME_CONSECUTIVE_LOSS_CNT), 0, CondUpdateType.Upsert));
						conds.add(Arrays.asList(User.COND_CONSECUTIVE_WINS_CNT, 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(User.COND_CONSECUTIVE_LOSS_CNT, 0, CondUpdateType.Upsert));
						conds.add(Arrays.asList(User.COND_DAILY_CONSECUTIVE_WINS_CNT, 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(User.COND_DAILY_CONSECUTIVE_LOSS_CNT, 0, CondUpdateType.Upsert));
						conds.add(Arrays.asList(User.COND_DAILY_WIN_CNT, 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(String.format(User.PATTERN_COND, oRoom.getGameType().toString(), User.COND_DAILY_GAME_CONSECUTIVE_WINS_CNT), 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(String.format(User.PATTERN_COND, oRoom.getGameType().toString(), User.COND_DAILY_GAME_CONSECUTIVE_LOSS_CNT), 0, CondUpdateType.Upsert));
						conds.add(Arrays.asList(String.format(User.PATTERN_COND, oRoom.getGameType().toString(), User.COND_DAILY_GAME_WIN_CNT), 1, CondUpdateType.Increase));
					}
					else{
						conds.add(Arrays.asList(String.format(User.PATTERN_COND, oRoom.getGameType().toString(), User.COND_GAME_LOSE_CNT), 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(String.format(User.PATTERN_COND, oRoom.getGameType().toString(), User.COND_GAME_CONSECUTIVE_LOSS_CNT), 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(String.format(User.PATTERN_COND, oRoom.getGameType().toString(), User.COND_GAME_CONSECUTIVE_WINS_CNT), 0, CondUpdateType.Upsert));
						conds.add(Arrays.asList(User.COND_CONSECUTIVE_LOSS_CNT, 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(User.COND_CONSECUTIVE_LOSS_CNT, 0, CondUpdateType.Upsert));
						conds.add(Arrays.asList(User.COND_DAILY_CONSECUTIVE_LOSS_CNT, 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(User.COND_DAILY_CONSECUTIVE_WINS_CNT, 0, CondUpdateType.Upsert));
						conds.add(Arrays.asList(User.COND_DAILY_LOSE_CNT, 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(String.format(User.PATTERN_COND, oRoom.getGameType().toString(), User.COND_DAILY_GAME_CONSECUTIVE_LOSS_CNT), 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(String.format(User.PATTERN_COND, oRoom.getGameType().toString(), User.COND_DAILY_GAME_CONSECUTIVE_WINS_CNT), 0, CondUpdateType.Upsert));
						conds.add(Arrays.asList(String.format(User.PATTERN_COND, oRoom.getGameType().toString(), User.COND_DAILY_GAME_LOSE_CNT), 1, CondUpdateType.Increase));
					}
					if(result_coin > 0){
						conds.add(Arrays.asList(User.COND_COIN_WIN_TOTAL, result_coin, CondUpdateType.Increase));
						conds.add(Arrays.asList(User.COND_DAILY_COIN_WIN, result_coin, CondUpdateType.Increase));
						conds.add(Arrays.asList(User.COND_DAILY_COIN_BALANCE, result_coin, CondUpdateType.Increase));
					}
					else{
						conds.add(Arrays.asList(User.COND_COIN_LOSE_TOTAL, -result_coin, CondUpdateType.Increase));
						conds.add(Arrays.asList(User.COND_DAILY_COIN_LOSE, -result_coin, CondUpdateType.Increase));
						conds.add(Arrays.asList(User.COND_DAILY_COIN_BALANCE, result_coin, CondUpdateType.Increase));
					}
					Number suggest_lobby = u.getConditionValue(CondType.LobbySuggest, oRoom.getGameType());
					if(suggest_lobby.byteValue() < oRoom.getLobbyType()){
						conds.add(Arrays.asList(User.COND_CHALLENGER, 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(User.COND_DAILY_CHALLENGER, 1, CondUpdateType.Increase));
					}
					if(RoomManager.isMaxBetOfLobby(oRoom.getGameType(), oRoom.getLobbyType(), oRoom.getBet())){
						conds.add(Arrays.asList(User.COND_CHALLENGER, 1, CondUpdateType.Increase));
						conds.add(Arrays.asList(User.COND_DAILY_CHALLENGER, 1, CondUpdateType.Increase));
					}
					Number maxcoin = u.getConditionValue(CondType.CoinWinMax);
					if(rs.gold() > maxcoin.longValue()){
						conds.add(Arrays.asList(User.COND_COIN_WIN_MAX, rs.gold(), CondUpdateType.Upsert));
					}
					u.setConditionValue(conds);
					// add gameTypeWinCnt to ranking
					int gameTypeWinCnt = u.getConditionValue(CondType.WinCount, oRoom.getGameType()).intValue();
					Rank.add(uid,gameTypeWinCnt,getRankType());
					//hardcode tạm tại đây
					if(rs.rank()==ResultType.NHAT){
						JsonObject user = LocalCache.get(User.USER_TABLENAME+rs.playerId());
						if(user!=null){
							winNotice.add(String.format(Notice.WINNER_NOTICE, user.getString(User.NAME)));
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Lib.getLogger().error(Arrays.asList("save", key, ursc, rs, Lib.getStackTrace(e), this.getClass().getSimpleName()));
				ursc = null;
			}
		}
		winNotice.add(Notice.WELCOME_NOTICE);
		CMDGameNotice gn = Notice.toGameNotice(oRoom.getGameType(), winNotice);
		List<Channel> lc = new ArrayList<>();
		lc.addAll(oRoom.getChannels());
		Lobby l = RoomManager.getLobby(oRoom.getGameType());
		if(l!=null){
			lc.addAll(l.getChannels());
		}
		Service.sendToClient(
			this.getClass().getSimpleName(), 
			String.format("%d_Notice", this.lId),
			Service.CMDTYPE_REQUEST, 
			lc, 
			Arrays.asList(CMD.GAME_NOTICE.cmd, CMD.GAME_NOTICE.subcmd, CMD.GAME_NOTICE.version, (byte)0, gn)
		);
	}

	@Override
	public void log() {
		// TODO Auto-generated method stub
	}
	
	private byte getRankType(){
		byte rankType = RankType.RankNone;
		switch (oRoom.getGameType()) {
		case GameType.TLMN:
			rankType = RankType.RankGameTLMN;
			break;
		case GameType.TALA:
			rankType = RankType.RankGameTala;
			break;
		case GameType.BALA:
			rankType = RankType.RankGameCao;
			break;
		case GameType.MAUBINH:
			rankType = RankType.RankGameMauBinh;
			break;			
		}
		return rankType;
	}
}
