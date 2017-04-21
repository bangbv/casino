package com.vng.gsmobile.casino.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public String getCurrentDate() throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		return df.format(c.getTime());
	}
	
	public boolean compare(String currentDate,String expireDate) throws Exception{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date cd = df.parse(currentDate);
		Date ed = df.parse(expireDate);
		if(cd.compareTo(ed) < 0){
			return true;
		}
		return false;
	}	
}
