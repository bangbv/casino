package vn.com.vng.gsmobile.casino.schedules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang.ArrayUtils;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.business.Service;
import vn.com.vng.gsmobile.casino.entries.CMD;
import vn.com.vng.gsmobile.casino.entries.ChannelType;
import vn.com.vng.gsmobile.casino.entries.Handshake;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.flatbuffers.LobbyRoom;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class RoomPushScheduler {
	public static int max_consume = 20;//số tối xử lý 1 lần
	public boolean running = false;
	public byte bGameType = 0;
	public byte bLobbyType = 0;
	public synchronized void setRunning(boolean isRunning){
		running = isRunning;
	}
	private class RoomPushService extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!running && !Const.IS_STARTING && !Const.IS_STOPPING)
				try{
					setRunning(true);
					String sTrid = Const.SERVER_HOST+"_"+System.currentTimeMillis();
					List<Channel> lc = null;
					List<Integer> l = new ArrayList<>();
					FlatBufferBuilder builder = new FlatBufferBuilder(0);
					ConcurrentHashMap<Byte, ConcurrentHashMap<Long, Object>> pGame = RoomManager.getPushList().get(bGameType);
					if(pGame!=null){
						ConcurrentHashMap<Long, Object> pLobby = pGame.get(bLobbyType);
						boolean isPushLobbyRoom = false;
						if(pLobby!=null){
							Iterator<Entry<Long, Object>> it = pLobby.entrySet().iterator();
							int cnt = max_consume;
							while(it.hasNext() && cnt > 0){
								Entry<Long, Object> e = it.next();
								try{
									Room r = RoomManager.getRoom(e.getKey());
									if(r!=null){
										//1. push roomupdateinfo tới người chơi/xem trong room
										lc = r.getChannels();
										Service.sendToClient(
												RoomPushService.class.getSimpleName(), 
												sTrid+"_"+e.getKey(), Service.CMDTYPE_REQUEST, 
												lc,
												Arrays.asList(CMD.PUSH_UPDATEROOM.cmd,CMD.PUSH_UPDATEROOM.subcmd,CMD.PUSH_UPDATEROOM.version,(byte)0,r.toRoomUpdateInfo())//-1l))							
											);
										//2. add danh sách roominfo để push người chơi trong lobby
										l.add(r.toRoomInfo(builder));
										isPushLobbyRoom = true;
									}
								}catch(Exception ex){
									Lib.getLogger().error(this.getClass().getName()+".run["+e.getKey()+"]:"+Lib.getStackTrace(ex));
								}
								it.remove();
								cnt--;
								if(lc!=null)
									lc.clear();
								lc = null;
							}
							//3. push room tới người chơi trong lobby
							if(isPushLobbyRoom){
								lc = new ArrayList<>();
								for(Long uid : RoomManager.getLobby(bGameType).getPlayers()){
									if(uid!=null && uid > 0){
										Channel c = Handshake.getChannel(uid, ChannelType.Game);
										if(c != null)
											lc.add(c);
									}
								}
								int ilr = LobbyRoom.createLobbyRoom(builder, 
										bLobbyType, 
										bGameType, 
										LobbyRoom.createRoomListVector(builder, ArrayUtils.toPrimitive(l.toArray(new Integer[l.size()])))
									);
								builder.finish(ilr);
								LobbyRoom lr = LobbyRoom.getRootAsLobbyRoom(builder.dataBuffer());
								Service.sendToClient(
										RoomPushService.class.getSimpleName(), 
										sTrid, Service.CMDTYPE_REQUEST, 
										lc,
										Arrays.asList(CMD.PUSH_LOBBYROOM.cmd,CMD.PUSH_LOBBYROOM.subcmd,CMD.PUSH_LOBBYROOM.version,(byte)0,lr)							
									);
							}
						}
					}
				}catch(Exception e){
					Lib.getLogger().error(this.getClass().getName()+".run.catch:"+Lib.getStackTrace(e));
				}	
				finally {
					setRunning(false);
				}
		}
	}
	long iPERIOD = 1000; // 1 giây 1 lần
	Timer tm = null;
	public RoomPushScheduler(byte bGameType, byte bLobbyType){
		this.bGameType = bGameType;
		this.bLobbyType = bLobbyType;
		tm = new Timer();
	}
	public RoomPushScheduler(long iPERIOD, byte bGameType, byte bLobbyType){
		this.iPERIOD = iPERIOD;
		this.bGameType = bGameType;
		this.bLobbyType = bLobbyType;
		tm = new Timer();
	}
	public void start(){ 
		Lib.getLogger().info(Arrays.asList(bGameType, bLobbyType, iPERIOD, this.getClass().getSimpleName()+".start:..."));
		tm.schedule(new RoomPushService(), iPERIOD, iPERIOD);
		Lib.getLogger().info(Arrays.asList(bGameType, bLobbyType, iPERIOD, this.getClass().getSimpleName()+".start:OK"));
	}
	public void stop(){
		Lib.getLogger().info(Arrays.asList(bGameType, bLobbyType, iPERIOD, this.getClass().getSimpleName()+".stop:..."));
		tm.cancel();
		tm.purge();
		Lib.getLogger().info(Arrays.asList(bGameType, bLobbyType, iPERIOD, this.getClass().getSimpleName()+".stop:OK"));
	}
}
