package com.vng.gsmobile.casino.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.vng.gsmobile.casino.entity.User;
import com.vng.gsmobile.casino.util.Lib;

@RestController
public class AddStartController {

	@RequestMapping(value = "/addStart", method = RequestMethod.GET)
	public Object addStart(@RequestParam Long uid,@RequestParam int start) {
		JsonObject uro = Lib.getCB(User.USERESOURCE_TABLENAME + uid);
		if (uro != null) {
			uro.put(User.STAR,start + uro.getInt(User.STAR));
			JsonDocument urd = JsonDocument.create(User.USERESOURCE_TABLENAME+uid,uro);
			Lib.getDBGame(false).getCBConnection().upsert(urd);
			return "Success";
		}		
		return "Fail !";
	}

}