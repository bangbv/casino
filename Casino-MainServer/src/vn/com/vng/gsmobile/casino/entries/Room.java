package vn.com.vng.gsmobile.casino.entries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.ArrayUtils;
import com.google.flatbuffers.FlatBufferBuilder;

import io.netty.channel.Channel;
import io.netty.util.internal.ConcurrentSet;
import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.flatbuffers.GameType;
import vn.com.vng.gsmobile.casino.flatbuffers.LobbyType;
import vn.com.vng.gsmobile.casino.flatbuffers.RoomDetailInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.RoomInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.RoomResponse;
import vn.com.vng.gsmobile.casino.flatbuffers.RoomUpdateInfo;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class Room {
	public static String ID = "Id";
	public static String NAME = "Name";
	public static String DESC = "Desc";
	public static String GAMETYPE = "GameType";
	public static String LOBBYTYPE = "LobbyType";
	public static String BET = "BetValue";
	public static String BIGBET = "BigBet";
	public static String TIP = "Tip";
	public static String MAXSIT = "MaxSit";
	public static String REQUIRECOIN = "RequireCoin";
	public static String REQUIRELEVEL = "RequireLevel";
	public static String PASSWORD = "Password";
	public static String STATE = "State";
	public static String HOST = "HostIdx";
	public static String SITLIST = "SitList";
	public static String PLAYERS = "Players";
	public static String VIEWERS = "Viewers";
	
	public static byte TIP_DEFAULT = 5;
	
	private byte bGameType = GameType.None;
	private byte bLobbyType = LobbyType.None;
	private Long lId = null;
	private String sName = null;
	private String sDesc = null;
	private long lBetValue = 0l;
	private byte bTip = TIP_DEFAULT;
	private int iBigBet = 0;
	private long lRequireCoin = 0l;
	private int iRequireLevel = 0;
	private String sPassword = null;
	private byte bState = GameRoomState.Idle;
	private long lStateTime = System.currentTimeMillis();
	private int iMaxSit = 4;
	private int iMaxViewer = 4;
	private List<Byte> lSitList = new ArrayList<Byte>();
	private int iHostIdx = -1;
	private List<Long> lPlayers = new ArrayList<Long>();
	private ConcurrentSet<Long> lViewers = new ConcurrentSet<Long>();//new ArrayList<Long>();

	private int iCurrentSit = 0;
	private Dealer oDealer = null;
	private Battle oBattle = null;
	
	private String sLastSortKey = null;
	private String sSortKey = null;
	
	private boolean bFake = false;
	private long lRemainingTime = 0l;
	public Room(Long lId){
		this.lId = lId;
		this.sName = lId.toString();
	}
	public boolean isFake(){
		return bFake;
	}
	public String getSortKey(){
		return sSortKey;
	}
	public String getLastSortKey(){
		return sLastSortKey;
	}
	public synchronized String setSortKey(){
		sLastSortKey = sSortKey;
		return sSortKey = Lib.buildSortKey(bGameType, bLobbyType, lBetValue, iCurrentSit, this.lId);
	}
	
	public synchronized byte use(String sName, String sDesc, byte bGameType, byte bLobbyType, long lBet, int iBigBet, byte bTip, int iMaxSit, String sPassword, long lRequireCoin, int iRequireLevel, boolean bFake){
		byte bKq = ErrorCode.UNKNOWN;
		if(this.bState <= GameRoomState.Idle){
			this.sName = sName;
			this.sDesc = sDesc;
			this.bGameType = bGameType;
			this.bLobbyType = bLobbyType;
			this.lBetValue = lBet;
			this.iBigBet = iBigBet;
			this.bTip = bTip;
			this.iMaxSit = iMaxSit;
			this.sPassword = sPassword;
			this.lRequireCoin = lRequireCoin;
			this.iRequireLevel = iRequireLevel;
			if(this.lSitList!=null) this.lSitList.clear();
			if(this.lPlayers!=null) this.lPlayers.clear();
			if(this.lViewers!=null) this.lViewers.clear();
			if(bFake){
				this.iCurrentSit = iMaxSit;
				for(int i=0;i < iMaxSit; i++){
					this.lSitList.add((byte)1);
					this.lPlayers.add(0l);
				}
				this.setDealer(new Dealer(this));
				setState(GameRoomState.Playing);
			}
			else{
				this.iCurrentSit = 0;
				for(int i=0;i < iMaxSit; i++){
					this.lSitList.add((byte)0);
					this.lPlayers.add(null);
				}
				this.setDealer(new Dealer(this));
				setState(GameRoomState.Idle);
			}
			this.bFake = bFake;
			bKq = ErrorCode.OK;
		}
		else
			bKq = ErrorCode.USED_ROOM;
		return bKq;
	}
	
	public synchronized byte unuse(){
		byte bKq = ErrorCode.UNKNOWN;
		if(this.bState != GameRoomState.Playing || bFake){
			this.sName = "Đã hủy";
			this.sDesc = "Phòng đã hủy";
			this.bGameType = GameType.None;
			this.bLobbyType = LobbyType.None;
			this.lBetValue = 0l;
			this.iBigBet = 0;
			this.bTip = TIP_DEFAULT;
			this.iMaxSit = 4;
			this.iCurrentSit = 0;
			this.sPassword = null;
			this.lRequireCoin = 0l;
			this.iRequireLevel = 0;
			if(this.lSitList!=null) this.lSitList.clear();
			if(this.lPlayers!=null) this.lPlayers.clear();
			if(this.lViewers!=null) this.lViewers.clear();			
			this.setDealer(null);
			this.setBattle(null);
			setState(GameRoomState.Destroyed);
			this.bFake = false;
			bKq = ErrorCode.OK;
		}
		else
			bKq = ErrorCode.PLAYING_ROOM;
		return bKq;
	}
		
	public boolean isFull(){
//		for(int i=0;i < iMaxSit; i++){
//			if(this.lSitList.get(i)==(byte)0)
//				return false;
//		}
//		return true;
		return this.bFake?true:this.iCurrentSit >= this.iMaxSit;
	}
	public Long getId(){
		return this.lId;
	}
	public String getName(){
		return this.sName;
	}
	public Byte getGameType(){
		return this.bGameType;
	}
	public byte getLobbyType(){
		return this.bLobbyType;
	}
	public Long getBet(){
		return this.lBetValue;
	}	
	public boolean isBigBet(){
		return this.iBigBet!=0;
	}
	public int getBigBet(){
		return this.iBigBet;
	}
	public byte getTip(){
		return this.bTip;
	}	
	public int getMaxViewers(){
		return this.iMaxViewer;
	}
	public int getMaxSit(){
		return this.iMaxSit;
	}
	public int getCurrentSit(){
		return this.iCurrentSit;
	}
	public synchronized int setCurrentSit(int iCurrentSit){
		return this.iCurrentSit = iCurrentSit;
	}
	public String getPassword(){
		return this.sPassword;
	}
	public synchronized String setPassword(String sPassword){
		return this.sPassword = sPassword;
	}
	public long getRequireCoin(){
		return this.lRequireCoin;
	}	
	public int getRequireLevel(){
		return this.iRequireLevel;
	}
	public List<Byte> getSitList(){
		return this.lSitList;
	}
	public List<Long> getPlayers(){
		return this.lPlayers==null?new ArrayList<Long>():this.lPlayers;
	}
	public ConcurrentSet<Long> getViewers(){
		return this.lViewers;
	}
	public List<Long> getAllUsers(){
		List<Long> kq = new ArrayList<>();
		Iterator<Long> itPlayers = this.lPlayers.iterator();
		while(itPlayers.hasNext()){
			Long uid = itPlayers.next();
			if(!kq.contains(uid))
				kq.add(uid);
		}
		Iterator<Long> itViewers = this.lViewers.iterator();
		while(itViewers.hasNext()){
			Long uid = itViewers.next();
			if(!kq.contains(uid))
				kq.add(uid);
		}
		return kq;
	}
	public byte getState(){
		return this.bState;
	}
	public long getTimeInState(){
		return System.currentTimeMillis() - this.lStateTime;
	}
	public int getHostIdx(){
		return this.iHostIdx;
	}
	public Long getHostId(){
		return this.lPlayers.get(this.iHostIdx);
	}
	public void setBigBet(int iBigBet){
		this.iBigBet = iBigBet;
	}
	public int randomBigBet(){
		if(iBigBet==0) return 0;
		iBigBet = new Random().nextInt(4)+1;
		return iBigBet;
	}
	public synchronized int setHostIdx(int iIdx){
		return this.iHostIdx = iIdx;
	}
	public synchronized byte setState(byte bState){
		this.lStateTime = System.currentTimeMillis();
		return this.bState = bState;
	}
	public synchronized Battle setBattle(Battle oBattle){
		if(this.oBattle!=null)
			this.oBattle.release();
		return this.oBattle = oBattle;
	}
	public Battle getBattle(){
		return this.oBattle;
	}
	public synchronized Dealer setDealer(Dealer oDealer){
		if(this.oDealer!=null)
			this.oDealer.release();
		return this.oDealer = oDealer;
	}
	public Dealer getDealer(){
		if(this.oDealer==null){
			this.setDealer(new Dealer(this));
		}
		return this.oDealer;
	}
	public List<Channel> getChannels(){
		List<Channel> lc = new ArrayList<>();
		Iterator<Long> itPlayers = this.getPlayers().iterator();
		while(itPlayers.hasNext()){
			Long uid = itPlayers.next();
			if (uid != null) {
				Channel c = Handshake.getChannel(uid, ChannelType.Game);
				if (c != null)
					lc.add(c);
			}
		}
		Iterator<Long> itViewers = this.getViewers().iterator();
		while(itViewers.hasNext()){
			Long uid = itViewers.next();
			if (uid != null && uid > 0) {
				Channel c = Handshake.getChannel(uid, ChannelType.Game);
				if (c != null)
					lc.add(c);
			}
		}
		return lc;
	}
	public List<Channel> getViewerChannels(){
		List<Channel> lc = new ArrayList<>();
		Iterator<Long> itViewers = this.getViewers().iterator();
		while(itViewers.hasNext()){
			Long uid = itViewers.next();
			if (uid != null && uid > 0) {
				Channel c = Handshake.getChannel(uid, ChannelType.Game);
				if (c != null)
					lc.add(c);
			}
		}
		return lc;
	}
	public void setDesc(String sDesc){
		this.sDesc = sDesc;
	}
	@Override
	public String toString(){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(ID, lId);
		data.put(NAME, sName);
		data.put(DESC, sDesc);
		data.put(GAMETYPE, bGameType);
		data.put(LOBBYTYPE, bLobbyType);
		data.put(BET, lBetValue);
		data.put(BIGBET, iBigBet);
		data.put(TIP, bTip);
		data.put(MAXSIT, iMaxSit);
		data.put(REQUIRECOIN, lRequireCoin);
		data.put(REQUIRELEVEL, iRequireLevel);
		data.put(PASSWORD, sPassword);
		data.put(STATE, bState);
		data.put(HOST, iHostIdx);
		data.put(SITLIST, lSitList);
		data.put(PLAYERS, lPlayers);
		data.put(VIEWERS, lViewers);
		return data.toString();
	}
	
	public RoomResponse toRoomResponse(String sTrid){
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		List<Long> ul = new ArrayList<>();
		Iterator<Long> itPlayers = this.getPlayers().iterator();
		while(itPlayers.hasNext()){
			Long uid = itPlayers.next();
			ul.add(uid==null?0:uid);
		}
		int irs = RoomResponse.createRoomResponse(builder, 
				builder.createString(sTrid), 
				bLobbyType,
				RoomInfo.createRoomInfo(builder, 
						lId, 
						builder.createString(sName), 
						lBetValue, 
						lRequireCoin, 
						iRequireLevel, 
						sPassword==null?0:builder.createString("*"),
						lSitList==null?0:RoomInfo.createSitListVector(builder, ArrayUtils.toPrimitive(lSitList.toArray(new Byte[lSitList.size()]))),
						bTip,
						bState, 
						RoomDetailInfo.createRoomDetailInfo(builder, 
								iHostIdx, 
								RoomDetailInfo.createPlayerIdVector(builder, ArrayUtils.toPrimitive(ul.toArray(new Long[ul.size()])))
							),
						builder.createString(sDesc),
						iBigBet
					)
			);
		builder.finish(irs);
		RoomResponse rs = RoomResponse.getRootAsRoomResponse(builder.dataBuffer());
		return rs;
	}
	public RoomUpdateInfo toRoomUpdateInfo(){
		Long remaining_time = lRemainingTime - (System.currentTimeMillis()-lStateTime);
		if(remaining_time<0l)
			remaining_time = 0l;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		List<Long> ul = new ArrayList<>();
		Iterator<Long> itPlayers = this.getPlayers().iterator();
		while(itPlayers.hasNext()){
			Long uid = itPlayers.next();
			ul.add(uid==null?0:uid);
		}
		int iruif = RoomUpdateInfo.createRoomUpdateInfo(builder, 
				lId, 
				bState, 
				remaining_time, 
				RoomDetailInfo.createRoomDetailInfo(builder, 
						iHostIdx, 
						RoomDetailInfo.createPlayerIdVector(builder, ArrayUtils.toPrimitive(ul.toArray(new Long[ul.size()])))
					)
			);
		builder.finish(iruif);
		RoomUpdateInfo ruif = RoomUpdateInfo.getRootAsRoomUpdateInfo(builder.dataBuffer());
		return ruif;
	}
	public RoomUpdateInfo toRoomUpdateInfo(Long time_remaining){
		if(time_remaining>=0)
			lRemainingTime = time_remaining;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		List<Long> ul = new ArrayList<>();
		Iterator<Long> itPlayers = this.getPlayers().iterator();
		while(itPlayers.hasNext()){
			Long uid = itPlayers.next();
			ul.add(uid==null?0:uid);
		}
		int iruif = RoomUpdateInfo.createRoomUpdateInfo(builder, 
				lId, 
				bState, 
				time_remaining, 
				RoomDetailInfo.createRoomDetailInfo(builder, 
						iHostIdx, 
						RoomDetailInfo.createPlayerIdVector(builder, ArrayUtils.toPrimitive(ul.toArray(new Long[ul.size()])))
					)
			);
		builder.finish(iruif);
		RoomUpdateInfo ruif = RoomUpdateInfo.getRootAsRoomUpdateInfo(builder.dataBuffer());
		return ruif;
	}
	
	public RoomInfo toRoomInfo(){
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		List<Long> ul = new ArrayList<>();
		Iterator<Long> itPlayers = this.getPlayers().iterator();
		while(itPlayers.hasNext()){
			Long uid = itPlayers.next();
			ul.add(uid==null?0:uid);
		}
		int irif = RoomInfo.createRoomInfo(builder, 
				lId, 
				builder.createString(sName), 
				lBetValue, 
				lRequireCoin, 
				iRequireLevel, 
				sPassword==null?0:builder.createString("*"), 
				lSitList==null?0:RoomInfo.createSitListVector(builder, ArrayUtils.toPrimitive(lSitList.toArray(new Byte[lSitList.size()]))),
				bTip,						
				bState, 
				RoomDetailInfo.createRoomDetailInfo(builder, 
						iHostIdx, 
						RoomDetailInfo.createPlayerIdVector(builder, ArrayUtils.toPrimitive(ul.toArray(new Long[ul.size()])))
					),
				builder.createString(sDesc),
				iBigBet
			);
		builder.finish(irif);
		RoomInfo rif = RoomInfo.getRootAsRoomInfo(builder.dataBuffer());
		return rif;
	}
	
	public int toRoomInfo(FlatBufferBuilder builder){
		List<Long> ul = new ArrayList<>();
		Iterator<Long> itPlayers = this.getPlayers().iterator();
		while(itPlayers.hasNext()){
			Long uid = itPlayers.next();
			ul.add(uid==null?0:uid);
		}
		return RoomInfo.createRoomInfo(builder, 
				lId, 
				builder.createString(sName), 
				lBetValue, 
				lRequireCoin, 
				iRequireLevel, 
				sPassword==null?0:builder.createString("*"), 
				lSitList==null?0:RoomInfo.createSitListVector(builder, ArrayUtils.toPrimitive(lSitList.toArray(new Byte[lSitList.size()]))), 
				bTip,
				bState, 
				RoomDetailInfo.createRoomDetailInfo(builder, 
						iHostIdx, 
						RoomDetailInfo.createPlayerIdVector(builder, ArrayUtils.toPrimitive(ul.toArray(new Long[ul.size()])))
					),
				builder.createString(sDesc),
				iBigBet
			);
	}
}
