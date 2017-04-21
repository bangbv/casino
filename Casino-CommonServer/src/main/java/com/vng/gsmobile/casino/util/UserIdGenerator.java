package com.vng.gsmobile.casino.util;

public class UserIdGenerator {
	
	public static Long genUserId(){
		StringBuilder userId = new StringBuilder();
        long current = System.currentTimeMillis();
        userId.append("g");
        userId.append(current);
		return (long) 0;
	}
}
