package vn.com.vng.gsmobile.casino.entries.tlmn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.ArrayUtils;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.business.Service;
import vn.com.vng.gsmobile.casino.entries.Battle;
import vn.com.vng.gsmobile.casino.entries.CMD;
import vn.com.vng.gsmobile.casino.entries.Card;
import vn.com.vng.gsmobile.casino.entries.ChannelType;
import vn.com.vng.gsmobile.casino.entries.ExpType;
import vn.com.vng.gsmobile.casino.entries.GameDataType;
import vn.com.vng.gsmobile.casino.entries.Handshake;
import vn.com.vng.gsmobile.casino.entries.LevelExp;
import vn.com.vng.gsmobile.casino.entries.ResultType;
import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGameTLMNUpdate;
import vn.com.vng.gsmobile.casino.flatbuffers.CardID;
import vn.com.vng.gsmobile.casino.flatbuffers.CardTLMNInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameResultInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.flatbuffers.GameTLMNInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameTLMNInterruptInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.PlayerTLMNCardInfo;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class TLMNBattle extends Battle {
	public static boolean UPDATE_GAMEINFO_FULL = false;
	private int last_player_idx_show_cards = -1;
	private int player_idx = -1;
	private int turn_idx = 0;
	private int ring_idx = 0;
	private AtomicInteger players_in_ring = new AtomicInteger(0);
	private boolean interrupt_begin = false;
	private boolean interrupt_end = false;
	private int interrupt_score = 0;
	private int interrupt_score_heo = 0; //cộng dồn điểm các heo trong cả vòng chặt
	private int interrupt_idx = 0; //lần chặt trong 1 ván bài
	private int interrupt_player1_idx = -1; //idx người chặt cuối
	private int interrupt_player2_idx = -1; //idx người bị chặt cuối
	private int last_interrupt_idx = -1; //lần chặt cuối cùng
	private int interrupt_period_idx = -1; //idx người chặt k cần vòng - bốn đôi thông
	private int last_interrupt_period_idx = -1; //idx người chặt k cần vòng cuối cùng (sẽ đc đánh tại vòng sau)
	private boolean trang = false;
	private boolean finish = false;
	private boolean hasAction = false;
	private boolean ringNew = false;
	private boolean isSkipTurn = false; // bỏ lượt
	private List<Integer> lPlayerCards = null;
	private List<Boolean> lPlayerInRing = null;
	private ConcurrentLinkedQueue<Integer> lPlayerInRingInterruptPeriod = new ConcurrentLinkedQueue<Integer>();
	private TLMNHand lastPlayCards = null;
	private byte finishType = FinishType.SOLE_WINNER; //Đếm lá
	List<Long> coinList = new ArrayList<>();

	public synchronized void setLastPlayCards(List<TLMNCard> oData, int player_idx){
		lPlayerCards.set(player_idx, lPlayerCards.get(player_idx)-oData.size());
		if(lastPlayCards!=null){
			lastPlayCards.release();
			lastPlayCards = null;
		}
		lastPlayCards = new TLMNHand(oData);
	}
	public synchronized void setLastPlayCards(TLMNHand oData, int player_idx){
		lPlayerCards.set(player_idx, lPlayerCards.get(player_idx)-oData.getCards().size());
		if(lastPlayCards!=null){
			lastPlayCards.release();
			lastPlayCards = null;
		}
		lastPlayCards = oData;
	}
	public TLMNHand getLastPlayCards(){
		return this.lastPlayCards;
	}
	public int getLastPlayerIdx(){
		return this.last_player_idx_show_cards;
	}
	public int getTurnIdx(){
		return this.turn_idx;
	}
	public int getRingIdx(){
		return this.ring_idx;
	}
	public int getPlayIdx(){
		return this.player_idx;
	}
	public int getInterruptScoreHeo(){
		return this.interrupt_score_heo;
	}
	public void setInterruptScoreHeo(int score){
		this.interrupt_score_heo += score;
	}
	public boolean isRingNew(){
		return this.ringNew;
	}
	public boolean isInterrupting(){
		return this.interrupt_begin && !this.interrupt_end;
	}
	public synchronized void setShowCardInterruptPeriod(int score, int interrupter_idx){
		interrupt_score += score;
		interrupt_begin = true;
		interrupt_player1_idx = interrupter_idx;
		interrupt_player2_idx = last_player_idx_show_cards;
		interrupt_period_idx = interrupter_idx;
		last_interrupt_period_idx = interrupter_idx;
		if(!lPlayerInRing.get(interrupter_idx)){
			//1. nếu k có trong vòng đánh thì join vào vòng chặt ưu tiên
			if(!lPlayerInRingInterruptPeriod.contains(interrupter_idx)){
				lPlayerInRingInterruptPeriod.add(interrupter_idx);
				players_in_ring.incrementAndGet();
			}
		}
		else{
			//2.1 bỏ khỏi vòng thường
			lPlayerInRing.set(interrupter_idx, false);
			//2.2 chuyển vào vòng chặt ưu tiên
			if(!lPlayerInRingInterruptPeriod.contains(interrupter_idx))
				lPlayerInRingInterruptPeriod.add(interrupter_idx);
		}
		this.setShowCardNormal();
		last_player_idx_show_cards = interrupter_idx;
		System.out.println(Arrays.asList("lPlayerInRingInterruptPeriod=",lPlayerInRingInterruptPeriod));
	}
	public synchronized void setShowCardInterrupt(int score){
		interrupt_score += score;
		interrupt_begin = true;
		interrupt_player1_idx = player_idx;
		interrupt_player2_idx = last_player_idx_show_cards;
		this.setShowCardNormal();
	}
	private synchronized void setEndInterrupt(){
		interrupt_score_heo = 0;
		if(isInterrupting()) {
			interrupt_end = true;
		}
	}
	private synchronized void finishInterrupt(){
		interrupt_begin = false;
		interrupt_end = false;
		interrupt_score = 0;
		interrupt_player1_idx = -1;
		interrupt_player2_idx = -1;
		interrupt_period_idx = -1;
		interrupt_idx += 1;
	}
	public synchronized void setShowCardNormal(){
		ringNew = false;
		hasAction = true;
		isSkipTurn = false;
		last_player_idx_show_cards = player_idx;
	}
	private synchronized void nextTurnInterruptPeriod(){
		hasAction = false;
		turn_idx += 1;
	}
	private synchronized void nextTurn(){
		hasAction = false;
		turn_idx += 1;
		if(player_idx != -1){
			//1. Tìm trong vòng chơi bình thường
			int total_players = lPlayerInRing.size();
			int nextIndex = (player_idx+1)%total_players;
			boolean isSkipAll = true;
			for(int i = nextIndex; i < total_players + nextIndex; i++){
				int idx = i % total_players;
				if(lPlayerInRing.get(idx)){
					player_idx = idx;
					isSkipAll = false;
					break;
				}
			}
			//2. Nếu vòng chơi thường hết người đánh thì tìm trong vòng chặt ưu tiên (không cần vòng - bốn đôi thông)
			if(isSkipAll){
				Integer tmp = lPlayerInRingInterruptPeriod.poll();
				if(tmp!=null)
					player_idx = tmp;
			}
			System.out.println(Arrays.asList("player_idx=",player_idx,"lPlayerInRingInterruptPeriod=",lPlayerInRingInterruptPeriod));
			//3. Nếu còn 1 người trong vòng (cả vòng thường và vòng chặt ưu tiên) thì bắt đầu vòng mới
			if(players_in_ring.intValue()==1){
				newRing();
			}
		}
		
	}
	private synchronized void skipRing(){
		ringNew = false;
		hasAction = false;
		isSkipTurn = true;
		lPlayerInRing.set(player_idx, false);
		players_in_ring.decrementAndGet();
	}
	private synchronized void newRing(){
		ringNew = true;
		ring_idx += 1;
		if(last_interrupt_period_idx!=-1){
			player_idx = last_interrupt_period_idx;
			last_interrupt_period_idx = -1;
		}
		lPlayerInRingInterruptPeriod.clear();
		players_in_ring.set(lPlayerInRing.size());
		for(int i = 0; i < lPlayerInRing.size(); i++)
			lPlayerInRing.set(i, true);
		setEndInterrupt();//nếu hết vòng thì chuyển vòng mới và ngừng quá trình chặt nếu có
	}
	public boolean isFinish(){
		return this.finish;
	}
	private boolean checkFinishBattle(int interrupter_idx){
		switch(finishType){
		case FinishType.SOLE_WINNER:
			return lPlayerCards.get(interrupter_idx) < 1;
		case FinishType.RANKING:
		default:
			return this.finish;
		}
	}
	private boolean checkFinishBattle(){
		switch(finishType){
		case FinishType.SOLE_WINNER:
			return lPlayerCards.get(player_idx) < 1;
		case FinishType.RANKING:
		default:
			return this.finish;
		}
	}
	private synchronized void setFinish(){
		this.trang = false;
		this.finish = true;
	}
	private synchronized void setTrang(){
		this.trang = true;
		this.finish = true;
	}
	private void sendGameData(byte bGameDataType){
		String sTrid = null;
		List<Channel> lc = null;
		if(finish)
			sTrid = lId+"_"+(turn_idx+1);
		else
			sTrid = lId+"_"+turn_idx;
		
		switch(bGameDataType){
		case GameDataType.UPDATE:
			//Gửi tất cả người trong phòng - che bài chưa đánh
			if(UPDATE_GAMEINFO_FULL){
				GameTLMNInfo oUpdateData = (GameTLMNInfo) this.oData.clone();
				for(int j = 0; j < oUpdateData.cardListLength(); j++){
					PlayerTLMNCardInfo pci = oUpdateData.cardList(j);
					if(isRingNew())
						pci.mutateSkipped(0);
					for(int i = 0; i < pci.cardsLength(); i++){
						if(pci.cards(i).turnIdx() <= 0){
							pci.cards(i).mutateCardId(CardID.Card_Hide);
						}
					}
				}
				lc = oRoom.getChannels();
				Service.sendToClient(
						TLMNBattle.class.getSimpleName(), 
						sTrid, Service.CMDTYPE_REQUEST, 
						lc,
						Arrays.asList(CMD.TLMN_UPDATE_BATTLE_INFO.cmd,CMD.TLMN_UPDATE_BATTLE_INFO.subcmd,CMD.TLMN_UPDATE_BATTLE_INFO.version,(byte)0,oUpdateData)							
					);
			}
			else{
				FlatBufferBuilder builder2 = new FlatBufferBuilder(0);
				GameTLMNInfo data = (GameTLMNInfo) this.oData;
				List<Byte> lsc = new ArrayList<>();
				if(!isSkipTurn && lastPlayCards !=null){
					for(Card c : lastPlayCards.getCards()){
						lsc.add(c.Id);
					}
				}
				int irs = CMDGameTLMNUpdate.createCMDGameTLMNUpdate(builder2, 
					oRoom.getId(), 
					this.lId, 
					isSkipTurn?-1:last_player_idx_show_cards, 
					isSkipTurn?0:CMDGameTLMNUpdate.createShowedCardsVector(builder2, ArrayUtils.toPrimitive(lsc.toArray(new Byte[lsc.size()]))), 
					data.playerIdx(), 
					data.turnIdx(), 
					data.ringIdx(),
					GameRoomState.Playing, 
					data.timeRemaining(), 
					last_interrupt_idx==-1?0:GameTLMNInterruptInfo.createGameTLMNInterruptInfo(builder2, 
							data.interruptList(last_interrupt_idx).turnIdx(), 
							data.interruptList(last_interrupt_idx).playerIdx1(), 
							data.interruptList(last_interrupt_idx).coin1(), 
							data.interruptList(last_interrupt_idx).playerIdx2(), 
							data.interruptList(last_interrupt_idx).coin2()
						)
				);
				builder2.finish(irs);
				CMDGameTLMNUpdate oUpdateData = CMDGameTLMNUpdate.getRootAsCMDGameTLMNUpdate(builder2.dataBuffer());
				lc = oRoom.getChannels();
				Service.sendToClient(
						TLMNBattle.class.getSimpleName(), 
						sTrid, Service.CMDTYPE_REQUEST, 
						lc,
						Arrays.asList(CMD.TLMN_UPDATE_BATTLE_INFO_TINY.cmd,CMD.TLMN_UPDATE_BATTLE_INFO_TINY.subcmd,CMD.TLMN_UPDATE_BATTLE_INFO_TINY.version,(byte)0,oUpdateData)							
					);
			}
			break;
		case GameDataType.HIDE:
			//Gửi người chơi - che bài người chơi khác
			Iterator<Long> itPlayers = oRoom.getPlayers().iterator();
			while(itPlayers.hasNext()){
				Long uid = itPlayers.next();
				if(uid!=null && uid > 0){
					Channel c = Handshake.getChannel(uid, ChannelType.Game);
					if(c!=null){
						GameTLMNInfo rs = (GameTLMNInfo) this.oData.clone();
						if(rs.state() == GameRoomState.Playing){
							for(int j = 0; j < rs.cardListLength(); j++){
								PlayerTLMNCardInfo pci = rs.cardList(j);
								if(!uid.equals(pci.playerId())){
									for(int i = 0; i < pci.cardsLength(); i++){
										if(pci.cards(i).turnIdx() <= 0){
											pci.cards(i).mutateCardId(CardID.Card_Hide);
										}
									}
								}
							}
						}
						Service.sendToClient(
								TLMNBattle.class.getSimpleName(), 
								sTrid+"_"+uid, Service.CMDTYPE_REQUEST, 
								Arrays.asList(c),
								Arrays.asList(CMD.TLMN_BATTLE_INFO.cmd,CMD.TLMN_BATTLE_INFO.subcmd,CMD.TLMN_BATTLE_INFO.version,(byte)0,rs)							
							);
					}
				}
			}
			//Gửi người xem - che bài người chơi
			GameTLMNInfo rs = (GameTLMNInfo) this.oData.clone();
			if(rs.state() == GameRoomState.Playing){
				for(int j = 0; j < rs.cardListLength(); j++){
					PlayerTLMNCardInfo pci = rs.cardList(j);
					for(int i = 0; i < pci.cardsLength(); i++){
						if(pci.cards(i).turnIdx() <= 0){
							pci.cards(i).mutateCardId(CardID.Card_Hide);
						}
					}
				}
			}
			lc = oRoom.getViewerChannels();
			Service.sendToClient(
					TLMNBattle.class.getSimpleName(), 
					sTrid+"_Viewers", Service.CMDTYPE_REQUEST, 
					lc,
					Arrays.asList(CMD.TLMN_BATTLE_INFO.cmd,CMD.TLMN_BATTLE_INFO.subcmd,CMD.TLMN_BATTLE_INFO.version,(byte)0,rs)							
				);
			break;
		case GameDataType.CLEAR:
			//Gửi tất cả 
			lc = oRoom.getChannels();
			Service.sendToClient(
					TLMNBattle.class.getSimpleName(), 
					sTrid, Service.CMDTYPE_REQUEST, 
					lc,
					Arrays.asList(CMD.TLMN_BATTLE_INFO.cmd,CMD.TLMN_BATTLE_INFO.subcmd,CMD.TLMN_BATTLE_INFO.version,(byte)0,this.oData)							
				);
			break;
		}
		//lLastAction = System.currentTimeMillis();
	}
	@Override
	public void start(){
		super.start();
		//1. chia bài
		TLMNDeck pc = new TLMNDeck();
		pc.dealing();
		//2. build data
		lPlayerCards = new ArrayList<>();
		lPlayerInRing = new ArrayList<>();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		builder.forceDefaults(true);
		List<Integer> l = new ArrayList<>();
		List<Integer> r = new ArrayList<>();
		Iterator<Card> it = pc.getCards().iterator();
		for(Long uid : lPlayer){
			if(uid != null && uid > 0){
				List<Integer> c = new ArrayList<>();
				int i = 0;
				while(it.hasNext() && i++ < 13){
					c.add(CardTLMNInfo.createCardTLMNInfo(builder, it.next().Id, 0, 0));
				}
				lPlayerCards.add(13); //đánh dấu player đang có 13 lá bài
				l.add(PlayerTLMNCardInfo.createPlayerTLMNCardInfo(builder, 
						uid,
						0,
						PlayerTLMNCardInfo.createCardsVector(builder, ArrayUtils.toPrimitive(c.toArray(new Integer[c.size()])))
					));
				r.add(GameResultInfo.createGameResultInfo(builder, uid, 0, 0));
				lPlayerInRing.add(true);
				JsonObject u = (JsonObject) LocalCache.get(User.USERESOURCE_TABLENAME+uid);
				Long have_coin = u!=null?u.getLong(User.COIN):0l;
				coinList.add(have_coin);
			}
		}
		player_idx = -1; //chưa chỉ định ai đánh trước
		turn_idx = 0;
		ring_idx = 0;
		players_in_ring.set(0);
		this.nextTurn();
		int iData = GameTLMNInfo.createGameTLMNInfo(builder, 
				oRoom.getId(), 
				lId, 
				GameRoomState.Playing,
				timePerTurn,
				player_idx,
				ring_idx,
				turn_idx,
				GameTLMNInfo.createCardListVector(builder, ArrayUtils.toPrimitive(l.toArray(new Integer[l.size()]))), 
				GameTLMNInfo.createResultVector(builder, ArrayUtils.toPrimitive(r.toArray(new Integer[r.size()]))),
				GameTLMNInfo.createInterruptListVector(builder, new int[]{
						GameTLMNInterruptInfo.createGameTLMNInterruptInfo(builder, 0, 0, 0, 0, 0),
						GameTLMNInterruptInfo.createGameTLMNInterruptInfo(builder, 0, 0, 0, 0, 0),
						GameTLMNInterruptInfo.createGameTLMNInterruptInfo(builder, 0, 0, 0, 0, 0),
						GameTLMNInterruptInfo.createGameTLMNInterruptInfo(builder, 0, 0, 0, 0, 0),
						GameTLMNInterruptInfo.createGameTLMNInterruptInfo(builder, 0, 0, 0, 0, 0),
						GameTLMNInterruptInfo.createGameTLMNInterruptInfo(builder, 0, 0, 0, 0, 0)
				}),
				CardID.Card_None
			);
		builder.finish(iData);
		GameTLMNInfo data = GameTLMNInfo.getRootAsGameTLMNInfo(builder.dataBuffer());
	
		//3.1 tìm người đánh trước
		Long firstPlayer = oRoom.getDealer().getFirstPlayer();
		if(firstPlayer != null){
			for(int i = 0 ; i < data.cardListLength(); i++){
				if(firstPlayer.equals(data.cardList(i).playerId())){
					player_idx = i;
					break;
				}
					
			}
		}
		if(player_idx == -1){
			//Nếu phòng mới hoặc người được đánh trước đã rời phòng, tìm người có bài nhỏ nhất
			int cardMinIdx = -1 ;
			int cardMinId = CardID.Card_2_D ;
			for(int i = 0 ; i < data.cardListLength(); i++){
				PlayerTLMNCardInfo c = data.cardList(i);
				for(int j = 0; j < c.cardsLength(); j++)
					if(c.cards(j).cardId() < cardMinId){
						cardMinId = c.cards(j).cardId();
						cardMinIdx = i;
					}
			}
			player_idx = cardMinIdx;
			oRoom.getDealer().setFirstCard(cardMinId != CardID.Card_None?new Card(cardMinId):null);
		}
		//3.2 kiểm tra tới trắng
		boolean isFirstBattleOfRoom = oRoom.getDealer().getFirstCard()!=null || oRoom.getDealer().getFirstPlayer()==null;
		List<TLMNHand> cardList = new ArrayList<>();
		int lenplayers = data.cardListLength();
		for(int j = 0; j < data.cardListLength(); j++){
			int turnScore = player_idx<0?0:lenplayers - (lenplayers+j-player_idx)%lenplayers;
			TLMNHand c = new TLMNHand(data.cardList(j), j, true, isFirstBattleOfRoom?true:false, turnScore);
			if(c.getTrangScore() > 0)
				cardList.add(c);
		}
		int len = cardList.size();
		if(len>0){
			this.setTrang();
			TLMNHand cmax = Collections.max(cardList);
			for(TLMNHand c :  cardList){
				if(cmax.trangLessThan(c))
					cmax = c;
			}
			player_idx = cmax.getPlayerIdx();
			data.mutatePlayerIdx(-1);//Không ai đánh trước
		}
		else{
			newRing();
			data.mutatePlayerIdx(player_idx);
			data.mutateRingIdx(ring_idx);
			Card cardMin = oRoom.getDealer().getFirstCard();
			if(cardMin!=null)
				data.mutateCardMinRequire(cardMin.Id);
			finish = false;
		}
		//4. gửi bài cho người chơi
		this.oData = data;
		this.sendGameData(GameDataType.HIDE);
		lLastAction = System.currentTimeMillis();
	}
	@Override
	public void playing(){
		while(!finish){
			synchronized (this) {
				//1. Chờ người chơi đánh
				try {
					this.wait(timePerTurn);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				GameTLMNInfo data = (GameTLMNInfo) oData;
				//2. Nếu không đánh - server tự đánh hộ hoặc bỏ qua lượt hộ
				int last_turn_idx = turn_idx;
				if(interrupt_period_idx==-1 || interrupt_period_idx==player_idx){
					if(!hasAction){
						//bắt buộc đánh bài bé nhất ra nếu là lượt đánh đầu tiên của vòng
						if(ringNew){
							int cardMin = CardID.Card_2_D;
							int cardMinIdx = 0;
							PlayerTLMNCardInfo pci = data.cardList(player_idx);
							for(int j=0; j<pci.cardsLength(); j++){
								if(pci.cards(j).turnIdx()==0 && pci.cards(j).cardId() < cardMin){
									cardMinIdx = j;
									cardMin = pci.cards(j).cardId();
								}
							}
							pci.cards(cardMinIdx).mutateTurnIdx(turn_idx);
							pci.cards(cardMinIdx).mutateRingIdx(ring_idx);
							this.setShowCardNormal();
							this.setLastPlayCards(Arrays.asList(new TLMNCard(cardMin)), player_idx);
						}
						else{ // bỏ qua vòng đánh
							this.skipRing();
							data.cardList(player_idx).mutateSkipped(1);
						}
					}
					else{
						if(checkFinishBattle()) //nếu người chặt cuối và hết bài => thắng, ngừng quá trình chặt
							setEndInterrupt();
					}
					//3. Chuyển lượt đánh tiếp theo
					if(!checkFinishBattle()){
						this.nextTurn();//nếu hết vòng thì chuyển vòng mới và ngừng quá trình chặt nếu có
						data.mutatePlayerIdx(player_idx);
						data.mutateTurnIdx(turn_idx);
						data.mutateRingIdx(ring_idx);
						data.mutateTimeRemaining(timePerTurn);
					}
					else
						this.setFinish();
					lLastAction = System.currentTimeMillis();
					interrupt_period_idx = -1;
				}
				else{//nếu chặt k cần vòng
					if(checkFinishBattle(interrupt_period_idx)) {
						setEndInterrupt();
						this.setFinish();
					}
					else{
						this.nextTurnInterruptPeriod();
						data.mutateTurnIdx(turn_idx);
						data.mutateTimeRemaining(this.getTimeRemaining());
					}
					interrupt_period_idx = -1;
				}
				//4. Xử lý chặt heo/hàng khi kết thúc quá trình chặt/chặt đè
				if(interrupt_begin && interrupt_end){
					interrupt_begin = false;
					interrupt_end = false;
					last_interrupt_idx = interrupt_idx;
					//4.1 cập nhật game data
					Long coin1 = coinList.get(interrupt_player1_idx);
					Long coin2 = coinList.get(interrupt_player2_idx);
					Long coinBet = interrupt_score * betValue;
					Long coinPen = Collections.min(Arrays.asList(coinBet, coin2));
					GameTLMNInterruptInfo interrupt = data.interruptList(interrupt_idx);
					interrupt.mutateTurnIdx(last_turn_idx);
					interrupt.mutatePlayerIdx1(interrupt_player1_idx);
					interrupt.mutateCoin1(coinPen);
					interrupt.mutatePlayerIdx2(interrupt_player2_idx);
					interrupt.mutateCoin2(-coinPen);	
					
					coinList.set(interrupt_player1_idx, coin1+coinPen);
					coinList.set(interrupt_player2_idx, coin2-coinPen);
					//4.2 kết thúc vòng chặt
					this.finishInterrupt();
				}
				else
					last_interrupt_idx = -1;
				//5. gửi dataupdate nếu chưa kết thúc ván
				if(!checkFinishBattle())
					this.sendGameData(GameDataType.UPDATE);
			}
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public void finish(){
		GameTLMNInfo data = (GameTLMNInfo) oData;
		//1. tính điểm thắng thua - chia tiền
		int winner_idx = player_idx;
		data.result(winner_idx).mutateRank(ResultType.NHAT);
		Double win_coin = 0d;
		for(int i = 0; i < data.cardListLength(); i++){
			if(i != winner_idx){
				data.result(i).mutateRank(ResultType.BET);
				PlayerTLMNCardInfo pc = data.cardList(i);
				List<TLMNCard> l = new ArrayList<>();
				for(int j = 0; j < pc.cardsLength(); j++)
					if(pc.cards(j).turnIdx() == 0)
						l.add(new TLMNCard(pc.cards(j).cardId(), j));
				TLMNHand c = new TLMNHand();
				c.setCards(l);
				//đánh dấu các lá bài thối
				List<?> pen = c.getFinishPenanceScore(trang);
				List<Card> p = (List<Card>) pen.get(1);
				for(Card c1 : p){
					pc.cards(c1.Index).mutateTurnIdx(-1);
				}
				//tính tiền: đếm lá + thối
				Long lose_coin = (Integer)pen.get(0) * betValue;
				Long have_coin = coinList.get(i);
				if(lose_coin > have_coin)
					lose_coin = have_coin;
				data.result(i).mutateGold(-lose_coin.intValue());
				win_coin += lose_coin;
			}
		}
		data.result(winner_idx).mutateGold(win_coin.intValue());
		
		data.mutatePlayerIdx(-1);//Không ai đánh nữa
		data.mutateState(GameRoomState.Finished);
		data.mutateTimeRemaining(0);//getTimeRemaining());
		oRoom.getDealer().setFirstPlayer(data.cardList(winner_idx).playerId()); //báo Bồi bàn người được đánh trước ván sau
		oRoom.getDealer().setFirstCard(null);
		//3. gửi kết quả cho người chơi trong phòng (cả ng chờ và ng chơi)
		this.sendGameData(GameDataType.CLEAR);
		//4. chờ kết thúc quá trình show bài của all player
		synchronized (this) {
			try {
				this.wait(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//5. call super xử lý kết thúc ván bài: save, log, push roomupdateinfo
		super.finish();
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		GameTLMNInfo data = (GameTLMNInfo) this.oData;
		for(int j = 0; j < data.resultLength(); j++){
			GameResultInfo rs = data.result(j);
			lResult.add(rs);
			if(rs.rank()==1)
				mExp.put(rs.playerId(), LevelExp.getExp(this.trang?ExpType.Special:ExpType.Winner));
			else
				mExp.put(rs.playerId(), LevelExp.getExp(ExpType.Loser));
		}
		for(int j = 0; j < data.interruptListLength(); j++){
			GameTLMNInterruptInfo i = data.interruptList(j);
			if(i.turnIdx()>0){
				GameResultInfo rs1 = data.result(i.playerIdx1());
				Long player1_coin = mResultBonus.get(rs1.playerId());
				if(player1_coin==null) player1_coin = 0l;
				mResultBonus.put(rs1.playerId(), player1_coin+i.coin1());
				Integer player1_exp = mExp.get(rs1.playerId());
				if(player1_exp==null) player1_exp = 0;
				mExp.put(rs1.playerId(), player1_exp+LevelExp.getExp(ExpType.Bonus));
				
				GameResultInfo rs2 = data.result(i.playerIdx2());
				Long player2_coin = mResultPenance.get(rs2.playerId());
				if(player2_coin==null) player2_coin = 0l;
				mResultPenance.put(rs2.playerId(), player2_coin+i.coin2());
				Integer player2_exp = mExp.get(rs2.playerId());
				if(player2_exp==null) player2_exp = 0;
				mExp.put(rs2.playerId(), player2_exp+LevelExp.getExp(ExpType.Penance));
			}
		}
		super.save();
	}
}
