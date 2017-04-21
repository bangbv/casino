package com.vng.gsmobile.casino.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.vng.gsmobile.casino.entity.BuyStatusType;
import com.vng.gsmobile.casino.entity.CallBackResponse;
import com.vng.gsmobile.casino.entity.Payment;
import com.vng.gsmobile.casino.entity.PaymentType;
import com.vng.gsmobile.casino.entity.Shop;
import com.vng.gsmobile.casino.entity.User;
import com.vng.gsmobile.casino.entity.UserPending;
import com.vng.gsmobile.casino.entity.Wallet;
import com.vng.gsmobile.casino.util.Const;
import com.vng.gsmobile.casino.util.ErrorCode;
import com.vng.gsmobile.casino.util.Lib;
import com.vng.gsmobile.casino.util.LocalCache;

@RestController
public class GameBillingController {

	@RequestMapping(value = "/gameBillingCb", method = RequestMethod.POST)
	public Object gameBillingCallback(@RequestBody String body) {
		JsonObject io = JsonObject.fromJson(body);
		System.out.println(body);
		if (io != null) {
			String trans = io.getString(Wallet.TRANSACTION_ID);
			String userId = io.getString(Wallet.USER_ID);
			Long uid = Long.valueOf(userId);
			String gameId = io.getString(Wallet.GAME_ID);
			String serverId = io.getString(Wallet.SERVER_ID);
			String itemId = io.getString(Wallet.ITEM_ID);
			long amount = io.getLong(Wallet.AMOUNT);
			String appTrans = io.getString(Wallet.APP_TRANS_ID);
			String addInfo = io.getString(Wallet.ADD_INFO);
			String sig = io.getString(Wallet.SIG);
			String local_sig = Lib.md5(trans
					+ userId + gameId + serverId + itemId + amount
					+ appTrans + addInfo + Const.WALLET_KEY);
			if (sig.equalsIgnoreCase(local_sig)) {				
				JsonObject ur = Lib.getCB(User.USERESOURCE_TABLENAME+uid);
				JsonObject po = Lib.getCB(Payment.PAYMENT_TABLENAME+appTrans);
				JsonObject upo = Lib.getCB(UserPending.USERPEDING_TB+appTrans);
				JsonObject so = LocalCache.get(Shop.SHOP_TABLENAME+Shop.SHOP_PREFIX);
				if(po != null){
					return buildErrorCode(Wallet.TRANSACTION_DUP, "Transaction duplicated !");
				}
				if(ur != null && so != null){
					po = JsonObject.create();
					po.put(Payment.TYPE,Payment.TYPE_VALUE);
					po.put(Payment.PAYMENT_TYPE, PaymentType.Wallet);
					po.put(Payment.TRANS_ID, trans);
					po.put(Payment.UID, uid);
					po.put(Payment.GAME_ID, gameId);
					po.put(Payment.SERVER_ID, serverId);
					po.put(Payment.ITEM_ID, itemId);
					po.put(Payment.AMOUNT, amount);
					po.put(Payment.APP_TRANS_ID, appTrans);
					po.put(Payment.ADD_INFO, addInfo);
					
					JsonObject slo = so.getObject(Shop.SHOP_LIST);
					JsonObject ito = slo.getObject(itemId);
					long cost_value = ito.getLong(Shop.COST_VALUE);
					long cash = ur.getLong(User.CASH);
					cash = cash + cost_value;
					ur.put(User.CASH, cash);
					
					if(upo != null){
						if(upo.getInt(UserPending.STATUS) == BuyStatusType.Pending){
							upo.put(UserPending.STATUS, BuyStatusType.Receipt);
						}
					}
					
					JsonDocument urd = JsonDocument.create(User.USERESOURCE_TABLENAME+uid,ur);
					JsonDocument pd = JsonDocument.create(Payment.PAYMENT_TABLENAME+appTrans,po);
					JsonDocument upd = JsonDocument.create(UserPending.USERPEDING_TB+appTrans,upo);
					Lib.getDBGame(false).getCBConnection().upsert(urd);
					Lib.getDBGame(false).getCBConnection().upsert(pd);
					Lib.getDBGame(false).getCBConnection().upsert(upd);
					return buildErrorCode(Wallet.SUCCESS, "Success !");
				}else{
					return buildErrorCode(ErrorCode.USER_NOTEXIST, "User is not exist !");
				}				
			}else{
				return buildErrorCode((byte)0, "Invalid sig");
			}
		}
		return buildErrorCode(ErrorCode.UNKNOWN, "unknown error !");
	}
	
	private CallBackResponse buildErrorCode(byte ec, String msg){
		CallBackResponse rs = new CallBackResponse();
		rs.setResultCode(ec);
		rs.setResultMessage(msg);
		return rs;
	}	
}