package com.vng.gsmobile.casino.util;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.couchbase.client.java.document.JsonDocument;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class LocalCache {
	private static final int SIZE = 10000;
	private static final int EXPIRE_TIME = 1;
	private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

	private static Cache<String, Object> localCache;
	static {
		// create a cache for employees based on their employee id
		localCache = CacheBuilder.newBuilder().maximumSize(SIZE) // maximum
																	// 10000
																	// records
																	// can be
																	// cached
				.expireAfterAccess(EXPIRE_TIME, TIME_UNIT) // cache will expire
															// after 30 minutes
															// of access
				.build();
	}

	public static void put(String key, Object value) {
		localCache.put(key, value);
	}

	public static Object getFromCache(String key) {
		return localCache.getIfPresent(key);
	}

	@SuppressWarnings("unchecked")
	public static <Any> Any get(String key) {
		// get from cache
		Object obj = localCache.getIfPresent(key);
		if (obj == null) {
			// if not get from CB
			List<?> l = Lib.getDBGame(false).getCBConnection().get(key);
			if ((Boolean) l.get(0)) {
				JsonDocument j = (JsonDocument) l.get(1);
				if (j != null) {
					obj = j.content();
					put(key, obj);
				}
			}
		}
		return (Any) obj;
	}
}
