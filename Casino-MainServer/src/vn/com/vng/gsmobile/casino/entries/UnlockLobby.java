package vn.com.vng.gsmobile.casino.entries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import com.couchbase.client.java.document.json.JsonObject;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class UnlockLobby {
	public static final String UNLOCK_LOBBY_TABLENAME = "30_";
	public static final String UNLOCK_LOBBY_LIST = "unlock_lobby_list";
	public static final String GAME_TYPE = "game_type";
	public static final String LOBBY_TYPE = "lobby_type";
	public static final String COND_LIST = "cond_list";	
	private static ConcurrentHashMap<Byte, TreeMap<Byte, List<Map<String, Object>>>> database = null;
	@SuppressWarnings("unchecked")
	public synchronized static ConcurrentHashMap<Byte, TreeMap<Byte, List<Map<String, Object>>>> getUnlockLobbyBase(){
		if(database==null){
			try{
				database = new ConcurrentHashMap<Byte, TreeMap<Byte, List<Map<String, Object>>>>();
				JsonObject jo = Lib.getCB(Const.UNLOCK_LOBBY_ID);
				if(jo!=null){
					Map<String, Object> m = jo.toMap();
					List<Map<String, Object>> l = (List<Map<String, Object>>) m.get(UNLOCK_LOBBY_LIST);
					if(l!=null){
						for(Map<String, Object> e : l){
							Number gameType = (Number) e.get(GAME_TYPE);
							Number loobyType = (Number) e.get(LOBBY_TYPE);
							List<Map<String, Object>> condList = (List<Map<String, Object>>) e.get(COND_LIST);
							if(!database.containsKey(gameType.byteValue())){
								database.put(gameType.byteValue(), new TreeMap<Byte, List<Map<String, Object>>>());
							}
							TreeMap<Byte, List<Map<String, Object>>> lb = database.get(gameType.byteValue());
							lb.put(loobyType.byteValue(), condList);
						}
					}
				}
				
			}
			catch(Exception e){
				database = null;
				Lib.getLogger().error(Lib.getStackTrace(e));
			}
		}
		return database;
	}
	
	public static Byte checkNewUnlock(Long uid, Byte game_type){
		Byte kq = null;
		getUnlockLobbyBase();
		if(database!=null){
			User u = new User(uid);
			Number unlock_lobby_max = u.getConditionValue(CondType.LobbyUnlock, game_type);
			kq = unlock_lobby_max.byteValue();
			TreeMap<Byte, List<Map<String, Object>>> tr = database.get(game_type);
			Iterator<Entry<Byte,  List<Map<String, Object>>>> it = tr.entrySet().iterator();
			while(it.hasNext()){
				Entry<Byte,  List<Map<String, Object>>> e = it.next();
				if(kq < e.getKey()){
					if(Condition.valid(uid, e.getValue()))
						kq = e.getKey();
				}
			}
			if(kq <= unlock_lobby_max.byteValue())
				kq = null;
		}
		return kq;
	}
	public static Byte getMaxUnlock(Long uid, Byte game_type){
		User u = new User(uid);
		Number unlock_lobby_max = u.getConditionValue(CondType.LobbyUnlock, game_type);
		Byte kq = unlock_lobby_max.byteValue();
		return kq;
	}
	public static boolean updateMaxUnlock(Long uid, Byte game_type){
		boolean isNewUnlock = false;
		User u = new User(uid);
		Number unlock_lobby_max = u.getConditionValue(CondType.LobbyUnlock, game_type);
		Byte kq = unlock_lobby_max.byteValue();
		getUnlockLobbyBase();
		if(database!=null){
			TreeMap<Byte, List<Map<String, Object>>> tr = database.get(game_type);
			if(tr!=null){
				Iterator<Entry<Byte,  List<Map<String, Object>>>> it = tr.entrySet().iterator();
				while(it.hasNext()){
					Entry<Byte,  List<Map<String, Object>>> e = it.next();
					if(kq < e.getKey()){
						if(Condition.valid(uid, e.getValue()))
							kq = e.getKey();
					}
				}
				if(kq > unlock_lobby_max.byteValue()){
					List<List<?>> conds = new ArrayList<>();
					conds.add(Arrays.asList(String.format(User.PATTERN_COND, game_type.toString(), User.COND_GAME_LOBBY_UNLOCK), kq, CondUpdateType.Upsert));
					u.setConditionValue(conds);
					isNewUnlock = true;
				}
			}
		}
		return isNewUnlock;
	}
}
