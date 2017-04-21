package vn.com.vng.gsmobile.casino.ulti;

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.couchbase.client.java.document.JsonDocument;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;


public class LocalCache {
	
	private static int SIZE = 10000;
	private static int EXPIRE_TIME = 1;
	private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;
	
	static Cache<String, Object> localCache = null;
	
	static {
	      //create a cache
		localCache  = CacheBuilder.newBuilder()
	    		  .maximumSize(SIZE) // maximum 10000 records can be cached
	    		  .expireAfterWrite(EXPIRE_TIME, TIME_UNIT) // cache will expire after minutes of access
	    		  .build();		
	}

	public static void put(String key,Object value){
		localCache.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public static <Any> Any get(String key){
		// get from cache
		Object obj = localCache.getIfPresent(key);
		if(obj == null){
			// if not get from CB
			List<?> l = Lib.getDBGame(false).getCBConnection().get(key);
			System.out.println("Hit CB !");
			if ((Boolean) l.get(0)) {
				JsonDocument j = (JsonDocument) l.get(1);
				if (j != null) {
					obj = j.content();
					put(key,obj);
				}
			}
		}
		return (Any) obj;
	}
	
	public static Object getRankList(String key){
		return localCache.getIfPresent(key);
	}

	public static int getSIZE() {
		return SIZE;
	}

	public static void setSIZE(int sIZE) {
		SIZE = sIZE;
	}

	public static int getEXPIRE_TIME() {
		return EXPIRE_TIME;
	}

	public static void setEXPIRE_TIME(int eXPIRE_TIME) {
		EXPIRE_TIME = eXPIRE_TIME;
	}
}
