package vn.com.vng.gsmobile.casino.entries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.math.RandomUtils;
import com.couchbase.client.java.document.json.JsonObject;
import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.flatbuffers.GameType;
import vn.com.vng.gsmobile.casino.flatbuffers.JoinGameType;
import vn.com.vng.gsmobile.casino.schedules.RoomPushScheduler;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class RoomManager {
	static ConcurrentHashMap<Long, Long> leave_list = new ConcurrentHashMap<>();	
	static ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Long, Object>>> push_list = new ConcurrentHashMap<>();
	static ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, RoomPushScheduler>> pushschedule_list = new ConcurrentHashMap<>();
	static ConcurrentHashMap<Byte, Lobby> lobby_list = new ConcurrentHashMap<>();
	static ConcurrentHashMap<Long, Room> room_list = new ConcurrentHashMap<>(Const.MAX_ROOM);
	static ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>>>> room_meta = new ConcurrentHashMap<>();
	static SortedMap<String, Long> room_sort = new TreeMap<>();
	static ConcurrentHashMap<Byte, SortedMap<Long, Byte>> bet_sort = new ConcurrentHashMap<>();
	static AntiSpamManager anti_spam_create_room = new AntiSpamManager(5, 10, 30); 
	static AntiSpamManager anti_spam_invite_room = new AntiSpamManager(10, 10, 30); 
	static SortedSet<Short> room_no = new TreeSet<Short>();
	private static AtomicInteger room_no_max = new AtomicInteger(0);
	private static ConcurrentHashMap<Long, Long> user_room_mapping = new ConcurrentHashMap<>();	
	private static ConcurrentHashMap<Long, List<?>> user_lobby_mapping = new ConcurrentHashMap<>();	

	public static Long ROOM_TRAINING_ID = 123456l;
	
	private static void initPushSchedule(byte bGameType, byte bLobbyType){
		ConcurrentHashMap<Byte, RoomPushScheduler> lg = pushschedule_list.get(bGameType);
		if(lg==null){
			lg = new ConcurrentHashMap<>();
			pushschedule_list.put(bGameType, lg);
		}
		if(!lg.containsKey(bLobbyType)){
			RoomPushScheduler rps = new RoomPushScheduler(Const.ROOMPUSH_SCHEDULE, bGameType, bLobbyType);
			lg.put(bLobbyType, rps);
			rps.start();
		}
	}
	
	private static void initBetSort(byte bGameType, byte bLobbyType, long lBetValue){
		SortedMap<Long, Byte> bs = bet_sort.get(bGameType);
		if(bs==null){
			bs = new TreeMap<>();
			bet_sort.put(bGameType, bs);
		}
		Byte lb = bs.get(lBetValue);
		if(lb==null || lb < bLobbyType){
			bs.put(lBetValue, bLobbyType);
		}
	}
	
	public static List<?> getBetAndLobby(Long uid, Byte bGameType, Long lCoin, Byte...lobby_unlock_max){
		List<?> kq = null;
		Integer rate = GameConfig.load(false).getObject(GameConfig.GAMELIST).getObject(bGameType.toString()).getInt(GameConfig.REQUIRECOIN_RATE);
		if(rate==null) rate = 1;
		SortedMap<Long, Byte> bs = bet_sort.get(bGameType==null?0:bGameType);
		if(bs!=null){
			Long toKey = lCoin/rate;
			SortedMap<Long, Byte> s = bs.headMap(toKey);
			if(s != null && s.size() > 0){
				if(lobby_unlock_max.length>0){
					TreeMap<Long, Byte> t = new TreeMap<>(Collections.reverseOrder());
					t.putAll(s);
					Iterator<Entry<Long, Byte>> it = t.entrySet().iterator();
					while (it.hasNext()) {
						Entry<Long, Byte> e = it.next();
						if(e.getValue() <= lobby_unlock_max[0]){
							kq = Arrays.asList(e.getKey(), e.getValue());
							break;
						}
					}
				}
				else{
					long lBetValue = s.lastKey();
					byte bLobbyType = bs.get(lBetValue);
					kq = Arrays.asList(lBetValue, bLobbyType);
				}
				
				//update suggest lobby
				SuggestLobby.updateSuggestLobby(uid, bGameType, (Byte)kq.get(1));
			}

			if(kq==null){
				long lBetValue = bs.firstKey();
				byte bLobbyType = bs.get(lBetValue);
				kq = Arrays.asList(lBetValue, bLobbyType);
			}
		}
		return kq;
	}
	public static boolean isMaxBetOfLobby(Byte game_type, Byte lobby_type, Long bet_value){
		boolean isMaxBet = false;
		SortedMap<Long, Byte> bs = bet_sort.get(game_type);
		if(bs!=null){
			Byte lb = bs.get(bet_value);
			if(lb!=null && lb > lobby_type)
				isMaxBet = true;
		}
		return isMaxBet;
	}
	private static void initRoomNo(){
		for(short i = 1000; i < 9999; i++){
			room_no.add(i);
		}
		room_no_max.set(9999); 
	}
	private static void initLobby(byte bGameType){
		if(!lobby_list.containsKey(bGameType)){
			Lobby l = new Lobby(bGameType, Const.INIT_SIT_IN_LOBBY);
			lobby_list.put(bGameType, l);
		}
	}
	@SuppressWarnings("unused")
	private synchronized static Room initRoom(Long lRId){
		if(room_list.size() < Const.MAX_ROOM ){
			Room r = new Room(lRId);
			room_list.put(lRId, r);
			return r;
		}
		else
			return null;
	}
	private synchronized static Room initRoom(){
		if(room_list.size() < Const.MAX_ROOM ){
			Long lRId = Lib.getNanoTimeId();
			Room r = new Room(lRId);
			room_list.put(lRId, r);
			return r;
		}
		else
			return null;
	}
	private static byte useRoom(Long lRId, String sDesc, byte bGameType, byte bLobbyType, long lBet, int iBigBet, byte bTip, int iMaxSit, String sPassword, long lRequireCoin, int iRequireLevel, boolean bFake){
		byte bKq = ErrorCode.UNKNOWN;
		Room r = room_list.get(lRId);
		if(r!=null){
			String sName = (ROOM_TRAINING_ID == lRId)?"TẬP SỰ":String.format("%d", allocRoomNo());
			bKq = r.use(sName, sDesc, bGameType, bLobbyType, lBet, iBigBet, bTip, iMaxSit, sPassword, lRequireCoin, iRequireLevel, bFake);
			if(bKq == ErrorCode.OK){
				if(!r.isFake()){
					r.setSortKey();
					resetRoomSort(r);
				}
				resetRoomMeta(r);
				addPushList(r.getId(), r.getGameType(), r.getLobbyType());//co can khong nhy
			}
		}
		else
			bKq = ErrorCode.NOTEXISTS;
		return bKq;
	}

	private static byte unuseRoom(Long lRId){
		byte bKq = ErrorCode.UNKNOWN;
		Room r = room_list.get(lRId);
		if(r!=null){
			byte bGameType = r.getGameType();
			byte bLobbyType = r.getLobbyType();
			long lBet = r.getBet();
			String sName = r.getName();
			boolean isFake = r.isFake();
			bKq = r.unuse();
			if(bKq == ErrorCode.OK){
				if(!isFake){
					removeRoomSort(r);
				}
				removeRoomMeta(lRId, bGameType, bLobbyType, lBet);
				addPushList(lRId, bGameType, bLobbyType);
				if(NumberUtils.isNumber(sName)){
					returnRoomNo(NumberUtils.toShort(sName));
				}
			}
		}
		return bKq;
	}

	private static byte joinRoom(Long sUID, Room room){
		byte bKq = ErrorCode.UNKNOWN;
		Dealer d = room.getDealer();
		if(d!=null){
			bKq = d.joinSit(sUID);
			if(bKq == ErrorCode.OK){
				Lobby lb = lobby_list.get(room.getGameType());
				lb.leave(sUID);
				room.setSortKey();
				resetRoomSort(room);
				resetRoomMeta(room);
				addPushList(room.getId(), room.getGameType(), room.getLobbyType());
				addUserRoomMapping(sUID, room.getId());
			}
		}
		else
			bKq = ErrorCode.DEALER_NOTFOUND;
		return bKq;		
	}
	private static byte joinRoomViewOnly(Long sUID, Room room){
		byte bKq = ErrorCode.UNKNOWN;
		Dealer d = room.getDealer();
		if(d!=null){
			bKq = d.joinViewer(sUID);
			if(bKq == ErrorCode.OK){
				Lobby lb = lobby_list.get(room.getGameType());
				lb.leave(sUID);
				addUserRoomMapping(sUID, room.getId());
			}
		}
		else
			bKq = ErrorCode.DEALER_NOTFOUND;
		return bKq;		
	}
	private synchronized static void resetRoomSort(Room r) {
		// TODO Auto-generated method stub
		String sLastSortKey = r.getLastSortKey();
		if(sLastSortKey!=null){
			room_sort.remove(sLastSortKey);
		}
		String sSortKey = r.getSortKey();
		if(sSortKey!=null){
			if(r.getPassword() ==null && r.getCurrentSit() < r.getMaxSit()){
				room_sort.put(sSortKey, r.getId());
			}
			else{
				room_sort.remove(sSortKey);
			}
		}
		Lib.getLogger().trace(Arrays.asList("resetRoomSort", sLastSortKey, sSortKey, room_sort, RoomManager.class.getSimpleName()));
	}
	
	private synchronized static void removeRoomSort(Room r) {
		// TODO Auto-generated method stub
		String sLastSortKey = r.getLastSortKey();
		if(sLastSortKey!=null){
			room_sort.remove(sLastSortKey);
		}
		String sSortKey = r.getSortKey();
		if(sSortKey!=null){
			room_sort.remove(sSortKey);
		}
		Lib.getLogger().trace(Arrays.asList("removeRoomSort", sLastSortKey, sSortKey, room_sort, RoomManager.class.getSimpleName()));
	}
	
	private synchronized static void resetRoomMeta(Room room) {
		// TODO Auto-generated method stub
		ConcurrentHashMap<Byte, ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>>> metaGameType = room_meta.get(room.getGameType());
		if(metaGameType==null){
			metaGameType = new ConcurrentHashMap<>();
			room_meta.put(room.getGameType(), metaGameType);
		}
		ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>> metaLobbyType = metaGameType.get(room.getLobbyType());
		if(metaLobbyType==null){
			metaLobbyType = new ConcurrentHashMap<>();
			metaGameType.put(room.getLobbyType(), metaLobbyType);
		}
		ConcurrentHashMap<Long, Object> metaBet = metaLobbyType.get(room.getBet());
		if(metaBet==null){
			metaBet = new ConcurrentHashMap<>();
			metaLobbyType.put(room.getBet(), metaBet);
		}
		Object metaRoom = Arrays.asList(room.getState(), room.getPlayers());
		metaBet.put(room.getId(), metaRoom);
	}
	
	
	private synchronized static void removeRoomMeta(Long lRId, byte bGameType, byte bLobbyType, long lBet) {
		// TODO Auto-generated method stub
		ConcurrentHashMap<Byte, ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>>> metaGameType = room_meta.get(bGameType);
		if(metaGameType==null){
			metaGameType = new ConcurrentHashMap<>();
			room_meta.put(bGameType, metaGameType);
		}
		ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>> metaLobbyType = metaGameType.get(bLobbyType);
		if(metaLobbyType==null){
			metaLobbyType = new ConcurrentHashMap<>();
			metaGameType.put(bLobbyType, metaLobbyType);
		}
		ConcurrentHashMap<Long, Object> metaBet = metaLobbyType.get(lBet);
		if(metaBet==null){
			metaBet = new ConcurrentHashMap<>();
			metaLobbyType.put(lBet, metaBet);
		}
		metaBet.remove(lRId);
	}
	
	private static boolean addPushList(Long lRId, byte bGameType, byte bLobbyType){
		ConcurrentHashMap<Byte, ConcurrentHashMap<Long, Object>> lg = push_list.get(bGameType);
		if(lg == null){
			lg = new ConcurrentHashMap<>();
			push_list.put(bGameType, lg);
		}
		ConcurrentHashMap<Long, Object> ll = lg.get(bLobbyType);
		if(ll == null){
			ll = new ConcurrentHashMap<>();
			lg.put(bLobbyType, ll);
		}
		ll.put(lRId, true);
		return true;
		
	}
	
	private static void addUserRoomMapping(long uid, long rid){
		user_room_mapping.put(uid, rid);
	}
	private static void removeUserRoomMapping(long uid){
		user_room_mapping.remove(uid);
	}
	public static void updateUserLobbyMapping(long uid, long time){
		List<?> l = user_lobby_mapping.get(uid);
		if(l!=null){
			user_lobby_mapping.put(uid, Arrays.asList(l.get(0), time));
		}
	}
	private static void addUserLobbyMapping(long uid, byte gameType){
		user_lobby_mapping.put(uid, Arrays.asList(gameType, Long.MAX_VALUE));
	}
	private static void removeUserLobbyMapping(long uid){
		user_lobby_mapping.remove(uid);
	}
	//////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public static void init(){
		initRoomNo();
		Iterator<Entry<String, Object>> it = GameConfig.load(false).getObject(GameConfig.ROOMLIST).toMap().entrySet().iterator();
		while(it.hasNext()){
			Map rcf  = (Map) it.next().getValue();
			int cnt = ((Double) rcf.get(GameConfig.INITROOM)).intValue();
			byte gameType = ((Double) rcf.get(GameConfig.GAMETYPE)).byteValue();
			byte lobbyType = ((Double) rcf.get(GameConfig.LOBBYTYPE)).byteValue();
			long bet = ((Double) rcf.get(GameConfig.BET)).longValue();
			byte tip = ((Double) rcf.get(GameConfig.TIP)).byteValue();
			int maxSit = ((Double) rcf.get(GameConfig.MAXSIT)).intValue();
			long requireCoin = ((Double) rcf.get(GameConfig.REQUIRECOIN)).longValue();
			int requireLevel = ((Double) rcf.get(GameConfig.REQUIRELEVEL)).intValue();
			initLobby(gameType);
			initPushSchedule(gameType, lobbyType);
			initBetSort(gameType, lobbyType, bet);
			for(int i = 0; i < cnt; i++){
				Room r = initRoom();
				if(r!=null)
					useRoom(r.getId(), getRoomDesc(gameType), gameType, lobbyType, bet, 0, tip, maxSit, null, requireCoin, requireLevel, false);
			}
		}
//		Room tr = initRoom(ROOM_TRAINING_ID);
//		if(tr!=null)
//			useRoom(tr.getId(), getRoomDesc(GameType.TLMN), GameType.TLMN, LobbyType.Lobby_1, 100, (byte)10, 4, Lib.md5("vudeptrai"), 500, 0, false);
	}
	
	public static void close(){
		//1. tắt các lịch push
		Iterator<Entry<Byte, ConcurrentHashMap<Byte, RoomPushScheduler>>> itGame = pushschedule_list.entrySet().iterator();
		while(itGame.hasNext()){
			ConcurrentHashMap<Byte, RoomPushScheduler> eGame = itGame.next().getValue();
			Iterator<Entry<Byte, RoomPushScheduler>> itLobby = eGame.entrySet().iterator();
			while(itLobby.hasNext()){
				RoomPushScheduler psch = itLobby.next().getValue();
				psch.stop();
			}
		}
		//2. giải phóng các room, room nào đang chơi thì chờ kết thúc ván
		Iterator<Entry<Long, Room>> it = room_list.entrySet().iterator();
		while(it.hasNext()){
			try{
				Room r = it.next().getValue();
				if(r!=null && r.getState() != GameRoomState.Playing){
					byte bKq = RoomManager.unuseRoom(r.getId());
					if(bKq == ErrorCode.OK)
						it.remove();
				}
			}catch(Exception e){
				Lib.getLogger().error(RoomManager.class.getName()+".close:"+Lib.getStackTrace(e));
			}
		}
	}
	
	public static void stateSchedule(){
		Iterator<Entry<Long, Room>> it = room_list.entrySet().iterator();
		while(it.hasNext()){
			try{
				Room r = it.next().getValue();
				if(r!=null){
					Byte state = r.getState();
					switch(state){
					case GameRoomState.Idle:
						if(!ROOM_TRAINING_ID.equals(r.getId()) && r.getTimeInState() > Const.ROOMSTATE_IDLE)
							RoomManager.unuseRoom(r.getId());
						break;
					case GameRoomState.Destroyed:
						if(r.getTimeInState() > Const.ROOMSTATE_DESTROYED)
							it.remove();
						break;
					case GameRoomState.Waiting_Player:
						if(r.getTimeInState() > Const.ROOMSTATE_WAITING_PLAYER)
							r.getDealer().kickall();
						break;
					case GameRoomState.Waiting_Game:
						if(r.getTimeInState() > Const.ROOMSTATE_WAITING_GAME)
							r.getDealer().kickall();
						break;
					}
				}
			}catch(Exception e){
				Lib.getLogger().error(RoomManager.class.getName()+".stateSchedule:"+Lib.getStackTrace(e));
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static void allocSchedule() {
		// TODO Auto-generated method stub
		//0. Tính số room fake cần hủy/cần tạo
		//1. Hủy ngẫu nhiên 1 số/hoặc hủy hết room fake
		Iterator<Room> itRoom = RoomManager.room_list.values().iterator();
		while(itRoom.hasNext()){
			Room r = itRoom.next();
			if(r.isFake())
				RoomManager.unuseRoom(r.getId());
		}
		//2. Tạo một số room fake khác
		Iterator<Entry<String, Object>> it = GameConfig.load(false).getObject(GameConfig.ROOMLIST).toMap().entrySet().iterator();
		while(it.hasNext()){
			Map rcf  = (Map) it.next().getValue();
			byte gameType = ((Double) rcf.get(GameConfig.GAMETYPE)).byteValue();
			byte lobbyType = ((Double) rcf.get(GameConfig.LOBBYTYPE)).byteValue();
			long bet = ((Double) rcf.get(GameConfig.BET)).longValue();
			byte tip = ((Double) rcf.get(GameConfig.TIP)).byteValue();
			int maxSit = ((Double) rcf.get(GameConfig.MAXSIT)).intValue();
			long requireCoin = ((Double) rcf.get(GameConfig.REQUIRECOIN)).longValue();
			int requireLevel = ((Double) rcf.get(GameConfig.REQUIRELEVEL)).intValue();
			String password = RandomUtils.nextInt(100)+1<95?null:Lib.md5(UUID.randomUUID().toString());
			int cnt = RandomUtils.nextBoolean()?RandomUtils.nextInt(10):0;
			for(int i = 0; i < cnt; i++){
				Room r = initRoom();
				if(r!=null)
					useRoom(r.getId(), getRoomDesc(gameType) ,gameType, lobbyType, bet, 0, tip, maxSit, password, requireCoin, requireLevel, true);
			}
		}
		//3. Tạo một số room trống (nếu cần)
	}
	public static void lobbyCleanSchedule() {
		Iterator<Entry<Long, List<?>>> it = user_lobby_mapping.entrySet().iterator();
		while(it.hasNext()){
			Entry<Long, List<?>> l = it.next();
			if(System.currentTimeMillis() - (long)l.getValue().get(1) >= Const.LOBBYUSER_IDLE){
				byte bGameType = (byte)l.getValue().get(0);
				Lobby lb = lobby_list.get(bGameType);
				if(lb!=null){
					lb.leave(l.getKey());
				}
				it.remove();
			}
		}
	}
	public static void roomCleanSchedule() {
	}
	public static Byte getGameTypeByUser(Long uid){
		Byte bGameType = null;
		List<?> l =  user_lobby_mapping.get(uid);
		if(l!=null)
			bGameType = (Byte) l.get(0);
		return bGameType;
	}
	
	public static Room getRoomByUser(Long uid){
		Room r = null;
		Long l = user_room_mapping.get(uid);
		if(l!=null){
			r = getRoom(l);
		}
		return r;
	}
	
	public static Room getRoom(Long lRId){
		return room_list.get(lRId);
	}
	@SuppressWarnings("unchecked")
	public static Room getRoom(Byte gameType, Byte roomState){
		Room r = null;
		ConcurrentHashMap<Byte, ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>>> metaGameType = room_meta.get(gameType);
		if(metaGameType!=null){
			Iterator<Entry<Byte, ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>>>> itGame = metaGameType.entrySet().iterator();
			while(itGame.hasNext()){
				Entry<Byte, ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>>> eGame = itGame.next();
				ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>> metaLobbyType = eGame.getValue();
				if(metaLobbyType!=null){
					Iterator<Entry<Long, ConcurrentHashMap<Long, Object>>> itLobby = metaLobbyType.entrySet().iterator();
					while(itLobby.hasNext()){
						Entry<Long, ConcurrentHashMap<Long, Object>> eLobby = itLobby.next();
						ConcurrentHashMap<Long, Object> metaBet = eLobby.getValue();
						if(metaBet!=null){
							Iterator<Entry<Long, Object>> itBet = metaBet.entrySet().iterator();
							while(itBet.hasNext()){
								Entry<Long, Object> eBet = itBet.next();
								List<Object> metaRoom = (List<Object>) eBet.getValue();
								if((byte)metaRoom.get(0)==roomState){
									r = room_list.get(eBet.getKey());
									break;
								}
							}
						}
					}
				}
			}
		}
		return r;
	}
	public static List<Room> getRoomList(Object... fillter){
		//fillter = [gameType, lobbyType, bet]
		List<Room> l = new ArrayList<>();
		ConcurrentHashMap<Byte, ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>>> metaGameType = null;
		switch(fillter.length){
		case 3:
			metaGameType = room_meta.get(fillter[0]);
			if(metaGameType!=null){
				ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>> metaLobbyType = metaGameType.get(fillter[1]);
				if(metaLobbyType!=null){
					ConcurrentHashMap<Long, Object> metaBet = metaLobbyType.get(fillter[2]);
					if(metaBet!=null){
						Iterator<Entry<Long, Object>> itBet = metaBet.entrySet().iterator();
						while(itBet.hasNext()){
							Entry<Long, Object> e = itBet.next();
							Long lRId = e.getKey();
							Room r = room_list.get(lRId);
							if(r!=null && !l.contains(r))
								l.add(r);
						}
					}
				}
			}
			break;
		case 2:
			metaGameType = room_meta.get(fillter[0]);
			if(metaGameType!=null){
				ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>> metaLobbyType = metaGameType.get(fillter[1]);
				if(metaLobbyType!=null){
					Iterator<Entry<Long, ConcurrentHashMap<Long, Object>>> itLobby = metaLobbyType.entrySet().iterator();
					while(itLobby.hasNext()){
						Entry<Long, ConcurrentHashMap<Long, Object>> eLobby = itLobby.next();
						ConcurrentHashMap<Long, Object> metaBet = eLobby.getValue();
						if(metaBet!=null){
							Iterator<Entry<Long, Object>> itBet = metaBet.entrySet().iterator();
							while(itBet.hasNext()){
								Entry<Long, Object> eBet = itBet.next();
								Long lRId = eBet.getKey();
								Room r = room_list.get(lRId);
								if(r!=null && !l.contains(r))
									l.add(r);
							}
						}
					}
				}
			}
			break;
		case 1:
			metaGameType = room_meta.get(fillter[0]);
			if(metaGameType!=null){
				Iterator<Entry<Byte, ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>>>> itGame = metaGameType.entrySet().iterator();
				while(itGame.hasNext()){
					Entry<Byte, ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>>> eGame = itGame.next();
					ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>> metaLobbyType = eGame.getValue();
					if(metaLobbyType!=null){
						Iterator<Entry<Long, ConcurrentHashMap<Long, Object>>> itLobby = metaLobbyType.entrySet().iterator();
						while(itLobby.hasNext()){
							Entry<Long, ConcurrentHashMap<Long, Object>> eLobby = itLobby.next();
							ConcurrentHashMap<Long, Object> metaBet = eLobby.getValue();
							if(metaBet!=null){
								Iterator<Entry<Long, Object>> itBet = metaBet.entrySet().iterator();
								while(itBet.hasNext()){
									Entry<Long, Object> eBet = itBet.next();
									Long lRId = eBet.getKey();
									Room r = room_list.get(lRId);
									if(r!=null && !l.contains(r))
										l.add(r);
								}
							}
						}
					}
				}
			}
			break;
		case 0:
		default:
			l.addAll(room_list.values());
		}
		return l;
	}
	
	public synchronized static List<?> createRoom(Long sUID, String sDesc, byte bGameType, byte bLobbyType, long lBet, int iBigBet, String sPassword) {
		// TODO Auto-generated method stub
		byte bKq = ErrorCode.UNKNOWN;
		Room r = null;
		JsonObject rcf = GameConfig.load(false).getObject(GameConfig.ROOMLIST).getObject(bGameType+"_"+bLobbyType+"_"+lBet);
		Integer current_room = null;
		if(rcf != null){
			//1. Tìm Room trống có sẵn
			ConcurrentHashMap<Byte, ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>>> metaGameType = room_meta.get(bGameType);
			if(metaGameType!=null){
				ConcurrentHashMap<Long, ConcurrentHashMap<Long, Object>> metaLobbyType = metaGameType.get(bLobbyType);
				if(metaLobbyType!=null){
					ConcurrentHashMap<Long, Object> metaBet = metaLobbyType.get(lBet);
					if(metaBet!=null){
						current_room = metaBet.size();
						Iterator<Entry<Long, Object>> it = metaBet.entrySet().iterator();
						while(it.hasNext()){
							Entry<Long, Object> e = it.next();
							List<?> l = (List<?>) e.getValue();
							if((byte) l.get(0) == GameRoomState.Idle){
								System.out.println(Arrays.asList("room="+e, "\nmetaBet="+metaBet, "\nroomMeta="+room_meta, "\nroomList="+room_list));
								r = room_list.get(e.getKey());
								if(r!=null && r.getTimeInState() < Const.ROOMSTATE_IDLE - 30000){
									r.setPassword(sPassword);
									r.setBigBet(iBigBet);
									r.setDesc(sDesc);
									bKq = ErrorCode.OK;
								}
								else 
									r = null;
								break;
							}
						}
					}
				}
			}
			//2. Không có thì tạo mới
			if(r == null){
				Integer max_room = rcf.getInt(GameConfig.MAXROOM);
				if(max_room==null || max_room == 0) max_room = Const.MAX_ROOM;
				if(current_room==null) current_room = 0;
				if(current_room <= max_room){
					r = initRoom();
					if(r!=null){
						bKq = useRoom(r.getId(),
							sDesc,
							bGameType, 
							bLobbyType, 
							lBet, 
							iBigBet,
							rcf.getInt(GameConfig.TIP).byteValue(),
							rcf.getInt(GameConfig.MAXSIT),
							sPassword, 
							rcf.getLong(GameConfig.REQUIRECOIN),
							rcf.getInt(GameConfig.REQUIRELEVEL),
							false
						);
					}
					else
						bKq = ErrorCode.NOTENOUGH_ROOM;
				}
				else
					bKq = ErrorCode.LIMITED_ROOM;
			}
			//3. Gán người tạo vào room, đặt làm chủ phòng luôn
			if(bKq == ErrorCode.OK)
				bKq = joinRoom(sUID, r);
		}
		else
			bKq = ErrorCode.ROOM_NOTMATCH;
		return Arrays.asList(bKq, r);
	}

	public static List<?> joinRoom(Long userId, Long roomId, int joinType, String passWord) {
		// TODO Auto-generated method stub
		byte bKq = ErrorCode.UNKNOWN;
		Room r = room_list.get(roomId);
		if(r != null){
			String roomPass = r.getPassword();
			if(roomPass==null || (roomPass!=null && roomPass.equals(passWord))){
				switch (joinType) {
				case JoinGameType.PLAY:
					bKq = joinRoom(userId, r);
					break;
				case JoinGameType.VIEW:
					bKq = joinRoomViewOnly(userId, r);
					break;
				default:
					break;
				}
			}
			else
				bKq = ErrorCode.PASSWORD_INVALID;
		}
		else
			bKq = ErrorCode.NOTEXISTS;
		return Arrays.asList(bKq, r);
	}
	
	public synchronized static List<?> quickJoinRoom(Long userId, Long lCoin, Byte bGameType, Byte bLobbyType) {
		// TODO Auto-generated method stub
		byte bKq = ErrorCode.UNKNOWN;
		Room r = null;
		List<?> l = getBetAndLobby(userId, bGameType, lCoin, bLobbyType);
		if(l!=null){
			Long lBetValue = (Long) l.get(0);
			Byte bLobbyType_Fixed = (Byte) l.get(1);
			String sSortKeyMin = Lib.buildSortKey(bGameType==null?0:bGameType, (byte)0, 0l, 0, 0);
			String lSortKey = Lib.buildSortKey(bGameType==null?0:bGameType, bLobbyType_Fixed, lBetValue, 9, 0);
			String lSortKeyMax = Lib.buildSortKey(bGameType==null?0:bGameType, bLobbyType_Fixed, 9999999999l, 9, Long.MAX_VALUE);
			SortedMap<String, Long> s = room_sort.subMap(sSortKeyMin, lSortKeyMax).headMap(lSortKey);
			Lib.getLogger().trace(Arrays.asList("quickJoinRoom", userId, lCoin, bGameType, bLobbyType_Fixed, sSortKeyMin, lSortKey, lSortKeyMax, s, room_sort, RoomManager.class.getSimpleName()));
			if(s!=null && s.size() > 0){
				Long lRId = s.get(s.lastKey());
				r = lRId==null?null:room_list.get(lRId);
				if(r!=null){
					bKq = joinRoom(userId, r);
				}
				else
					bKq = ErrorCode.NOTEXISTS;
			}
			else
				bKq = ErrorCode.ROOM_NOTMATCH;
		}
		else
			bKq = ErrorCode.ROOM_NOTMATCH;
		return Arrays.asList(bKq, r);
	}
	
	public static byte leaveRoom(Long sUID, Long lRId){
		byte bKq = ErrorCode.UNKNOWN;
		Room room = room_list.get(lRId);
		if(room!=null){
			Dealer d = room.getDealer();
			if(d!=null){
				boolean isViewer = d.isViewer(sUID);
				bKq = isViewer?d.leaveViewer(sUID):d.leaveSit(sUID);
				if(bKq == ErrorCode.OK){
					Lobby lb = lobby_list.get(room.getGameType());
					lb.join(sUID, true);
					removeUserRoomMapping(sUID);
					if(!isViewer){
						room.setSortKey();
						resetRoomSort(room);
						resetRoomMeta(room);
						addPushList(room.getId(), room.getGameType(), room.getLobbyType());
					}
				}
			}
			else
				bKq = ErrorCode.DEALER_NOTFOUND;
		}
		else
			bKq = ErrorCode.NOTEXISTS;
		return bKq;
	}
	
	public static boolean joinLobby(Long userId, byte bGameType) {
		// TODO Auto-generated method stub
		Lobby lb = lobby_list.get(bGameType);
		if(lb!=null){
			addUserLobbyMapping(userId, bGameType);
			return lb.join(userId, true);
		}
		else
			return false;
	}
	
	public static boolean leaveLobby(Long userId, byte bGameType) {
		// TODO Auto-generated method stub
		Lobby lb = lobby_list.get(bGameType);
		if(lb!=null){
			removeUserLobbyMapping(userId);
			return lb.leave(userId);
		}
		else
			return false;
	}
	
	public static Lobby getLobby(byte bGameType){
		return lobby_list.get(bGameType);
	}
	public static ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Long, Object>>> getPushList(){
		return push_list;
	}
	public static ConcurrentHashMap<Long, Long> getLeaveList(){
		return leave_list;
	}
	public static boolean validInviteRoom(long uid){
		return anti_spam_invite_room.get(uid).valid();
	}
	public static boolean validCreateRoom(long uid){
		return anti_spam_create_room.get(uid).valid();
	}
	public static short allocRoomNo(){
		synchronized (room_no) {
			Short no = room_no.first();
			if(no == null){
				room_no_max.incrementAndGet();
				no = room_no_max.shortValue();
			}
			else
				room_no.remove(no);
			return no;
		}
	}
	public static void returnRoomNo(Short no){
		synchronized (room_no) {
			if(no!=null && !room_no.contains(no))
				room_no.add(no);
		}
	}
	public static SortedSet<Short> getRoomNo(){
		return room_no;
	}
	
	private static final Map<String, List<String>> room_desc = MapUtils.putAll(
			new HashMap<String, List<String>>(),  
			new Object[][]{
				{String.format("%d", GameType.TLMN), Arrays.asList(
						"Liều thì ăn nhiều",
				        "Đừng để thúi heo",
				        "Dân chuyên nghiệp",
				        "Nhiều tiền thì vào",
				        "4 đôi thông",
				        "Zui là chính"
				)},
				{String.format("%d", GameType.BALA), Arrays.asList(
						"Liều thì ăn nhiều",
				        "Ba tiên chấp hết",
				        "Dân chuyên nghiệp",
				        "Nhiều tiền thì vào",
				        "Vào là hốt hết",
				        "Zui là chính"
				)},
				{String.format("%d", GameType.MAUBINH), Arrays.asList(
						"Liều thì ăn nhiều",
				        "Coi chừng sập hòm",
				        "Dân chuyên nghiệp",
				        "Nhiều tiền thì vào",
				        "Vào là hốt hết",
						"Bắt sập làng",
				        "Zui là chính"
				)},
		});
	public static String getRoomDesc(byte bGameType){
		List<String> desc = room_desc.get(String.format("%d", bGameType));
		if(desc!=null){
			int index = new Random().nextInt(desc.size());
			return desc.get(index);
		}
		return "";
	}
}
