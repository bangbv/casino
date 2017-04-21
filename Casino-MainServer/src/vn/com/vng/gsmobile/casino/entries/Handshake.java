package vn.com.vng.gsmobile.casino.entries;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import vn.com.vng.gsmobile.casino.connector.CLConnector;
import vn.com.vng.gsmobile.casino.ulti.Const;

public class Handshake {
    public static CLConnector mainWebsocket = new CLConnector("Main Websocket");
    public static CLConnector gameWebsocket = new CLConnector("Game Websocket");
    public static ConcurrentHashMap<String, Checker> pingList = new ConcurrentHashMap<String, Checker>();
	public static void closeChannel(Long uid, byte... bChannelType){
		if(uid == null) return;
		byte bChType = bChannelType.length>0?bChannelType[0]:ChannelType.Any;
		switch(bChType){
		case ChannelType.Any:
			Handshake.gameWebsocket.remove(uid);
			Handshake.mainWebsocket.remove(uid);
			break;
		case ChannelType.Main:
			Handshake.mainWebsocket.remove(uid);
			break;
		case ChannelType.Game:
			Handshake.gameWebsocket.remove(uid);
			break;			
		}
	}
	public static void closeChannel(Channel c, byte... bChannelType){
		if(c == null) return;
		byte bChType = bChannelType.length>0?bChannelType[0]:ChannelType.Any;
		switch(bChType){
		case ChannelType.Any:
			Handshake.gameWebsocket.remove(c);
			Handshake.mainWebsocket.remove(c);
			break;
		case ChannelType.Main:
			Handshake.mainWebsocket.remove(c);
			break;
		case ChannelType.Game:
			Handshake.gameWebsocket.remove(c);
			break;			
		}
	}
	public static Channel getChannel(Long uid, byte... bChannelType){
		if(uid == null) return null;
		Channel c = null;
		byte bChType = bChannelType.length>0?bChannelType[0]:ChannelType.Any;
		switch(bChType){
		case ChannelType.Any:
			c = Handshake.gameWebsocket.getChannel(uid);
			if(c == null)
				c = Handshake.mainWebsocket.getChannel(uid);
			break;
		case ChannelType.Main:
			c = Handshake.mainWebsocket.getChannel(uid);
			break;
		case ChannelType.Game:
			c = Handshake.gameWebsocket.getChannel(uid);
			break;			
		}
		return c;
	}
	public static Long getUser(Channel c, byte... bChannelType){
		if(c == null) return null;
		Long uid = null;
		byte bChType = bChannelType.length>0?bChannelType[0]:ChannelType.Any;
		switch(bChType){
		case ChannelType.Any:
			uid = Handshake.gameWebsocket.getUser(c);
			if(uid == null)
				uid = Handshake.mainWebsocket.getUser(c);
			break;
		case ChannelType.Main:
			uid = Handshake.mainWebsocket.getUser(c);
			break;
		case ChannelType.Game:
			uid = Handshake.gameWebsocket.getUser(c);
			break;			
		}
		return uid;
	}
	public static byte getChannelType(Channel c){
		if(Handshake.gameWebsocket.getUsers().containsKey(c.id().toString()))
			return ChannelType.Game;
		else if(Handshake.mainWebsocket.getUsers().containsKey(c.id().toString()))
			return ChannelType.Main;
		else
			return ChannelType.None;
	}
	public static boolean checkChannel(Long uid, byte... bChannelType){
		return ping(getChannel(uid, bChannelType));
	}
	private static boolean ping(Channel c){
		if(c==null) return false;
		Checker checker = new Checker();
		try{
			c.writeAndFlush(new PingWebSocketFrame());
			pingList.put(c.id().toString(), checker);
			System.out.println(Arrays.asList("server check network", "ping", c.id()));
			synchronized (checker) {
				checker.wait(Const.PING_TIMEOUT);//chờ phản hồi 1 giây
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			pingList.remove(c.id().toString());
		}
		return checker.getResult();
	}
	
	public static boolean verify(Channel c, Long uid, byte... bChannelType){
		if(c == null || uid == null || uid == 0) return false;
		Long uid2 = null;
		byte bChType = bChannelType.length>0?bChannelType[0]:ChannelType.Any;
		switch(bChType){
		case ChannelType.Any:
			uid2 = Handshake.gameWebsocket.getUser(c);
			if(uid2 == null || uid2 == 0)
				uid2 = Handshake.mainWebsocket.getUser(c);
			break;
		case ChannelType.Main:
			uid2 = Handshake.mainWebsocket.getUser(c);
			break;
		case ChannelType.Game:
			uid2 = Handshake.gameWebsocket.getUser(c);
			break;			
		}
		return uid.equals(uid2);
	}
}
