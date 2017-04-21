package vn.com.vng.gsmobile.casino.schedules;

import java.util.Timer;
import java.util.TimerTask;

import vn.com.vng.gsmobile.casino.ulti.Lib;

public class GCScheduler {
	private class GC extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				System.gc();
			}catch(Exception e){
				Lib.getLogger().error(this.getClass().getName()+".run.catch:"+Lib.getStackTrace(e));
			}
		}
		
	}
	long iPERIOD = 30000; // 30 giây 1 lần
	Timer tm = null;
	public GCScheduler(){
		tm = new Timer();
	}
	public GCScheduler(long iPERIOD){
		this.iPERIOD = iPERIOD;
		tm = new Timer();
	}
	public void start(){ 
		Lib.getLogger().info(this.getClass().getName()+".start:iPERIOD="+iPERIOD);
		tm.schedule(new GC(), 0, iPERIOD);
		Lib.getLogger().info(this.getClass().getName()+".start:OK");
	}
	public void stop(){
		Lib.getLogger().info(this.getClass().getName()+".stop...");
		tm.cancel();
		tm.purge();
		Lib.getLogger().info(this.getClass().getName()+".stop:OK");
	}
}
