package vn.com.vng.gsmobile.casino.ulti;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;

import vn.com.vng.gsmobile.casino.entries.Gift;
import vn.com.vng.gsmobile.casino.entries.GiftName;
import vn.com.vng.gsmobile.casino.entries.User;

public class SendSystemGift {

	public static void main(String[] args) throws Exception {
		String uid = args[0];
		//String uid = "3";
		JsonObject g = (JsonObject) LocalCache.get(Gift.GIFT_TABLENAME + uid);
		JsonArray gl = null;
		if (g != null) {
			gl = g.getArray(User.GIFT_LIST);
		} else {
			g = JsonObject.create();
			gl = JsonArray.create();
			g.put(Gift.GIFT_LIST, gl);
		}
		JsonObject igo = JsonObject.create();
		igo.put(Gift.GIFT_ID, String.valueOf(System.currentTimeMillis()));
		igo.put(Gift.GIFT_NAME, GiftName.name(GiftName.GIFTSYSTEM));
		igo.put(Gift.GIFT_TYPE, 1);
		igo.put(Gift.DATE, new DateUtil().getCurrentDate());
		igo.put(Gift.GIFT_VALUE, (long) 10000);
		igo.put(Gift.EXPIRED_TIME, (long) 0);
		igo.put(Gift.CONSECUTIVE_DAY, 0);
		igo.put(Gift.STATUS, 1);
		gl.add(igo);
		JsonDocument nd = JsonDocument.create(Gift.GIFT_TABLENAME + uid, g);
		Lib.getDBGame(false).getCBConnection().upsert(nd);
		System.out.println("finish !");
	}
}
