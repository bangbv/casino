package vn.com.vng.gsmobile.casino.entries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.collections4.MapUtils;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import vn.com.vng.gsmobile.casino.flatbuffers.CardID;
import vn.com.vng.gsmobile.casino.flatbuffers.GameType;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class FakeCards {
	
	private static String FAKECARDS_KEY = "fakecards";
	private static String LIST = "list";

	private static final Map<String, Byte> fakeCards = MapUtils.putAll(
			new HashMap<String, Byte>(),  
			new Object[][]{
				{"34", CardID.Card_3_A},
				{"33", CardID.Card_3_B},
				{"32", CardID.Card_3_C},
				{"31", CardID.Card_3_D},
				
				{"44", CardID.Card_4_A},
				{"43", CardID.Card_4_B},
				{"42", CardID.Card_4_C},
				{"41", CardID.Card_4_D},
				
				{"54", CardID.Card_5_A},
				{"53", CardID.Card_5_B},
				{"52", CardID.Card_5_C},
				{"51", CardID.Card_5_D},
				
				{"64", CardID.Card_6_A},
				{"63", CardID.Card_6_B},
				{"62", CardID.Card_6_C},
				{"61", CardID.Card_6_D},
				
				{"74", CardID.Card_7_A},
				{"73", CardID.Card_7_B},
				{"72", CardID.Card_7_C},
				{"71", CardID.Card_7_D},
				
				{"84", CardID.Card_8_A},
				{"83", CardID.Card_8_B},
				{"82", CardID.Card_8_C},
				{"81", CardID.Card_8_D},
				
				{"94", CardID.Card_9_A},
				{"93", CardID.Card_9_B},
				{"92", CardID.Card_9_C},
				{"91", CardID.Card_9_D},
				
				{"104", CardID.Card_10_A},
				{"103", CardID.Card_10_B},
				{"102", CardID.Card_10_C},
				{"101", CardID.Card_10_D},
				
				{"J4", CardID.Card_J_A},
				{"J3", CardID.Card_J_B},
				{"J2", CardID.Card_J_C},
				{"J1", CardID.Card_J_D},
				
				{"Q4", CardID.Card_Q_A},
				{"Q3", CardID.Card_Q_B},
				{"Q2", CardID.Card_Q_C},
				{"Q1", CardID.Card_Q_D},
				
				{"K4", CardID.Card_K_A},
				{"K3", CardID.Card_K_B},
				{"K2", CardID.Card_K_C},
				{"K1", CardID.Card_K_D},
				
				{"A4", CardID.Card_A_A},
				{"A3", CardID.Card_A_B},
				{"A2", CardID.Card_A_C},
				{"A1", CardID.Card_A_D},
				
				{"24", CardID.Card_2_A},
				{"23", CardID.Card_2_B},
				{"22", CardID.Card_2_C},
				{"21", CardID.Card_2_D}
		});
	private static Map<Byte, Integer> idx = MapUtils.putAll(new ConcurrentHashMap<Byte, Integer>(), new Object[][]{
		{GameType.TLMN, 0},
		{GameType.TALA, 0},
		{GameType.BALA, 0},
		{GameType.MAUBINH, 0},
	});
	public synchronized static int nextIndex(Byte game_type, int size){
		Integer i = idx.get(game_type);
		if(i < size)
			idx.put(game_type, i+1);
		else{
			i = 0;
			idx.put(game_type, i);
		}
		return i;
	}
	public static List<Card> get(byte bGameType){
		List<Card> l = null;
		JsonArray fake = null;
		//1. lấy bài ngẫu nhiên từ DB
		JsonObject jo = LocalCache.get(FAKECARDS_KEY);
		if(jo!=null){
			JsonArray ja = jo.getArray(String.format("%s_%d", LIST, bGameType));
			if(ja!=null && ja.size()>0){
				int index = nextIndex(bGameType, ja.size());//new Random().nextInt(ja.size());
				fake = ja.getArray(index);
				//2. trả bài
				if(fake!=null && fake.size() >= 52){
					String fakeCardId = null;
					try{
						l = new ArrayList<>();
						Iterator<Object> it = fake.iterator();
						while(it.hasNext()){
							Object o = it.next();
							fakeCardId = o.toString();
							Byte realCardId = fakeCards.get(fakeCardId); 
							if(realCardId!=null)
								l.add(new Card(realCardId));
							else
								throw new Exception("Bài fake không hợp lệ");
						}
					}catch(Exception e){
						l = null;
						System.out.println(Arrays.asList("Lá bài chưa định nghĩa", fakeCardId, Lib.getStackTrace(e)));
					}
				}
				else
					System.out.println(Arrays.asList("Bộ bài không đủ 52 lá", fake));
			}
		}
		return l;
	}
}
