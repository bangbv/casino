package com.vng.gsmobile.casino.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vng.gsmobile.casino.entity.AppTranResponse;
import com.vng.gsmobile.casino.entity.Transaction;

@RestController
public class GenTranController {

	private static final String REGEX = "_"; 
	@RequestMapping(value = "/getAppTransID", method = RequestMethod.POST)
	public Object genTranId(@RequestParam String uid) {
		StringBuilder tranBuilder = new StringBuilder();
		tranBuilder.append(uid);
		tranBuilder.append(REGEX);
		tranBuilder.append(System.currentTimeMillis());
		
		AppTranResponse rs = new AppTranResponse();
		Transaction tran = new Transaction();
		tran.setAppTransID(tranBuilder.toString());
		rs.setReturnCode(1);
		rs.setMessage("Success");
		rs.setData(tran);
		return rs;
	}
}