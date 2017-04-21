package vn.com.vng.gsmobile.casino.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.google.flatbuffers.FlatBufferBuilder;
import vn.com.vng.gsmobile.casino.entries.Card;
import vn.com.vng.gsmobile.casino.entries.Event;
import vn.com.vng.gsmobile.casino.entries.ExpType;
import vn.com.vng.gsmobile.casino.entries.GameConfig;
import vn.com.vng.gsmobile.casino.entries.Handshake;
import vn.com.vng.gsmobile.casino.entries.LevelExp;
import vn.com.vng.gsmobile.casino.entries.Deck;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.entries.Shop;
import vn.com.vng.gsmobile.casino.entries.bala.CaoHand;
import vn.com.vng.gsmobile.casino.entries.tlmn.TLMNHand;
import vn.com.vng.gsmobile.casino.entries.tlmn.TLMNDLRule;
import vn.com.vng.gsmobile.casino.flatbuffers.Card3LaInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.CardID;
import vn.com.vng.gsmobile.casino.flatbuffers.GameTLMNInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameType;
import vn.com.vng.gsmobile.casino.flatbuffers.LobbyRoom;
import vn.com.vng.gsmobile.casino.flatbuffers.LobbyType;
import vn.com.vng.gsmobile.casino.flatbuffers.Player3LaCardInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfo;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class testAll {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		RoomManager.init();
//		LobbyRoom rs = null;
//		FlatBufferBuilder builder = new FlatBufferBuilder(0);
//		List<Integer> l = new ArrayList<>();
//		for(Room r : RoomManager.getRoomList(LobbyType.NongDan, GameType.BALA)){
//			l.add(r.toRoomInfo(builder));
//		}
//		int ilr = LobbyRoom.createLobbyRoom(builder, 
//				LobbyType.NongDan, 
//				GameType.BALA, 
//				LobbyRoom.createRoomListVector(builder, ArrayUtils.toPrimitive(l.toArray(new Integer[l.size()])))
//			);
//		builder.finish(ilr);
//		rs = LobbyRoom.getRootAsLobbyRoom(builder.dataBuffer());
//		System.out.println(rs);
//		TreeMap<Long, String> a = new TreeMap<>();
//		a.put(1000l, "1000");
//		a.put(2000l, "1000");
//		a.put(3000l, "1000");
//		a.put(4000l, "1000");
//		a.put(5000l, "1000");
//		a.put(6000l, "1000");
//		a.put(7000l, "1000");
//		a.put(8000l, "1000");
//		System.out.println(a.subMap(0l, 6500l));
//		System.out.println(a.subMap(7000l, 850000l));
//		System.out.println(a.subMap(1000l, 6500l).tailMap(3000l));
//		System.out.println(GameConfig.load(false).getObject(GameConfig.GAMELIST).getObject("3").getLong(GameConfig.TIME_INITBATTLE));
//
//		FlatBufferBuilder builder = new FlatBufferBuilder(0);
//		builder.finish(Player3LaCardInfo.createPlayer3LaCardInfo(builder, 
//				builder.createString("123456"), 
//				Player3LaCardInfo.createCardsVector(builder, new int[]{
//						Card3LaInfo.createCard3LaInfo(builder, CardID.Card_J_A, 0),
//						Card3LaInfo.createCard3LaInfo(builder, CardID.Card_A_A, 0),
//						Card3LaInfo.createCard3LaInfo(builder, CardID.Card_7_C, 0),
//				})
//			)
//		);
//		Player3LaCardInfo c = Player3LaCardInfo.getRootAsPlayer3LaCardInfo(builder.dataBuffer());
//		Cards3La v = new Cards3La(c, 0);
//		System.out.println(v);
//		FlatBufferBuilder builder1 = new FlatBufferBuilder(0);
//		builder1.finish(Player3LaCardInfo.createPlayer3LaCardInfo(builder1, 
//				builder1.createString("123456"), 
//				Player3LaCardInfo.createCardsVector(builder1, new int[]{
//						Card3LaInfo.createCard3LaInfo(builder1, CardID.Card_K_D, 0),
//						Card3LaInfo.createCard3LaInfo(builder1, CardID.Card_7_A, 0),
//						Card3LaInfo.createCard3LaInfo(builder1, CardID.Card_Q_B, 0),
//				})
//			)
//		);
//		Player3LaCardInfo c1 = Player3LaCardInfo.getRootAsPlayer3LaCardInfo(builder1.dataBuffer());
//		Cards3La v1 = new Cards3La(c1, 1);
//		System.out.println(v1);
//		
//		System.out.println(v.compareTo(v1));
//		
//		List<Cards3La> lRs = Arrays.asList(v, v1);
//		Collections.sort(lRs);
//		System.out.println(lRs);
		
    	//tool log
//		UserInfo u3 = Lib.parseLogToFlatBuffers(Arrays.asList(
//    				"vn.com.vng.gsmobile.casino.flatbuffers.UserInfo",
//    				"1c0000000000160008000400000000000000000000000000000000001600000004000000120000006631303230313332353938393232383938330000",
//    				0
//    			));
//    	System.out.println(u3.toString());
//		System.out.println(Arrays.asList("1,2,".split(",",-1)));
//		CardsTLMNDL c = new CardsTLMNDL();
//		c.setCards(Arrays.asList(
//			new Card(CardID.Card_10_D),
//			new Card(CardID.Card_3_C),
//			new Card(CardID.Card_3_A),
//			new Card(CardID.Card_5_B),
//			new Card(CardID.Card_5_D),
//			new Card(CardID.Card_4_A),
////			new Card(CardID.Card_9_C),
//			new Card(CardID.Card_9_D),
//			new Card(CardID.Card_7_A),
//			new Card(CardID.Card_7_B),
////			new Card(CardID.Card_2_B),
//			new Card(CardID.Card_8_D),
//			new Card(CardID.Card_8_B)
//		));
//		System.out.println(c);
//		Long start = System.currentTimeMillis();//System.nanoTime();
//		System.out.println(c.getFinishPenanceScore(false));
//		Long finish = System.currentTimeMillis();//System.nanoTime();
//		//System.out.println((finish-start)/1000000d);
//		System.out.println(finish-start);
//		
//		start = System.currentTimeMillis();//System.nanoTime();
//		RuleTLMNDL.getPenanceScore("Card_2_A");
//		finish = System.currentTimeMillis();//System.nanoTime();
//		//System.out.println((finish-start)/1000000d);
//		System.out.println(finish-start);
//		
//		start = System.currentTimeMillis();//System.nanoTime();
//		RuleTLMNDL.getPenanceScore(CardID.name(CardID.Card_2_A));
//		finish = System.currentTimeMillis();//System.nanoTime();
//		//System.out.println((finish-start)/1000000d);
//		System.out.println(finish-start);
//		List<String> l = null;
//		for(String s : l){
//			System.out.println(s);
//		}
//		System.out.println(Shop.buildShopList("123456"));
//		System.out.println(Shop.getShopList("123456"));
//		Event.getEventBase();
//		Shop.getShopBase();
//		System.out.println(Shop.buildPolicyShop("1234567"));
//		System.out.println(Shop.getPolicyShop("1234567"));
//		System.out.println(Shop.getPolicyShop("1234567", "102"));
//		System.out.println(Event.getEventList());
//		System.out.println(Lib.md5("vudeptrai"));
//		System.out.println(String.format("%d", null));`
//		System.out.println(LevelExp.getExp(ExpType.Penance));
//		System.out.println(LevelExp.getExp((byte)5));
//		System.out.println(Lib.getNanoTimeId());
//		System.out.println(Lib.getNanoTimeId());
//		System.out.println(Lib.getNanoTimeId());
//		System.out.println(Lib.getNanoTimeId());
//		int i  = RandomUtils.nextInt(100)+1;
//		System.out.println(i);
		
//		Long l = Lib.ConvertDateToLong(new Date(), 9);
//		Lib.ConvertLongToDay(new Date(), l);
//		
//		int gzip = ((int)129 & (int)0x80) >> 7;
//		int ver = (int)129 & (int)0x7F;
//		System.out.println(Arrays.asList(gzip, ver, (int)127, (int)0x80));
//		int a =ver | 0x80;
//		System.out.println((byte)a);
		Integer kq = null;
		System.out.println(2 + kq);
	}

}
