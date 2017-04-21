package vn.com.vng.gsmobile.casino.entries;

import java.util.concurrent.TimeUnit;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class AntiSpamManager {

	private Cache<Long, AntiSpam> cache = CacheBuilder.newBuilder()
	    		  .maximumSize(10000)
	    		  .expireAfterAccess(60, TimeUnit.MINUTES)
	    		  .build();		
	private int limit_time = 5;
	private int limit_count = 10;
	private int limit_pause_time = 60;
	
	public AntiSpam get(long uid){
		AntiSpam a = cache.getIfPresent(uid);
		if(a==null){
			a = new AntiSpam(limit_time, limit_count, limit_pause_time);
			cache.put(uid, a);
		}
		return a;
	}
	
	public AntiSpamManager(int limit_time, int limit_count, int limit_pause_time){
		this.limit_time = limit_time;
		this.limit_count = limit_count;
		this.limit_pause_time = limit_pause_time;
	}
}
