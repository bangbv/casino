package vn.com.vng.gsmobile.casino.schedules;

import java.util.Timer;
import java.util.TimerTask;

import vn.com.vng.gsmobile.casino.ulti.Lib;

public class MonitorScheduler {
	private class Monitor extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//noi dung canh bao tai day
			try {
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				Lib.getLogger().error(this.getClass().getName()+".run.catch:"+e1.getMessage());
			}

		}
		
	}
	long iPERIOD = 30000; // 5 phút 1 lần
	Timer tm = null;
	public MonitorScheduler(){
		tm = new Timer();
	}
	public MonitorScheduler(long iPERIOD){
		this.iPERIOD = iPERIOD;
		tm = new Timer();
	}
	public void start(){ 
		Lib.getLogger().info(this.getClass().getName()+".start:iPERIOD="+iPERIOD);
		tm.schedule(new Monitor(), 0, iPERIOD);
		Lib.getLogger().info(this.getClass().getName()+".start:OK");
	}
	public void stop(){
		Lib.getLogger().info(this.getClass().getName()+".stop...");
		tm.cancel();
		tm.purge();
		Lib.getLogger().info(this.getClass().getName()+".stop:OK");
	}
}
