package com.vng.gsmobile.casino.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.client.java.document.json.JsonObject;
import com.vng.gsmobile.casino.entity.HttpCmdResponse;
import com.vng.gsmobile.casino.entity.JWT;
import com.vng.gsmobile.casino.service.GetGameCBConfigService;
import com.vng.gsmobile.casino.service.UserInfoService;
import com.vng.gsmobile.casino.util.Const;
import com.vng.gsmobile.casino.util.ErrorCode;
import com.vng.gsmobile.casino.util.LocalCache;

@RestController
public class HttpCmdController {

	@Value("${server.port}")
	private String port;

	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "/httpCmd", method = RequestMethod.POST)
	public Object httpCmdRequest(@RequestHeader String Authorization, HttpSession session,HttpServletRequest request) throws Exception {
		String sessionKey = session.getId();
		HttpCmdResponse httpCmd = new HttpCmdResponse();
		JsonObject user = null;
		if (Authorization == null) {
			return buildErrorCode(httpCmd,ErrorCode.UNKNOWN,Const.DATA_EMPTY);
		} else {
			// parse token
			Authorization = Authorization.replace(JWT.HEADER, "").trim();
			String[] jwtParts = Authorization.split(JWT.REGEX);
//			try {
//				Claims claims = Jwts.parser().setSigningKey(Const.ENCRYPT_KEY.getBytes()).parseClaimsJws(Authorization)
//						.getBody();
//				Long exp = Long.valueOf(claims.get(JWT.EXP).toString());
//				if (exp < (System.currentTimeMillis() / 1000)) {
//					return buildErrorCode(httpCmd,ErrorCode.JWT_INVALID,"JWT expired !");
//				}
//				String jti = (String) claims.get(JWT.JTI);
//				if (LocalCache.getFromCache(jti) == null) {
//					LocalCache.put(jti, "exist");
//				} else {
//					return buildErrorCode(httpCmd,ErrorCode.JWT_INVALID,"JWT jti is exist !");
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				return buildErrorCode(httpCmd,ErrorCode.JWT_INVALID,"JWT error !");
//			}
			String body = new String(Base64Utils.decodeFromString(jwtParts[1]));
			JSONObject bodyJson = new JSONObject(body);
			String jti = bodyJson.getString(JWT.JTI);
			if (LocalCache.getFromCache(jti) == null) {
				LocalCache.put(jti, "exist");
			} else {
				return buildErrorCode(httpCmd,ErrorCode.JWT_INVALID,"JWT jti is exist !");
			}
			JSONObject data = bodyJson.getJSONObject(JWT.DATA);
			user = UserInfoService.getUserInfo(data, sessionKey,request);
			if (user == null) {
				return buildErrorCode(httpCmd,ErrorCode.UNKNOWN,"internal server error !");
			}
			httpCmd.setCode(ErrorCode.OK);
			httpCmd.setMsg(Const.SUCCESS);
			Map gameConfig = GetGameCBConfigService.getGameCBConfig(data, user, port);
			httpCmd.setData(gameConfig);
			return httpCmd;
		}
	}
	
	private HttpCmdResponse buildErrorCode(HttpCmdResponse httpCmd,byte ec, String msg){
		httpCmd.setCode(ec);
		httpCmd.setMsg(msg);
		return httpCmd;
	}
}
