package com.vng.gsmobile.casino.Casino_CommonServer;

import java.util.Map;

import com.couchbase.client.java.document.json.JsonObject;
import com.vng.gsmobile.casino.entity.Shop;
import com.vng.gsmobile.casino.util.LocalCache;


public class AppTest {

	public static void main(String[] args) {
		JsonObject so = LocalCache.get(Shop.SHOP_TABLENAME + Shop.SHOP_PREFIX);
		JsonObject slo = so.getObject(Shop.SHOP_LIST);
		Map<String, Object> slm = slo.toMap();
		
		for (Map.Entry<String, Object> entry : slm.entrySet()) {
			String sKey = entry.getKey();
			JsonObject shop = slo.getObject(sKey);
			int costType = shop.getInt(Shop.COST_TYPE);
			if(costType == 12 || costType == 13){
				slo.removeKey(sKey);
			}
		}
		
		System.out.println(slo);
	}
}
