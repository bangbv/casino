package vn.com.vng.gsmobile.casino.ulti;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;


public class CBObject {
	
	private static int SIZE = 10000;
	private static int EXPIRE_TIME = 1;
	private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;
	
	static Cache<String, Object> localCache = null;
	
	static {
	      //create a cache
		localCache  = CacheBuilder.newBuilder()
	    		  .maximumSize(SIZE)
	    		  .expireAfterWrite(EXPIRE_TIME, TIME_UNIT)
	    		  .build();		
	}

	private final Map<String, Object> content;
	
	private CBObject(){
		content = new HashMap<String, Object>();
	}	
	
	public static CBObject create(){
		return new CBObject();
	}
	
	public void put(String key,Object value){
		localCache.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <Any> Any load(String key){
		// get from cache
		Object obj = localCache.getIfPresent(key);
		if(obj == null){
			// if not get from CB
			List<?> l = Lib.getDBGame(false).getCBConnection().get(key);
			if ((Boolean) l.get(0)) {
				JsonDocument j = (JsonDocument) l.get(1);
				if (j != null) {
					obj = j.content();
					//content = toMap();
					put(key,obj);
				}
			}
		}
		return (Any) obj;
	}

	public Object get(String name){		
		return content.get(name);
	}
	
    private Map<String, Object> toMap() {
        Map<String, Object> copy = new HashMap<String, Object>(content.size());
        for (Map.Entry<String, Object> entry : content.entrySet()) {
            Object content = entry.getValue();
            if (content instanceof JsonObject) {
                copy.put(entry.getKey(), ((JsonObject) content).toMap());
            } else if (content instanceof JsonArray) {
                copy.put(entry.getKey(), ((JsonArray) content).toList());
            } else {
                copy.put(entry.getKey(), content);
            }
        }
        return copy;
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
