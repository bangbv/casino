package vn.com.vng.gsmobile.casino.entries.bala;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.business.Service;
import vn.com.vng.gsmobile.casino.entries.Battle;
import vn.com.vng.gsmobile.casino.entries.CMD;
import vn.com.vng.gsmobile.casino.entries.Card;
import vn.com.vng.gsmobile.casino.entries.ExpType;
import vn.com.vng.gsmobile.casino.entries.LevelExp;
import vn.com.vng.gsmobile.casino.entries.ResultType;
import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.flatbuffers.Card3LaInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.Game3LaGameInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameResultInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.flatbuffers.Player3LaCardInfo;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class CaoBattle extends Battle {
	@Override
	public void start() {
		// TODO Auto-generated method stub
		super.start();
		//1. chia bài
		CaoDeck pc = new CaoDeck();
		pc.dealing();
		//2. build data
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		builder.forceDefaults(true);
		List<Integer> l = new ArrayList<>();
		List<Integer> r = new ArrayList<>();
		Iterator<Card> it = pc.getCards().iterator();
		for(Long uid : lPlayer){
			if(uid != null && uid > 0){
				List<Integer> c = new ArrayList<>();
				int i = 0;
				while(it.hasNext() && i++ < 3){
					c.add(Card3LaInfo.createCard3LaInfo(builder, it.next().Id, 0));
				}
				l.add(Player3LaCardInfo.createPlayer3LaCardInfo(builder, 
						uid,
						Player3LaCardInfo.createCardsVector(builder, ArrayUtils.toPrimitive(c.toArray(new Integer[c.size()])))
					));
				r.add(GameResultInfo.createGameResultInfo(builder, uid, 0, 0));
			}
		}
		int iData = Game3LaGameInfo.createGame3LaGameInfo(builder, 
				oRoom.getId(), 
				lId, 
				Game3LaGameInfo.createCardListVector(builder, ArrayUtils.toPrimitive(l.toArray(new Integer[l.size()]))), 
				Game3LaGameInfo.createResultVector(builder, ArrayUtils.toPrimitive(r.toArray(new Integer[r.size()]))),
				GameRoomState.Playing,
				timePerTurn
			);
		builder.finish(iData);
		this.oData = Game3LaGameInfo.getRootAsGame3LaGameInfo(builder.dataBuffer());
		//3. Gửi bài cho người chơi trong phòng (cả ng chờ và ng chơi)
		List<Channel> lc = oRoom.getChannels();
		Service.sendToClient(
				CaoBattle.class.getSimpleName(), 
				lId+"_Data", Service.CMDTYPE_REQUEST, 
				lc,
				Arrays.asList(CMD.CAO_BATTLE_INFO.cmd,CMD.CAO_BATTLE_INFO.subcmd,CMD.CAO_BATTLE_INFO.version,(byte)0,this.oData)							
			);
		lLastAction = System.currentTimeMillis();
	}

	@Override
	public void playing() {
		// TODO Auto-generated method stub
		//các lượt đánh tại đây, game ba lá chờ timePerTurn ms hết ván
		synchronized (this) {
			try {
				this.wait(timePerTurn);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.playing();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		//xử lý kết thúc ván tại đây rồi gọi finish của super
		//1. lật bài
		List<CaoHand> lRs = new ArrayList<>();
		Game3LaGameInfo data = (Game3LaGameInfo) this.oData;
		for(int i = 0 ; i < data.cardListLength(); i++){
			int cnt = data.cardList(i).cardsLength();
			for(int j = 0; j < cnt; j++)
				data.cardList(i).cards(j).mutateShowFlag(1);
			lRs.add(new CaoHand(data.cardList(i), i));
		}
		//2. tính điểm thắng thua, tìm số người thắng
		Collections.sort(lRs);
		CaoHand winner = lRs.get(lRs.size()-1);
		//System.out.println(Arrays.asList(winner, lRs));
		//3. chia tiền - cùng nhất thì chia đều tiền - tất cả bằng điểm nhau thì hòa
		int winner_cnt = 0;
		Long win_coin = 0l;
		synchronized (data) {
			for(int i = 0 ; i < data.cardListLength(); i++){
				CaoHand c = lRs.get(i);
				if(c.compareTo(winner) < 0){
					Long lose_coin = betValue;
					JsonObject u = LocalCache.get(User.USERESOURCE_TABLENAME+data.result(i).playerId());
					if(u!=null){
						Long have_coin = u.getLong(User.COIN);
						if(lose_coin > have_coin)
							lose_coin = have_coin;
					}
					data.result(c.getPlayIdx()).mutateRank(ResultType.NHI);
					data.result(c.getPlayIdx()).mutateGold(-lose_coin.intValue());
					win_coin += lose_coin;
				}
				else{
					winner_cnt+=1;
					data.result(c.getPlayIdx()).mutateRank(ResultType.NHAT);
				}
			}
			win_coin = win_coin/winner_cnt;
			for(int i = 0 ; i < data.cardListLength(); i++){
				if(data.result(i).rank()==1){
					data.result(i).mutateGold(win_coin.intValue());
				}
			}
			data.mutateState(GameRoomState.Finished);
			data.mutateTimeRemaining(0);
		}
		//4. Gửi kết quả cho người chơi trong phòng (cả ng chờ và ng chơi)
		List<Channel> lc = oRoom.getChannels();
		Service.sendToClient(
				CaoBattle.class.getSimpleName(), 
				lId+"_Data_Finish", Service.CMDTYPE_REQUEST, 
				lc,
				Arrays.asList(CMD.CAO_BATTLE_INFO.cmd,CMD.CAO_BATTLE_INFO.subcmd,CMD.CAO_BATTLE_INFO.version,(byte)0,data)							
			);
		//5. chờ kết thúc quá trình show bài của all player
		synchronized (this) {
			try {
				this.wait(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//6. call super xử lý kết thúc ván bài: save, log, push roomupdateinfo
		super.finish();
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		Game3LaGameInfo data = (Game3LaGameInfo) this.oData;
		for(int j = 0; j < data.resultLength(); j++){
			GameResultInfo rs = data.result(j);
			lResult.add(rs);
			mExp.put(rs.playerId(), LevelExp.getExp(rs.rank()==1?ExpType.Normal:ExpType.None));
		}
		super.save();
	}

}
