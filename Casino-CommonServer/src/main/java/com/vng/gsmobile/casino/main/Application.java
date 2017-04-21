package com.vng.gsmobile.casino.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages="com.vng.gsmobile.casino.controller")
public class Application 
{
    public static void main(String[] args) throws Exception {
    	
        SpringApplication.run(Application.class, args);
    }
       
}
