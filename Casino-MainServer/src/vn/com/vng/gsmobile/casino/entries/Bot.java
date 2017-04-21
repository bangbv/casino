package vn.com.vng.gsmobile.casino.entries;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import io.netty.channel.ChannelFuture;
import vn.com.vng.gsmobile.casino.client.Client;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDJoinLobby;
import vn.com.vng.gsmobile.casino.flatbuffers.GameType;
import vn.com.vng.gsmobile.casino.flatbuffers.LobbyType;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class Bot {
	public static ConcurrentHashMap<Long, Bot> list = new ConcurrentHashMap<Long, Bot>();
	Long uid = null;
	Byte gameid = null;
	Long bid = null;
	Long rid = null;
	Integer playerIdx = null;
	Client connector = null;
	public String url = null;
	public long lasttime = 0l;
	public CONNECT_STATUS connectstatus = CONNECT_STATUS.INIT;
	
	public Bot(Long uid) {
		this.uid = uid;
	}
	public Long getId(){
		return uid;
	}
	public Byte getGameType(){
		return gameid;
	}
	public void setGameType(Byte game_type){
		gameid = game_type;
	}
//	public Long getBattleId(){
//		return this.bid;
//	}
//	public void setBattleId(Long bid){
//		this.bid = bid;
//	}
	public Long getRoomId(){
		return this.rid;
	}
	public void setRoomId(Long rid){
		this.rid = rid;
	}
	public Integer getPlayerIdx(){
		return this.playerIdx;
	}
	public void setPlayerIdx(Integer playerIdx){
		this.playerIdx = playerIdx;
	}
	public boolean connect(Object...params){
		close();
		if(connector==null)
			connector = new Client(uid);
		if(params.length>1){
			this.url = (String) params[0];
			this.gameid = (Byte) params[1];
		}else if(params.length>0){
			this.url = (String) params[0];
			this.gameid = null;
		}
		boolean kq = connector.connect(this.url, this.uid, this.gameid);
		if(kq){
			joinGame();
			this.lasttime = System.currentTimeMillis();
			this.connectstatus = CONNECT_STATUS.CONNECTED;
		}
		else
			this.connectstatus = CONNECT_STATUS.RECONNECT;
		return kq;
	}
	public boolean close(){
		boolean bKq = false;
		if(connector!=null) {
			bKq = connector.close();
			connector = null;
		}
		this.lasttime = 0l;
		return bKq;
	}
	public boolean isActive(){
		return connector!=null && connector.isActive();
	}
	public ChannelFuture write(CMD cmd, Table data){
		return connector!=null?connector.write(cmd, data):null;
	}
	public void reconnect(){
    	this.connectstatus = CONNECT_STATUS.RECONNECT;
	}
	
	//////////////////BOT MANAGER/////////////////////////
	public synchronized static void init(){
		//1. Đóng các BOT đã lên TOP
		//2. Tải các BOT khác lên
		Map<Long, Byte> bots = new HashMap<Long, Byte>();
		bots.put(73319113887399936l, GameType.TLMN);
		bots.put(49612943943368704l, GameType.MAUBINH);
		bots.put(74783075770384384l, GameType.BALA);
		//bots.put(73530605895237632l, GameType.MAUBINH);
		
		for(Entry<Long, Byte> e: bots.entrySet()){
			if(!list.containsKey(e.getKey())){
				Bot bot = new Bot(e.getKey());
				bot.setGameType(e.getValue());
				list.put(e.getKey(), bot);
			}
		}
	}
	public synchronized static void release(){
		list.clear();
		list = null;
		Lib.getLogger().info("release:OK");
	}
	
	/////////////////////BOT ACTION///////////////////////
	public void joinGame(){
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		builder.finish(CMDJoinLobby.createCMDJoinLobby(builder, uid, 0, getGameType(), LobbyType.Lobby_1));
		CMDJoinLobby jl = CMDJoinLobby.getRootAsCMDJoinLobby(builder.dataBuffer());
		write(CMD.JOIN_LOBBY, jl);
	}
	public void quitGame(){
		
	}
}
