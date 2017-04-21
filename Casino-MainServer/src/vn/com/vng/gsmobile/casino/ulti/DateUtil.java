package vn.com.vng.gsmobile.casino.ulti;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public String nextDate(String date) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(df.parse(date));
		c.add(Calendar.DATE, 1);
		String nextDate = df.format(c.getTime());
		return nextDate;
	}

	public String nextNDate(int n) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(df.parse(df.format(c.getTime())));
		c.add(Calendar.DATE, n);
		String nextDate = df.format(c.getTime());
		return nextDate;
	}
	
	public String backNDate(int n, String date) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(df.parse(date));
		c.add(Calendar.DATE, -n);
		String bd = df.format(c.getTime());
		c.clear(Calendar.DATE);
		return bd;
	}
	
	public Long backNDateL(int n, String date) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(df.parse(date));
		c.add(Calendar.DATE, -n);
		Long bd = c.getTimeInMillis();
		c.clear(Calendar.DATE);
		return bd;
	}
	
	public String getCurrentDate(){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		return df.format(c.getTime());
	}

	public boolean isCurrentDate(String date){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();		
		String cd = df.format(c.getTime());
		if (cd.equalsIgnoreCase(date)) {
			return true;
		}
		return false;
	}
	
	public boolean isNextDate(String date) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		String currentDate = df.format(c.getTime());
		c.setTime(df.parse(date));
		c.add(Calendar.DATE, 1);
		String nextDate = df.format(c.getTime());
		if (currentDate.equalsIgnoreCase(nextDate)) {
			return true;
		}
		return false;
	}
	
	public boolean compare(String currentDate,String expireDate) throws Exception{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date cd = df.parse(currentDate);
		Date ed = df.parse(expireDate);
		if(cd.compareTo(ed) > 0){
			return true;
		}
		return false;
	}
	
	public String convertDate(Long date){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = new Date(date);
		String dstr =  df.format(d);
		return dstr;
	}
}
