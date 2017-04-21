package com.vng.gsmobile.casino.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.vng.gsmobile.casino.entity.AppReview;
import com.vng.gsmobile.casino.entity.Common;
import com.vng.gsmobile.casino.entity.Event;
import com.vng.gsmobile.casino.entity.GameConfig;
import com.vng.gsmobile.casino.entity.JWT;
import com.vng.gsmobile.casino.entity.Level;
import com.vng.gsmobile.casino.entity.Shop;
import com.vng.gsmobile.casino.entity.User;
import com.vng.gsmobile.casino.entity.Vip;
import com.vng.gsmobile.casino.util.Const;
import com.vng.gsmobile.casino.util.DateUtil;
import com.vng.gsmobile.casino.util.LocalCache;
import com.vng.gsmobile.casino.util.NetworkHelper;

public class GetGameCBConfigService {

	private static final String REGEX = "-";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getGameCBConfig(JSONObject data, JsonObject user, String port) throws Exception {
		JsonObject gco = LocalCache.get(GameConfig.GAME_TABLENAME + GameConfig.PREFIX);
		JsonObject so = LocalCache.get(Shop.SHOP_TABLENAME + Shop.SHOP_PREFIX);
		JsonObject vo = LocalCache.get(Vip.VIP_TABLENAME + Vip.PREFIX);
		JsonObject eo = LocalCache.get(Event.EVENT_TABLENAME + Event.PREFIX);
		JsonObject lo = LocalCache.get(Level.LEVEL_TABLENAME + Level.PREFIX);
		JsonObject uco = LocalCache.get(User.USERCOND_TB + user.getLong(User.UID));
		JsonObject tdo = LocalCache.get(Const.GAME_DEFINE);
		JsonObject slo = so.getObject(Shop.SHOP_LIST);
		JsonObject vlo = vo.getObject(Vip.VIP_LIST);
		JsonObject elo = eo.getObject(Event.EVENT_LIST);
		JsonArray la = lo.getArray(Level.LEVEL_EXP);
		Map<String, Object> slm = slo.toMap();
		// shop depend on payment type
		for (Map.Entry<String, Object> entry : slm.entrySet()) {
			String sKey = entry.getKey();
			JsonObject shop = slo.getObject(sKey);
			int costType = shop.getInt(Shop.COST_TYPE);
			if(costType == 12 || costType == 13){
				slo.removeKey(sKey);
			}
		}		
		Map vlm = vlo.toMap();
		Map elm = elo.toMap();
		List<?> ll = la.toList();
		Map gameConfig = gco.toMap();
		Map<String, Object> tdm = tdo.toMap();
		gameConfig.put(Common.SHOP_ITEM, slm);
		gameConfig.put(Common.VIP_LIST, vlm);
		gameConfig.put(Common.EVENT_LIST, elm);
		gameConfig.put(Common.EVENT_LIST, elm);
		gameConfig.put(Common.LEVEL_EXP, ll);

		gameConfig.put(Common.UID, user.getLong(User.UID));
		gameConfig.put(Common.NEW_VERSION, false);
		gameConfig.put(Common.NEW_VERSION_MSG, null);
		gameConfig.put(Common.LOCATION, user.getString(User.LOCATION));
		gameConfig.put(Common.SESSION, user.getString(User.SESSION_KEY));

		Map<String, Object> p = new HashMap<>();
		p.put("id", 1);
		p.put("url", "http://" + NetworkHelper.getLocalIp() + ":" + port);
		p.put("version", 1);

		List<Map<String, Object>> pl = new ArrayList<>();
		pl.add(p);
		gameConfig.put(Common.PACK, p);

		int platform = data.getInt(JWT.PLATFORM);
		String version = data.getString(JWT.VERSION);
		String build_version = data.getString(JWT.BUILD_VERSION);
		JsonObject aro = LocalCache.get(AppReview.APP_REVIEW_TB);
		JsonObject rlo = aro.getObject(AppReview.REVIEW_LIST);
		String dateReview = rlo.getString(platform + REGEX + version + REGEX + build_version);
		if (dateReview != null) {
			if (new DateUtil().compare(new DateUtil().getCurrentDate(), dateReview)) {
				gameConfig.put(Common.REVIEW, true);
			}
		}

		JsonObject lbo = gco.getObject(Common.LOBBY);
		for (Map.Entry<String, Object> entry : tdm.entrySet()) {
			Integer gameId = (int) entry.getValue();
			int lt;
			try{
				lt = uco.getInt(gameId + "_" + "lobby_type");
				lbo.put(gameId.toString(), lt);
			}catch (Exception e) {
				// do something
			}			
		}
		gameConfig.put(Common.LOBBY, lbo.toMap());
		return gameConfig;
	}
}
