package vn.com.vng.gsmobile.casino.schedules;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import vn.com.vng.gsmobile.casino.entries.Bot;
import vn.com.vng.gsmobile.casino.entries.CMD;
import vn.com.vng.gsmobile.casino.entries.CONNECT_STATUS;
import vn.com.vng.gsmobile.casino.flatbuffers.GameServerInfo;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class BotConnectScheduler {
	public static int max_consume = 1000;
	public static boolean running = false;
	public synchronized static void setRunning(boolean isRunning){
		running = isRunning;
	}
	private class ConnectService extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!running && !(Const.IS_STARTING || Const.IS_STOPPING))
			try{
				setRunning(true);
				int cnt = max_consume;
				Iterator<Entry<Long, Bot>> it = Bot.list.entrySet().iterator();
				while(cnt > 0 && it.hasNext()){
					if(!Const.IS_STOPPING){
						Bot b = it.next().getValue();
						try{
							switch(b.connectstatus){
							case INIT:
							case RECONNECT:
								b.connectstatus = CONNECT_STATUS.CONNECTING;
								b.connect("ws://120.138.76.130:8080/websocket", b.getGameType());
								b.setRoomId(null);
								cnt -= 1;
								break;
							case CLOSE:
								b.connectstatus = CONNECT_STATUS.CLOSING;
								b.close();
								it.remove();
								b = null;
								cnt -= 1;
								break;
							case CONNECTED:
								if(b.getRoomId()==null){
									Table data = null;
									FlatBufferBuilder builder = new FlatBufferBuilder(0);
									builder.finish(GameServerInfo.createGameServerInfo(builder, b.getId(), b.getGameType(), 0, 0, 0));
									data = GameServerInfo.getRootAsGameServerInfo(builder.dataBuffer());
									b.write(CMD.ROOM_WAIT, data);
								}
								break;
							default:
								break;
							}
						}catch(Exception e){
							b.connectstatus = CONNECT_STATUS.CLOSING;
							b.close();
							it.remove();
							Lib.getLogger().error(ConnectService.class.getName()+".run:"+Arrays.asList(b.getId(), Lib.getStackTrace(e)));
							b = null;
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
	public BotConnectScheduler(){
		tm = new Timer();
	}
	public BotConnectScheduler(long iPERIOD){
		this.iPERIOD = iPERIOD;
		tm = new Timer();
	}
	public void start(){ 
		try{
			Lib.getLogger().info(this.getClass().getName()+".start:iPERIOD="+iPERIOD);
			tm.schedule(new ConnectService(), iPERIOD, iPERIOD);
			Lib.getLogger().info(this.getClass().getName()+".start:OK");
		}catch(Exception e){
			Lib.getLogger().error(this.getClass().getName()+".start:"+Lib.getStackTrace(e));
		}
	}
	public void stop(){
		Lib.getLogger().info(this.getClass().getName()+".stop...");
		tm.cancel();
		tm.purge();
		Lib.getLogger().info(this.getClass().getName()+".stop:OK");
	}
}
