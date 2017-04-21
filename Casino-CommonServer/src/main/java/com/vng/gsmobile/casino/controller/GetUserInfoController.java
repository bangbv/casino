package com.vng.gsmobile.casino.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.client.java.document.json.JsonObject;
import com.vng.gsmobile.casino.entity.User;
import com.vng.gsmobile.casino.entity.UserDTO;
import com.vng.gsmobile.casino.entity.UserResponse;
import com.vng.gsmobile.casino.util.LocalCache;

@RestController
public class GetUserInfoController {

	@RequestMapping(value = "/getListRoleInfo", method = RequestMethod.POST)
	public Object gameUserInfo(@RequestParam Long uid) {
		JsonObject uo = LocalCache.get(User.USER_TABLENAME + uid);
		JsonObject uro = LocalCache.get(User.USERESOURCE_TABLENAME + uid);
		UserResponse rs = new UserResponse();
		if (uo != null && uro != null) {
			UserDTO u = new UserDTO();
			u.setUid(uid);
			u.setName(uo.getString(User.NAME));
			uo.getInt(User.VIP);
			u.setLevel(uro.getInt(User.LEVEL));

			rs.setReturnCode(1);
			rs.setMessage("Success !");
			List<UserDTO> data = new ArrayList<>();
			data.add(u);
			rs.setData(data);
			return rs;
		}
		rs.setReturnCode(-1);
		rs.setMessage("Error !");
		return rs;
	}

}