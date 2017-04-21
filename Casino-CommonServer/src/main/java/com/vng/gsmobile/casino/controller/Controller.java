package com.vng.gsmobile.casino.controller;

import com.vng.gsmobile.casino.entity.HttpCmdResponse;

public class Controller {
	protected HttpCmdResponse buildErrorCode(byte ec, String msg){
		HttpCmdResponse httpCmd = new HttpCmdResponse();
		httpCmd.setCode(ec);
		httpCmd.setMsg(msg);
		return httpCmd;
	}
}
