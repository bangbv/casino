package vn.com.vng.gsmobile.casino.entries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.flatbuffers.GameType;

public class Lobby {
	public static String ID = "Id"; 
	public static String GAMETYPE = "GameType";
	public static String PLAYERS = "Players";
	
	private long lId = 0;
	private byte bGameType = GameType.None;
	private ConcurrentHashMap<Long, Object> lPlayers = null;
	
	public Lobby(byte bGameType, int... initSit){
		this.lId = bGameType;
		this.bGameType = bGameType;
		if(initSit.length>0)
			this.lPlayers = new ConcurrentHashMap<Long, Object>(initSit[0]);
		else
			this.lPlayers = new ConcurrentHashMap<Long, Object>();
	}
	
	public Long getId(){
		return this.lId;
	}
	public byte getGameType(){
		return this.bGameType;
	}
	public Set<Long> getPlayers(){
		return this.lPlayers.keySet();
	}
	
	public List<Channel> getChannels(){
		List<Channel> lc = new ArrayList<>();
		Iterator<Entry<Long, Object>> itPlayers = this.lPlayers.entrySet().iterator();
		while(itPlayers.hasNext()){
			Entry<Long, Object> e = itPlayers.next();
			Long uid = e.getKey();
			if (uid != null) {
				Channel c = Handshake.getChannel(uid, ChannelType.Game);
				if (c != null)
					lc.add(c);
			}
		}
		return lc;
	}
	
	public boolean join(Long sUID, Object obj){
		Channel c = Handshake.getChannel(sUID, ChannelType.Game);
		if(c!=null){
			this.lPlayers.put(sUID, obj);
			return true;
		}
		else
			return false;
	}
	
	public boolean leave(Long sUID){
		return this.lPlayers.remove(sUID)!=null;
	}
	
	@Override
	public String toString(){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(ID, lId);
		data.put(GAMETYPE, bGameType);
		data.put(PLAYERS, lPlayers);
		return data.toString();
	}
}
