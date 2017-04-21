package com.vng.gsmobile.casino.service;

import java.security.MessageDigest;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.vng.gsmobile.casino.entity.JWT;
import com.vng.gsmobile.casino.entity.User;
import com.vng.gsmobile.casino.util.Const;
import com.vng.gsmobile.casino.util.Lib;
import com.vng.gsmobile.casino.util.LocalCache;
import com.vng.gsmobile.casino.util.NetworkHelper;

public class UserInfoService {

	/**
	 * get UserInfo
	 * 
	 * @param token
	 * @return User
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <Any> Any getUserInfo(JSONObject data, String sessionKey,HttpServletRequest request) throws Exception {
		String deviceId = data.getString(JWT.DEVICE_ID);
		Long uid = data.getLong(User.UID);
		int loginType = data.getInt(JWT.LOGIN_TYPE);
		String sid = null;
		if((loginType ==1) || (loginType ==2)){
			sid = data.getString(User.SID);
		}
		JsonObject uo = LocalCache.get(User.USER_TABLENAME + uid);
		if (uo != null) {
			uo.put(User.SESSION_KEY, genSessionKey(uid));
			String location = uo.getString(User.LOCATION);
			if("HCM".equalsIgnoreCase(location)){
				uo.put(User.LOCATION, NetworkHelper.getLocaltion(request));
			}
			Lib.getDBGame(false).getCBConnection().upsert(JsonDocument.create(User.USER_TABLENAME + uid, uo));
			return (Any) uo;
		} else {
			return (Any) genDefaultUser(loginType, uid, deviceId, sessionKey, sid, request);
		}
	}

	public static Object genDefaultUser(int loginType, Long uid, String deviceId, String sessionKey, String sid, HttpServletRequest request)
			throws Exception {
		String location = NetworkHelper.getLocaltion(request);
		JsonObject uo = LocalCache.get(User.USER_TABLENAME + User.PREFIX);
		uo.put(User.UID, uid);
		uo.put(User.LOCATION, location);
		uo.put(User.ACCTYPE, loginType);
		uo.put(User.NAME, genGuestName(uid));
		uo.put(User.CREATEDATE, System.currentTimeMillis());
		uo.put(User.LASTLOGIN, System.currentTimeMillis());
		uo.put(User.SESSION_KEY, genSessionKey(uid));
		JsonDocument nud = JsonDocument.create(User.USER_TABLENAME + uid.toString(), uo);

		JsonObject uro = LocalCache.get(User.USERESOURCE_TABLENAME + User.PREFIX);
		uro.put(User.UID, uid);
		JsonDocument nurd = JsonDocument.create(User.USERESOURCE_TABLENAME + uid, uro);

		if ((loginType == 1) || (loginType == 2)) {
			uo.put(User.SID, sid);
			JsonObject umo = JsonObject.create();
			umo.put(User.TYPE, User.USERMAPPING_TYPE_VALUE);
			umo.put(User.UID, uid);
			umo.put(User.CREATEDATE, System.currentTimeMillis());
			JsonDocument mapping = JsonDocument.create(User.USERMAPPING_TABLENAME + sid, umo);
			Lib.getDBGame(false).getCBConnection().insert(mapping);
		}

		JsonObject condo = LocalCache.get(User.USERCOND_TB + User.PREFIX);
		JsonDocument ncondo = JsonDocument.create(User.USERCOND_TB + uid, condo);

		Lib.getDBGame(false).getCBConnection().insert(nud);
		Lib.getDBGame(false).getCBConnection().insert(nurd);
		Lib.getDBGame(false).getCBConnection().insert(ncondo);
		return uo;
	}

	private static String genGuestName(Long uid) {
		String str_uid = uid.toString();
		int ul = str_uid.length();
		StringBuilder guestName = new StringBuilder();
		guestName.append("Guest-");
		if(str_uid.length() > 6){
			guestName.append(str_uid.substring(ul - 6));
		}else{
			guestName.append(str_uid);
		}		
		return guestName.toString();
	}
	
	private static String genSessionKey(Long uid) {
		long currentTime = System.currentTimeMillis();
		try {
			MessageDigest digest = MessageDigest.getInstance(Const.ALGORITHM);
			byte[] hash = digest.digest((uid.toString() + currentTime).getBytes(Const.ENCODE));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString().substring(0, 16);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
