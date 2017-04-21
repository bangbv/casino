package vn.com.vng.gsmobile.casino.bot;

import java.net.ServerSocket;
import java.net.Socket;
import vn.com.vng.gsmobile.casino.entries.Bot;
import vn.com.vng.gsmobile.casino.schedules.BotConnectScheduler;
import vn.com.vng.gsmobile.casino.schedules.GCScheduler;
import vn.com.vng.gsmobile.casino.schedules.MonitorScheduler;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class BotClass {
	public static BotConnectScheduler cs = null;
	public static GCScheduler gc = null;
	public static MonitorScheduler ms = null;
	public static ServerSocket ss = null;
    public static String APPCFG = "bot.cfg";
	public static void main(String[] args){
		// TODO Auto-generated method stub
		if(!Lib.loadConfig(false, APPCFG)) System.exit(0);
		try{
			Lib.getLogger().info(BotClass.class.getName()+".start:...");
			Const.IS_STARTING = true;
			//init here
			Bot.init();
			cs  = new BotConnectScheduler(Const.BOT_CONNECT_SCHEDULE);
			cs.start();
			Const.IS_STARTING = false;
			Lib.getLogger().info(BotClass.class.getName()+".start: OK");
			//telnet control here
			ss = new ServerSocket(Const.BOT_CONTROL_PORT);
			Socket s = null;
			while (true) {
				s = ss.accept();
				new Thread(new BotControl(s)).start();
            }
		}catch(Exception e){
			Lib.getLogger().fatal(BotClass.class.getName()+".start:"+Lib.getStackTrace(e));
			System.exit(0);
		}
	}
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				//Lưu ý: cần tắt theo thứ tự
				try{
					Lib.getLogger().info(BotClass.class.getName()+".shutdown:...");
					Const.IS_STOPPING = true;
					if(cs!=null) cs.stop();
					//release here
					Bot.release();
					Lib.getDBGame(false).close();
					Lib.getDBLog(false).close();
					Lib.getRedisGame(false).close();
					if(ss!=null) ss.close();
					Lib.getLogger().info(BotClass.class.getName()+".shutdown:OK");
				}catch(Exception e){
					Lib.getLogger().error(BotClass.class.getName()+".shutdown:"+Lib.getStackTrace(e));
				}
			}
		});
	}	
}
