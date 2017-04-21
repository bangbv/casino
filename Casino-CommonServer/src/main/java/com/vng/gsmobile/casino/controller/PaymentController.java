package com.vng.gsmobile.casino.controller;

import java.io.File;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vng.gsmobile.casino.entity.HttpCmdResponse;
import com.vng.gsmobile.casino.service.PaymentService;

@RestController
public class PaymentController {

	@RequestMapping(value = "/paymentFake", method = RequestMethod.POST)
	public Object paymentFake(@RequestBody String body) {
		PaymentService ps = new PaymentService();
		boolean rs = ps.save(body);
		HttpCmdResponse httpCmd = new HttpCmdResponse();
		if (rs) {
			httpCmd.setCode(1);
			httpCmd.setMsg("Success !");
		} else {
			httpCmd.setCode(0);
			httpCmd.setMsg("Error !");
		}
		httpCmd.setData(null);
		
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("database.cfg").getFile());
		System.out.println("path:"+file.getAbsolutePath());
		return httpCmd;
	}
}