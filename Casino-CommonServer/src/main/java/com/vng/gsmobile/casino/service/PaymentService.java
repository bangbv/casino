package com.vng.gsmobile.casino.service;

import java.util.List;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.vng.gsmobile.casino.entity.Payment;
import com.vng.gsmobile.casino.entity.User;
import com.vng.gsmobile.casino.util.Lib;

public class PaymentService {

	public boolean save(String body) {
		JsonObject po = JsonObject.fromJson(body);
		if (po != null) {
			long bonusCash;
			long cash;
			JsonObject data = po.getObject(Payment.DATA);
			String appTraxId = data.getString(Payment.APP_TRANX_ID);
			String uid = appTraxId.split("_")[0];
			long amount = data.getLong(Payment.AMOUNT);
			long netAmount = data.getLong(Payment.NET_AMOUNT);
			JsonArray items = data.getArray(Payment.ITEMS);
			long itemPrice = items.getObject(0).getLong(Payment.ITEM_PRICE);
			int channel = data.getInt(Payment.CHANNEL);

			bonusCash = netAmount;
			if (channel == 5) {
				bonusCash = amount;
			}
			if ((channel == 33) || (channel == 34) || (channel == 35)) {
				bonusCash = itemPrice;
			}

			List<?> l = Lib.getDBGame(false).getCBConnection().get(User.USERESOURCE_TABLENAME + uid);
			if ((Boolean) l.get(0)) {
				JsonDocument urd = (JsonDocument) l.get(1);
				if (urd != null) {
					JsonObject ur = urd.content();
					cash = ur.getLong(User.CASH);
					cash = cash + bonusCash;
					ur.put(User.CASH, cash);
					Lib.getDBGame(false).getCBConnection().upsert(urd);
				}
			}
			JsonDocument npd = JsonDocument.create(Payment.PAYMENT_TABLENAME + appTraxId, data);
			Lib.getDBGame(false).getCBConnection().upsert(npd);
			return true;
		}
		return false;
	}
}
