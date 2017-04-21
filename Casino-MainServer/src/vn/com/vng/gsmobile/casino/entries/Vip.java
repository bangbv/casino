package vn.com.vng.gsmobile.casino.entries;

import com.couchbase.client.java.document.json.JsonObject;

import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class Vip {
	public static final String VIP_TABLENAME = "25_";
	public static final String VIP_LIST = "vip_list";
	public static final String VIP_TYPE = "vip_type";
	public static final String VALUE_DAILY = "value_daily";
	public static final String VALUE_IMME = "value_imme";
	public static final String DAYS = "days";
	public static final String BUY_COIN_PROMO = "buy_coin_promo";
	public static final String SHOP_ID = "shop_id";
	public static final String DATE = "date";
	public static final String EXPIRED_TIME = "expiredTime";
	public static final String STATUS = "status";

	private static JsonObject database = null;

	public synchronized static JsonObject getVipBase() {
		if (database == null) {
			database = (JsonObject) LocalCache.get(Const.VIP_ID);
		}
		return database;
	}

}
