package vn.com.vng.gsmobile.casino.entries.maubinh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
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
import vn.com.vng.gsmobile.casino.flatbuffers.CardID;
import vn.com.vng.gsmobile.casino.flatbuffers.GameMBInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameMBResultDetail;
import vn.com.vng.gsmobile.casino.flatbuffers.GameResultInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.flatbuffers.MBResultStatus;
import vn.com.vng.gsmobile.casino.flatbuffers.MBShowType;
import vn.com.vng.gsmobile.casino.flatbuffers.MauBinhType;
import vn.com.vng.gsmobile.casino.flatbuffers.PlayerMBCardInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.RankType;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class MBBattle extends Battle {
	private static final long STAGE_SHOW_TIME_DEFAULT = 5000;
	private static final long FINISH_SHOW_TIME_DELAY = 2000;
	List<List<Number>> showTypeList = new ArrayList<>();
	List<Number> showTinyList = new ArrayList<>();
	List<Boolean> finishShowList = new ArrayList<>();
	TreeMap<Integer, MBHand> binhLungList = new TreeMap<>();
	TreeMap<Integer, MBHand> mauBinhList = new TreeMap<>();
	TreeMap<Integer, MBHand> soChiList = new TreeMap<>();
	List<Long> coinList = new ArrayList<>();
	List<List<Long>> soChiTotal = new ArrayList<>();
	int hostIdx = 0;
	long showTime = 0;
	@Override
	public void start() {
		// TODO Auto-generated method stub
		super.start();
		Long hoster = oRoom.getHostId();
		//1. chia bài
		MBDeck deck = new MBDeck();
		deck.dealing();
		//2. build data
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		builder.forceDefaults(true);
		List<Integer> l = new ArrayList<>();
		List<Integer> r = new ArrayList<>();
		List<Integer> rd = new ArrayList<>();
		Iterator<Card> it = deck.getCards().iterator();
		for(int idx = 0; idx < lPlayer.size(); idx++){
			Long uid = lPlayer.get(idx);
			if(uid != null && uid > 0){
				List<Byte> c = new ArrayList<>();
				int i = 0;
				while(it.hasNext() && i++ < 13){
					c.add(it.next().Id);
				}
				l.add(PlayerMBCardInfo.createPlayerMBCardInfo(builder, 
						uid,
						PlayerMBCardInfo.createCardsVector(builder, ArrayUtils.toPrimitive(c.toArray(new Byte[c.size()]))),
						PlayerMBCardInfo.createChiTypeVector(builder, new byte[]{MauBinhType.None, MauBinhType.None, MauBinhType.None}),
						MauBinhType.None,
						MBShowType.None
					));
				r.add(GameResultInfo.createGameResultInfo(builder, uid, 0, RankType.RankNone));
				rd.add(GameMBResultDetail.createGameMBResultDetail(builder, 0, 0, 0, 0, 0, 0, 0, MBResultStatus.Normal));
				List<Number> s = new ArrayList<>();
				s.add(MBShowType.None);
				s.add(0l);
				showTypeList.add(s);
				showTinyList.add(0);
				finishShowList.add(false);
				if(uid==hoster)
					hostIdx = idx;
			}
		}
		int iData = GameMBInfo.createGameMBInfo(builder, 
				oRoom.getId(), 
				lId, 
				GameRoomState.Playing,
				timePerTurn,
				System.currentTimeMillis(),
				GameMBInfo.createCardListVector(builder, ArrayUtils.toPrimitive(l.toArray(new Integer[l.size()]))), 
				GameMBInfo.createResultVector(builder, ArrayUtils.toPrimitive(r.toArray(new Integer[r.size()]))),
				GameMBInfo.createResultDetailVector(builder, ArrayUtils.toPrimitive(rd.toArray(new Integer[rd.size()])))
			);
		builder.finish(iData);
		GameMBInfo data = GameMBInfo.getRootAsGameMBInfo(builder.dataBuffer());
		//3. kiểm tra mậu binh
		for(int j = 0; j < data.cardListLength(); j++){
			PlayerMBCardInfo pc = data.cardList(j);
			MBHand h = new MBHand(pc);
			if(h.isMauBinh()){
				pc.mutateHandType(h.getType());
				List<MBCard> lc = h.getCards();
				for(int i =0; i < lc.size(); i++){
					pc.mutateCards(i, lc.get(i).Id);
				}
			}
		}
		//4. gửi bài
		this.oData = data;
		this.sendGameData(GameDataType.HIDE);
	}
	@Override
	public void playing() {
		// TODO Auto-generated method stub
		//1. Chờ xếp bài
		synchronized (this) {
			try {
				this.wait(timePerTurn);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//2. Chờ gửi bài
		if(!isShowAll())
			synchronized (this) {
				try {
					this.wait(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		//3. Chờ 1 giây rồi so bài
		synchronized (this) {
			try {
				this.wait(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		GameMBInfo data = (GameMBInfo) oData;
		//1. tính điểm - phân loại
		int lenplayers = data.cardListLength();
		for(int j = 0; j < lenplayers; j++){
			//Tính điểm
			PlayerMBCardInfo pc = data.cardList(j);
			MBHand h = new MBHand(pc, (byte)(j==hostIdx?1:0));
			h.setPlayerIdx(j);
			pc.mutateHandType(h.getType());
			pc.mutateChiType(0, h.getChi(HandType.CHI1).getType());
			pc.mutateChiType(1, h.getChi(HandType.CHI2).getType());
			pc.mutateChiType(2, h.getChi(HandType.CHI3).getType());
			List<MBCard> lc  = new ArrayList<>();
			lc.addAll(h.getChi(HandType.CHI1).getCards());
			lc.addAll(h.getChi(HandType.CHI2).getCards());
			lc.addAll(h.getChi(HandType.CHI3).getCards());
			for(int i =0; i < lc.size(); i++){
				pc.mutateCards(i, lc.get(i).Id);
			}
			//Phân loại
			byte showType = showTypeList.get(j).get(0).byteValue();
			int turnScore = (lenplayers+j-hostIdx)%lenplayers;
			if(h.isMauBinh()){
				if(showType!=MBShowType.So_Chi)
					mauBinhList.put(turnScore, h);
				else{
					if(h.getChi(HandType.CHI1).compareTo(h.getChi(HandType.CHI2))>=0 
					&& h.getChi(HandType.CHI2).compareTo(h.getChi(HandType.CHI3))>=0)
						soChiList.put(turnScore, h);
					else
						binhLungList.put(turnScore, h);
				}
			}
			else if(h.isBinhLung())
				binhLungList.put(turnScore, h);
			else
				soChiList.put(turnScore, h);
			//Khởi tạo dữ liệu đệm
			List<Long> l = new ArrayList<>();
			for(int i=0; i < lenplayers; i++)
				l.add(0l);
			soChiTotal.add(l);
			//Lấy coin hiện tại của user
			JsonObject u = (JsonObject) LocalCache.get(User.USERESOURCE_TABLENAME+data.result(j).playerId());
			Long have_coin = u!=null?u.getLong(User.COIN):0l;
			coinList.add(have_coin);
		}
		calculateBinhLung();
		calculateMauBinh();
		if(soChiList.size()>1){
			calculateSoChi(HandType.CHI1);
			calculateSoChi(HandType.CHI2);
			calculateSoChi(HandType.CHI3);
			calculateSapHo();
		}
		if(soChiList.size()>2){
			calculateSapLang();
		}
		calculateTong();
		//2. gửi kết quả cho người chơi trong phòng (cả ng chờ và ng chơi)
		data.mutateState(GameRoomState.Finished);
		data.mutateTimeRemaining(0);
		this.sendGameData(GameDataType.CLEAR);
		//3. chờ kết thúc quá trình show bài của all player
		synchronized (this) {
			try {
				this.wait(this.showTime+FINISH_SHOW_TIME_DELAY);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//4. call super xử lý kết thúc ván bài: save, log, push roomupdateinfo
		super.finish();
		//5. chuyển quyền chia bài cho người tiếp theo
		oRoom.getDealer().setNextHostIdx();
	}
	@Override
	public void save() {
		// TODO Auto-generated method stub
		GameMBInfo data = (GameMBInfo) this.oData;
		for(int j = 0; j < data.resultLength(); j++){
			GameResultInfo rs = data.result(j);
			lResult.add(rs);
			if(rs.rank()==1)
				mExp.put(rs.playerId(), LevelExp.getExp(ExpType.Winner));
			else
				mExp.put(rs.playerId(), LevelExp.getExp(ExpType.Loser));
		}
		super.save();
	}
	private void sendGameData(byte bGameDataType){
		String sTrid = lId.toString();
		List<Channel> lc = null;
		GameMBInfo rs = null;
		switch(bGameDataType){
		case GameDataType.UPDATE:
		case GameDataType.HIDE:
			//Gửi người chơi - che bài người chơi khác
			Iterator<Long> itPlayers = oRoom.getPlayers().iterator();
			while(itPlayers.hasNext()){
				Long uid = itPlayers.next();
				if(uid!=null && uid > 0){
					Channel c = Handshake.getChannel(uid, ChannelType.Game);
					if(c!=null){
						rs = (GameMBInfo) this.oData.clone();
						if(rs.state() == GameRoomState.Playing){
							for(int j = 0; j < rs.cardListLength(); j++){
								PlayerMBCardInfo pci = rs.cardList(j);
								if(!uid.equals(pci.playerId())){
									for(int i = 0; i < pci.cardsLength(); i++){
										pci.mutateCards(i, CardID.Card_Hide);
									}
								}
							}
						}
						rs.mutateTransTime(System.currentTimeMillis());
						Service.sendToClient(
								MBBattle.class.getSimpleName(), 
								sTrid+"_"+uid, Service.CMDTYPE_REQUEST, 
								Arrays.asList(c),
								Arrays.asList(CMD.MB_BATTLE_INFO.cmd,CMD.MB_BATTLE_INFO.subcmd,CMD.MB_BATTLE_INFO.version,(byte)0,rs)							
							);
					}
				}
			}
			//Gửi người xem - che bài người chơi
			rs = (GameMBInfo) this.oData.clone();
			if(rs.state() == GameRoomState.Playing){
				for(int j = 0; j < rs.cardListLength(); j++){
					PlayerMBCardInfo pci = rs.cardList(j);
					for(int i = 0; i < pci.cardsLength(); i++){
						pci.mutateCards(i, CardID.Card_Hide);
					}
				}
			}
			lc = oRoom.getViewerChannels();
			rs.mutateTransTime(System.currentTimeMillis());
			Service.sendToClient(
					MBBattle.class.getSimpleName(), 
					sTrid+"_Viewers", Service.CMDTYPE_REQUEST, 
					lc,
					Arrays.asList(CMD.MB_BATTLE_INFO.cmd,CMD.MB_BATTLE_INFO.subcmd,CMD.MB_BATTLE_INFO.version,(byte)0,rs)							
				);
			break;
		case GameDataType.CLEAR:
			//Gửi tất cả 
			rs = (GameMBInfo) this.oData;
			rs.mutateTransTime(System.currentTimeMillis());
			lc = oRoom.getChannels();
			Service.sendToClient(
					MBBattle.class.getSimpleName(), 
					sTrid, Service.CMDTYPE_REQUEST, 
					lc,
					Arrays.asList(CMD.MB_BATTLE_INFO.cmd,CMD.MB_BATTLE_INFO.subcmd,CMD.MB_BATTLE_INFO.version,(byte)0,this.oData)							
				);
			break;
		}
		lLastAction = System.currentTimeMillis();
	}
	
	public List<Number> getShowType(int playerIdx){
		return showTypeList.get(playerIdx);
	}
	public synchronized void setShowType(int playerIdx, int showType, long showId){
		List<Number> b = showTypeList.get(playerIdx);
		b.set(0, showType);
		b.set(1, showId);
	}
	public Number getShowTiny(int playerIdx){
		return showTinyList.get(playerIdx);
	}
	public synchronized void setShowTiny(int playerIdx, long showId){
		showTinyList.set(playerIdx, showId);
	}
	public boolean isShowAll(){
		for(List<Number> b : showTypeList)
			if(b.get(0).byteValue()==MBShowType.None)
				return false;
		return true;
	}
	public synchronized void setFinishShow(int playerIdx){
		finishShowList.set(playerIdx, true);
	}
	public boolean isFinishShowAll(){
		for(Boolean b : finishShowList)
			if(!b)
				return false;
		return true;
	}
	private void calculateBinhLung(){
		GameMBInfo data = (GameMBInfo) oData;
		Long coinBet = MBRule.getPenanceScore(MauBinhType.BinhLung)*betValue;
		TreeMap<Integer, MBHand> thangList = new TreeMap<>();
		thangList.putAll(mauBinhList);
		thangList.putAll(soChiList);
		Iterator<Entry<Integer, MBHand>> itThua = binhLungList.entrySet().iterator();
		while(itThua.hasNext()){
			Entry<Integer, MBHand> eThua = itThua.next();
			MBHand hThua = eThua.getValue();
			Long coinThua = coinList.get(hThua.getPlayerIdx());
			GameMBResultDetail rsThua = data.resultDetail(hThua.getPlayerIdx()); 
			Iterator<Entry<Integer, MBHand>> itThang = thangList.entrySet().iterator();
			while(itThang.hasNext()){
				Entry<Integer, MBHand> eThang = itThang.next();
				MBHand hThang = eThang.getValue();
				Long coinThang = coinList.get(hThang.getPlayerIdx());
				Long coin = Collections.min(Arrays.asList(coinBet, coinThua, coinThang));
				coinThua -= coin;
				coinThang += coin;
				rsThua.mutateGoldBinhLung(-coin+rsThua.goldBinhLung());
				GameMBResultDetail rsThang = data.resultDetail(hThang.getPlayerIdx());
				rsThang.mutateGoldBinhLung(coin+rsThang.goldBinhLung());
				coinList.set(hThang.getPlayerIdx(), coinThang);
			}
			coinList.set(hThua.getPlayerIdx(), coinThua);
			rsThua.mutateResultStatus(MBResultStatus.BinhLung);
		}
		if(binhLungList.size()>0)
			showTime += STAGE_SHOW_TIME_DEFAULT;
	}
	private void calculateMauBinh(){
		GameMBInfo data = (GameMBInfo) oData;
		TreeMap<Integer, MBHand> thangList = mauBinhList;
		Iterator<Entry<Integer, MBHand>> itThang = thangList.entrySet().iterator();
		while(itThang.hasNext()){
			Entry<Integer, MBHand> eThang = itThang.next();
			MBHand hThang = eThang.getValue();
			Long coinThang = coinList.get(hThang.getPlayerIdx());
			GameMBResultDetail rsThang = data.resultDetail(hThang.getPlayerIdx());
			Long coinBet = MBRule.getPenanceScore(hThang.getType())*betValue;
			
			TreeMap<Integer, MBHand> thuaList = new TreeMap<>();
			thuaList.putAll(binhLungList);
			thuaList.putAll(soChiList);
			Iterator<Entry<Integer, MBHand>> itThang2 = thangList.entrySet().iterator();
			while(itThang2.hasNext()){
				Entry<Integer, MBHand> eThang2 = itThang2.next(); 
				MBHand hThang2 = eThang2.getValue();
				if(hThang2!=hThang && hThang2.compareTo(hThang)<0)
					thuaList.put(eThang2.getKey(), hThang2);
			}
			Iterator<Entry<Integer, MBHand>> itThua = thuaList.entrySet().iterator();
			while(itThua.hasNext()){
				Entry<Integer, MBHand> eThua = itThua.next();
				MBHand hThua = eThua.getValue();
				Long coinThua = coinList.get(hThua.getPlayerIdx());
				Long coin = Collections.min(Arrays.asList(coinBet, coinThua, coinThang));
				coinThua -= coin;
				coinThang += coin;
				GameMBResultDetail rsThua = data.resultDetail(hThua.getPlayerIdx()); 
				rsThua.mutateGoldMauBinh(-coin+rsThua.goldMauBinh());
				rsThang.mutateGoldMauBinh(coin+rsThang.goldMauBinh());
				coinList.set(hThua.getPlayerIdx(), coinThua);
			}
			coinList.set(hThang.getPlayerIdx(), coinThang);
			rsThang.mutateResultStatus(MBResultStatus.MauBinh);
		}
		if(mauBinhList.size()>0)
			showTime += STAGE_SHOW_TIME_DEFAULT;
	}
	private void calculateSoChi(byte chi) {
		// TODO Auto-generated method stub
		GameMBInfo data = (GameMBInfo) oData;
		Iterator<Entry<Integer, MBHand>> itSoChi = soChiList.entrySet().iterator();
		while(itSoChi.hasNext()){
			Entry<Integer, MBHand> eSoChi = itSoChi.next();
			MBHand hSoChi = eSoChi.getValue();
		
			NavigableMap<Integer, MBHand> soChi2List = soChiList.tailMap(eSoChi.getKey(), false);
			if(soChi2List!=null && !soChi2List.isEmpty()){
				Iterator<Entry<Integer, MBHand>> itSoChi2 = soChi2List.entrySet().iterator();
				while(itSoChi2.hasNext()){
					Entry<Integer, MBHand> eSoChi2 = itSoChi2.next();
					MBHand hSoChi2 = eSoChi2.getValue();
					
					MBHand hThua = null;
					MBHand hThang = null;
					int compare = hSoChi2.getChi(chi).compareTo(hSoChi.getChi(chi));
					if(compare > 0){
						hThua = hSoChi;
						hThang = hSoChi2;
					}
					else if(compare < 0){
						hThua = hSoChi2;
						hThang = hSoChi;							
					}
					Long coinThua = coinList.get(hThua.getPlayerIdx());
					Long coinThang = coinList.get(hThang.getPlayerIdx());
					Long coinBet = betValue * MBRule.getPenanceScore(hThang.getChi(chi).getType());
					Long coin = Collections.min(Arrays.asList(coinBet, coinThua, coinThang));
					coinThua -= coin;
					coinThang += coin;
					GameMBResultDetail rsThua = data.resultDetail(hThua.getPlayerIdx()); 
					GameMBResultDetail rsThang = data.resultDetail(hThang.getPlayerIdx());
					switch (chi) {
					case HandType.CHI1:
						rsThua.mutateGoldChi1(-coin+rsThua.goldChi1());
						rsThang.mutateGoldChi1(coin+rsThang.goldChi1());
						break;
					case HandType.CHI2:
						rsThua.mutateGoldChi2(-coin+rsThua.goldChi2());
						rsThang.mutateGoldChi2(coin+rsThang.goldChi2());
						break;
					case HandType.CHI3:
						rsThua.mutateGoldChi3(-coin+rsThua.goldChi3());
						rsThang.mutateGoldChi3(coin+rsThang.goldChi3());
						break;
					}
					coinList.set(hThua.getPlayerIdx(), coinThua);
					coinList.set(hThang.getPlayerIdx(), coinThang);
					
					List<Long> l1 = soChiTotal.get(hThang.getPlayerIdx());
					l1.set(hThua.getPlayerIdx(), -coin+l1.get(hThua.getPlayerIdx()));
					l1.set(hThang.getPlayerIdx(), coin+l1.get(hThang.getPlayerIdx()));
					soChiTotal.set(hThang.getPlayerIdx(), l1);
					
					List<Long> l2 = soChiTotal.get(hThua.getPlayerIdx());
					l2.set(hThua.getPlayerIdx(), -coin+l2.get(hThua.getPlayerIdx()));
					l2.set(hThang.getPlayerIdx(), coin+l2.get(hThang.getPlayerIdx()));
					soChiTotal.set(hThua.getPlayerIdx(), l2);
				}
			}
		}
		if(soChiList.size()>1)
			showTime += STAGE_SHOW_TIME_DEFAULT;
	}
	
	private void calculateSapHo() {
		// TODO Auto-generated method stub
		GameMBInfo data = (GameMBInfo) oData;
		boolean isShowSapHo = false;
		Iterator<Entry<Integer, MBHand>> itSoChi = soChiList.entrySet().iterator();
		while(itSoChi.hasNext()){
			Entry<Integer, MBHand> eSoChi = itSoChi.next();
			MBHand hSoChi = eSoChi.getValue();
		
			NavigableMap<Integer, MBHand> soChi2List = soChiList.tailMap(eSoChi.getKey(), false);
			if(soChi2List!=null && !soChi2List.isEmpty()){
				Iterator<Entry<Integer, MBHand>> itSoChi2 = soChi2List.entrySet().iterator();
				while(itSoChi2.hasNext()){
					Entry<Integer, MBHand> eSoChi2 = itSoChi2.next();
					MBHand hSoChi2 = eSoChi2.getValue();
					
					MBHand hThua = null;
					MBHand hThang = null;
					int compare1 = hSoChi.getChi(HandType.CHI1).compareTo(hSoChi2.getChi(HandType.CHI1));
					int compare2 = hSoChi.getChi(HandType.CHI2).compareTo(hSoChi2.getChi(HandType.CHI2));
					int compare3 = hSoChi.getChi(HandType.CHI3).compareTo(hSoChi2.getChi(HandType.CHI3));
					if(compare1 > 0 && compare2 > 0 && compare3 > 0){
						hThua = hSoChi2;
						hThang = hSoChi;
						isShowSapHo = true;
					}
					else if(compare1 < 0 && compare2 < 0 && compare3 < 0){
						hThua = hSoChi;
						hThang = hSoChi2;
						isShowSapHo = true;
					}
					if(hThua!=null && hThang!=null){
						Long coinThua = coinList.get(hThua.getPlayerIdx());
						Long coinThang = coinList.get(hThang.getPlayerIdx());
						Long coinBet = Math.abs(soChiTotal.get(hThang.getPlayerIdx()).get(hThua.getPlayerIdx()));
						Long coin = Collections.min(Arrays.asList(coinBet, coinThua, coinThang));
						coinThua -= coin;
						coinThang += coin;
						GameMBResultDetail rsThua = data.resultDetail(hThua.getPlayerIdx()); 
						GameMBResultDetail rsThang = data.resultDetail(hThang.getPlayerIdx());
						rsThua.mutateGoldSapHo(-coin+rsThua.goldSapHo());
						rsThang.mutateGoldSapHo(coin+rsThang.goldSapHo());
						coinList.set(hThua.getPlayerIdx(), coinThua);
						coinList.set(hThang.getPlayerIdx(), coinThang);
						
						rsThua.mutateResultStatus(MBResultStatus.SapBaChi);
						if(rsThang.resultStatus()!=MBResultStatus.SapBaChi)
							rsThang.mutateResultStatus(MBResultStatus.BatSapBaChi);
					}
				}
			}
		}
		if(isShowSapHo)
			showTime += STAGE_SHOW_TIME_DEFAULT;
	}
	
	private void calculateSapLang() {
		// TODO Auto-generated method stub
		GameMBInfo data = (GameMBInfo) oData;
		MBHand batSapLang = null;
		for(MBHand h1: soChiList.values()){
			boolean isBatSapLang = true;
			for(MBHand h2: soChiList.values()){
				if(h1!=h2){
					int c1 = h1.getChi(HandType.CHI1).compareTo(h2.getChi(HandType.CHI1));
					int c2 = h1.getChi(HandType.CHI2).compareTo(h2.getChi(HandType.CHI2));
					int c3 = h1.getChi(HandType.CHI3).compareTo(h2.getChi(HandType.CHI3));
					if(c1 <= 0 || c2 <= 0 || c3 <=0){
						isBatSapLang = false;
						break;
					}
				}
			}	
			if(isBatSapLang){
				batSapLang = h1;
				break;
			}
		}
		if(batSapLang!=null){
			Long coinBet = ((Double)(betValue * MBRule.getSapLangPenanceScore(soChiList.size()))).longValue();
			GameMBResultDetail rsThang = data.resultDetail(batSapLang.getPlayerIdx());
			Long coinThang = coinList.get(batSapLang.getPlayerIdx());
			for(MBHand h : soChiList.values()){
				if(h!=batSapLang){
					GameMBResultDetail rsThua = data.resultDetail(h.getPlayerIdx());
					Long coinThua = coinList.get(h.getPlayerIdx());
					Long coin = Collections.min(Arrays.asList(coinBet, coinThua, coinThang));
					coinThua -= coin;
					coinThang += coin;
					rsThua.mutateGoldSapLang(-coin+rsThua.goldSapLang());
					rsThang.mutateGoldSapLang(coin+rsThang.goldSapLang());
					coinList.set(h.getPlayerIdx(), coinThua);
					rsThua.mutateResultStatus(MBResultStatus.SapBaChi);
				}
			}
			coinList.set(batSapLang.getPlayerIdx(), coinThang);
			rsThang.mutateResultStatus(MBResultStatus.BatSapLang);
			showTime += STAGE_SHOW_TIME_DEFAULT;
		}
	}

	private void calculateTong() {
		// TODO Auto-generated method stub
		GameMBInfo data = (GameMBInfo) oData;
		for(int j=0; j < data.resultLength(); j++){
			GameResultInfo rs = data.result(j);
			GameMBResultDetail rsd = data.resultDetail(j);
			Long gold = rsd.goldBinhLung()+rsd.goldMauBinh()+rsd.goldChi1()+rsd.goldChi2()+rsd.goldChi3()+rsd.goldSapHo()+rsd.goldSapLang();
			int rank = gold >= 0?ResultType.NHAT:ResultType.BET;
			rs.mutateGold(gold);
			rs.mutateRank(rank);
		}
		showTime += STAGE_SHOW_TIME_DEFAULT;
	}
}
