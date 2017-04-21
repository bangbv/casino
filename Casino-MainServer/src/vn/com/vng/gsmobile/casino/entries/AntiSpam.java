package vn.com.vng.gsmobile.casino.entries;

public class AntiSpam{
	private int count = 0;
	private long last_time = 0;
	private long pause_start_time = 0;
	
	private int limit_time = 0;
	private int limit_count = 0;
	private int limit_pause_time = 0;
	
	public AntiSpam(int limit_time, int limit_count, int limit_pause_time){
		this.limit_time = limit_time;
		this.limit_count = limit_count;
		this.limit_pause_time = limit_pause_time;
	}
	public boolean valid(){
		boolean bKq = true;
		if(count > 0){
			if((System.currentTimeMillis() - last_time)/1000 > limit_time){
				if(count > limit_count){
					if(pause_start_time==0)
						pause_start_time = System.currentTimeMillis();
					long pause_time = (System.currentTimeMillis()-pause_start_time)/1000;
					if(pause_time > limit_pause_time){
						this.count = 0;
						this.last_time = 0;
						this.pause_start_time = 0;
					}
					else
						bKq = false;
				}
			}
			else
				bKq =  false;
		}
		
		if(bKq){
			this.count += 1;
			this.last_time = System.currentTimeMillis();
		}
		return bKq;
	}
}