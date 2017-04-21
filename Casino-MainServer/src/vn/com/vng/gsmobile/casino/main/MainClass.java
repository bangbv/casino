package vn.com.vng.gsmobile.casino.main;

import java.net.ServerSocket;
import java.net.Socket;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import vn.com.vng.gsmobile.casino.entries.BuyPending;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.entries.ServerType;
import vn.com.vng.gsmobile.casino.schedules.BuyPendingScheduler;
import vn.com.vng.gsmobile.casino.schedules.GCScheduler;
import vn.com.vng.gsmobile.casino.schedules.LobbyCleanScheduler;
import vn.com.vng.gsmobile.casino.schedules.MonitorScheduler;
import vn.com.vng.gsmobile.casino.schedules.RoomAllocScheduler;
import vn.com.vng.gsmobile.casino.schedules.RoomStateScheduler;
import vn.com.vng.gsmobile.casino.server.Server;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class MainClass {
	public static GCScheduler gc = null;
	public static MonitorScheduler ms = null;
	public static RoomStateScheduler rs = null;
	public static RoomAllocScheduler ra = null;
	public static LobbyCleanScheduler lc = null;
	public static BuyPendingScheduler bp = null;
	public static ServerSocket ss = null;
	static EventLoopGroup bg = null;
	static EventLoopGroup wg = null;
    static EventExecutorGroup eg = null;
    public static String APPCFG = "main.cfg";
	public static void main(String[] args){
		// TODO Auto-generated method stub
		if(!Lib.loadConfig(false, APPCFG)) System.exit(0);
		try{
			Lib.getLogger().info(MainClass.class.getName()+".start:...");
			Const.IS_STARTING = true;
			bg = new NioEventLoopGroup();
			wg = new NioEventLoopGroup();
			Server pvp = null;
			if(Const.IS_BUSINESS_SLOWLY){
				eg = new DefaultEventExecutorGroup(Const.MAX_THREADS_EXECUTE_MESSAGE);
				pvp = new Server(Const.SERVER_PORT, bg, wg, eg);
			}
			else
				pvp = new Server(Const.SERVER_PORT, bg, wg);
			pvp.start();	
			if(pvp.getStatus()!=0){//on or initing
				//GC schedule
				gc = new GCScheduler(Const.GC_SCHEDULE); 
				gc.start();
				//Monitor schedule
				ms = new MonitorScheduler(Const.MONITOR_SCHEDULE);
				ms.start();
				//All server schedule
				//MainServer schedule
				if(Const.SERVER_TYPE == ServerType.AllInOne || Const.SERVER_TYPE == ServerType.Main){
				}
				//GameServer schedule
				if(Const.SERVER_TYPE == ServerType.AllInOne || Const.SERVER_TYPE == ServerType.Game){
					RoomManager.init();
					rs = new RoomStateScheduler(Const.ROOMSTATE_SCHEDULE);
					rs.start();
					ra = new RoomAllocScheduler(Const.ROOMALLOC_SCHEDULE);
					ra.start();
					lc = new LobbyCleanScheduler(Const.LOBBYCLEAN_SCHEDULE);
					lc.start();
				}
				BuyPending.init();
				bp = new BuyPendingScheduler(Const.BUYPENDING_SCHEDULE);
				bp.start();
				Const.IS_STARTING = false;
				Lib.getLogger().info(MainClass.class.getName()+".start: OK");
				//telnet control here
				ss = new ServerSocket(Const.CONTROL_PORT);
				Socket s = null;
				while (true) {
					s = ss.accept();
					new Thread(new MainControl(s)).start();
	            }
			}
		}catch(Exception e){
			Lib.getLogger().fatal(MainClass.class.getName()+".start:"+Lib.getStackTrace(e));
			System.exit(0);
		}
	}
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				//Lưu ý: cần tắt theo thứ tự
				try{
					Lib.getLogger().info(MainClass.class.getName()+".shutdown:...");
					Const.IS_STOPPING = true;
					if(bp!=null) bp.stop();
					BuyPending.release();
					Lib.getLogger().info(MainClass.class.getName()+":notice all user and wait 30s...");
					//Notice to all user here
					//Thread.sleep(30000);
					Lib.getLogger().info(MainClass.class.getName()+":wait battle running about max 300s...");
					if(gc!=null) gc.stop();
					if(ms!=null) ms.stop();
					if(Const.SERVER_TYPE == ServerType.AllInOne || Const.SERVER_TYPE == ServerType.Game){
						if(lc!=null) lc.stop();
						if(ra!=null) ra.stop();
						if(rs!=null) rs.stop();
						RoomManager.close();
					}
					Lib.getDBGame(false).close();
					Lib.getDBLog(false).close();
					Lib.getRedisGame(false).close();
		            if(wg!=null) wg.shutdownGracefully().sync();
		            if(bg!=null) bg.shutdownGracefully().sync();
		            if(eg!=null) eg.shutdownGracefully().sync();
					if(ss!=null) ss.close();
					Lib.getLogger().info(MainClass.class.getName()+".shutdown:OK");
				}catch(Exception e){
					Lib.getLogger().error(MainClass.class.getName()+".shutdown:"+Lib.getStackTrace(e));
				}
			}
		});
	}	
}
