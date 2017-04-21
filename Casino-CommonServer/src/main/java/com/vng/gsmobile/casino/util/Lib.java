package com.vng.gsmobile.casino.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.springframework.core.io.ClassPathResource;

import com.couchbase.client.java.document.JsonDocument;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.vng.gsmobile.casino.connector.CBConnector;

public class Lib {
	static Properties propsError = null;
	public synchronized static Properties getErrorConfig(boolean isReload) {
		if (propsError == null || isReload) {
			new File(Const.CONFIG_PATH).mkdir();
			String sFN = Const.CONFIG_PATH+"error.cfg";
			FileInputStream f;
			try {
				propsError = new Properties();
				f = new FileInputStream(sFN);
				Reader reader = new InputStreamReader(f, "UTF-8");
				propsError.load(reader);
				f.close();
			} catch (Exception e) {
				e.printStackTrace();
				Lib.getLogger().error(Lib.class.getName()+".getErrorConfig["+sFN+"]:"+Lib.getStackTrace(e));
				propsError = null;
			}
		}
		return propsError;
	}
	public static String getErrorMessage(int iErcd) {
		String sMsq = null;
		if(propsError!=null)
			sMsq = propsError.getProperty(""+iErcd);
		return sMsq==null?"Lỗi chưa định nghĩa ("+iErcd+")":sMsq;
	}
	@SuppressWarnings("rawtypes")
	static Map propsMainCfg = null;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized static boolean loadConfig(boolean isReload) {
		String sFN = "main.cfg";
		if(propsMainCfg==null || isReload)
		try {
    		Const.IS_STOPPING = true;
			System.out.println("load main config...");
			Gson gson = new Gson();
		    propsMainCfg = (Map) gson.fromJson(new BufferedReader(new FileReader(sFN)), Object.class);
			Const.CONFIG_PATH = propsMainCfg.get("CONFIG_PATH").toString();
			Const.LOG_PATH = propsMainCfg.get("LOG_PATH").toString();
			Const.SERVICE_PATH = propsMainCfg.get("SERVICE_PATH").toString();
			Const.SERVER_PORT = new Double(propsMainCfg.get("SERVER_PORT").toString()).intValue();
			Const.CONTROL_PORT = new Double(propsMainCfg.get("CONTROL_PORT").toString()).intValue();
			Const.READ_TIMEOUT = new Double(propsMainCfg.get("READ_TIMEOUT").toString()).intValue();
			Const.IS_BUSINESS_SLOWLY = "true".equalsIgnoreCase(propsMainCfg.get("IS_BUSINESS_SLOWLY").toString())?true:false;
			Const.MAX_THREADS_EXECUTE_MESSAGE = new Double(propsMainCfg.get("MAX_THREADS_EXECUTE_MESSAGE").toString()).intValue();
			Const.USERPUSH_SCHEDULE = new Double(propsMainCfg.get("USERPUSH_SCHEDULE").toString()).longValue();
			Const.RSCPUSH_SCHEDULE = new Double(propsMainCfg.get("RSCPUSH_SCHEDULE").toString()).longValue();
			Const.GC_SCHEDULE = new Double(propsMainCfg.get("GC_SCHEDULE").toString()).longValue();
			Const.NOTICE_SCHEDULE = new Double(propsMainCfg.get("NOTICE_SCHEDULE").toString()).longValue();
			Const.MONITOR_SCHEDULE = new Double(propsMainCfg.get("MONITOR_SCHEDULE").toString()).longValue();
			Const.LIMIT_PACKGAGE_SIZE = new Double(propsMainCfg.get("LIMIT_PACKGAGE_SIZE").toString()).intValue();
			Const.EMAIL_HOST = propsMainCfg.get("EMAIL_HOST").toString();
			Const.EMAIL_PORT = propsMainCfg.get("EMAIL_PORT").toString();
			Const.EMAIL_ACCOUNT = propsMainCfg.get("EMAIL_ACCOUNT").toString();
			Const.EMAIL_PASSWORD = propsMainCfg.get("EMAIL_PASSWORD").toString();
			Const.EMAIL_ALERT = propsMainCfg.get("EMAIL_ALERT").toString();
			Const.SERVER_HOST = propsMainCfg.get("SERVER_HOST").toString();
			Const.SERVER_TYPE = propsMainCfg.get("SERVER_TYPE").toString();
			Const.SESSION_KEY = propsMainCfg.get("SESSION_KEY").toString();
			System.out.println("load main config: OK");
			Lib.getLogger().info(Lib.class.getName()+".loadMainConfig["+sFN+"]: OK");
			if(getServiceConfig(isReload)==null) return false;
			if(getDBConfig(isReload)==null) return false;
			Lib.getDBGame(isReload);
			Lib.getDBLog(isReload);
			if(getRedisConfig(isReload)==null) return false;
			//Lib.getRedisGame(isReload);
			if(getErrorConfig(isReload)==null) return false;
			if(getServerConfig(isReload)==null) return false;
			Map<String, Object> m = (Map<String, Object>) Lib.getServerConfig(false).get(Const.SERVER_HOST);
			Const.MAX_BATTLE = new Double(m.get("MAX").toString()).intValue();
			Const.IS_STOPPING = false;
		} catch (Exception e) {
			e.printStackTrace();
			propsMainCfg = null;
			Const.IS_STOPPING = false;
			return false;
		}
		return true;
	}
	@SuppressWarnings("rawtypes")
	static Map propsServerCfg = null;
	@SuppressWarnings({ "rawtypes"})
	public synchronized static Map getServerConfig(boolean isReload) {
		new File(Const.CONFIG_PATH).mkdir();
		String sFN = Const.CONFIG_PATH+"server.cfg";
		if(propsServerCfg==null || isReload)
		try {
			Lib.getLogger().info(Lib.class.getName()+".getServerConfig["+sFN+"]:...");
			Gson gson = new Gson();
			propsServerCfg = (Map) gson.fromJson(new BufferedReader(new FileReader(sFN)), Object.class);
			Lib.getLogger().info(Lib.class.getName()+".getServerConfig["+sFN+"]: OK");
		} catch (Exception e) {
			e.printStackTrace();
			Lib.getLogger().error(Lib.class.getName()+".getServerConfig["+sFN+"]:"+Lib.getStackTrace(e));
			propsServerCfg = null;
		}
		return propsServerCfg;
	}
	@SuppressWarnings("rawtypes")
	static Map propsServiceCfg = null;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized static Map getServiceConfig(boolean isReload) {
		new File(Const.CONFIG_PATH).mkdir();
		String sFN = Const.CONFIG_PATH+"service.cfg";
		if(propsServiceCfg==null || isReload)
		try {
			Lib.getLogger().info(Lib.class.getName()+".getServiceConfig["+sFN+"]:...");
			Gson gson = new Gson();
			propsServiceCfg = (Map) gson.fromJson(new BufferedReader(new FileReader(sFN)), Object.class);
			//load service class here
			Class[] parameters = new Class[] { URL.class };
			URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class sysclass = URLClassLoader.class;
			LinkedTreeMap<Object, Object> m = (LinkedTreeMap<Object, Object>) propsServiceCfg;
			for(Entry<Object, Object> e : m.entrySet()){
				Map mService = (Map) e.getValue();
				File f = new File(Const.SERVICE_PATH+mService.get("package").toString());
				URL u = f.toURI().toURL();
				Method method = sysclass.getDeclaredMethod("addURL", parameters);
				method.setAccessible(true);
				method.invoke(sysLoader, new Object[] { u });
			}
			Lib.getLogger().info(Lib.class.getName()+".getServiceConfig["+sFN+"]: OK");
		} catch (Exception e) {
			e.printStackTrace();
			Lib.getLogger().error(Lib.class.getName()+".getServiceConfig["+sFN+"]:"+Lib.getStackTrace(e));
			propsServiceCfg = null;
		}
		return propsServiceCfg;
	}
	@SuppressWarnings("rawtypes")
	static Map propsRedisCfg = null;
	@SuppressWarnings("rawtypes")
	public synchronized static Map getRedisConfig(boolean isReload) {
		new File(Const.CONFIG_PATH).mkdir();
		String sFN = Const.CONFIG_PATH+"redis.cfg";
		if(propsRedisCfg==null || isReload)
		try {
			Lib.getLogger().info(Lib.class.getName()+".getRedisConfig["+sFN+"]:...");
			Gson gson = new Gson();
			propsRedisCfg = (Map) gson.fromJson(new BufferedReader(new FileReader(sFN)), Object.class);
			Lib.getLogger().info(Lib.class.getName()+".getRedisConfig["+sFN+"]: OK");
		} catch (Exception e) {
			e.printStackTrace();
			Lib.getLogger().error(Lib.class.getName()+".getRedisConfig["+sFN+"]:"+Lib.getStackTrace(e));
			propsRedisCfg = null;
		}
		return propsRedisCfg;
	}
	
//	static RedisConnector redisGame = null;
//	@SuppressWarnings("rawtypes")
//	public synchronized static RedisConnector getRedisGame(boolean isReload) {
//		if(redisGame==null || isReload)
//		{
//			if(isReload && redisGame!=null){
//				redisGame.close();
//			}
//			Map mdb = (Map) Lib.getRedisConfig(false).get("REDIS_GAME");
//			String sId = "RDG"+System.currentTimeMillis();
//			try{
//				Lib.getLogger().info(Lib.class.getName()+".getRedisGame["+sId+"]:...");
//				redisGame = new RedisConnector(sId, mdb);
//				Lib.getLogger().info(Lib.class.getName()+".getRedisGame["+sId+"]:OK");
//			}catch(Exception e){
//				Lib.getLogger().error(Lib.class.getName()+".getRedisGame["+sId+"]:"+e.getMessage());
//				if(redisGame!=null) redisGame.close();
//				redisGame = null;
//			}
//		}
//		return redisGame;
//	}
	
	@SuppressWarnings("rawtypes")
	static Map propsDBCfg = null;
	@SuppressWarnings("rawtypes")
	public synchronized static Map getDBConfig(boolean isReload) {
		String sFN = "database.cfg";
		if(propsDBCfg==null || isReload)
		try {
			Lib.getLogger().info(Lib.class.getName()+".getDBConfig["+sFN+"]:...");
			Gson gson = new Gson();
			propsDBCfg = (Map) gson.fromJson(new BufferedReader(new InputStreamReader(new ClassPathResource(sFN).getInputStream())), Object.class);
			Lib.getLogger().info(Lib.class.getName()+".getDBConfig["+sFN+"]: OK");
		} catch (Exception e) {
			e.printStackTrace();
			Lib.getLogger().error(Lib.class.getName()+".getDBConfig["+sFN+"]:"+Lib.getStackTrace(e));
			propsDBCfg = null;
		}
		return propsDBCfg;
	}
	static CBConnector dbGame = null;
	@SuppressWarnings("rawtypes")
	public synchronized static CBConnector getDBGame(boolean isReload) {
		if(dbGame==null || isReload)
		{
			if(isReload && dbGame!=null){
				dbGame.close();
			}
			Map mdb = (Map) Lib.getDBConfig(false).get("DBGAME");
			String sId = "DBG"+System.currentTimeMillis();
			try{
				Lib.getLogger().info(Lib.class.getName()+".getDBGame["+sId+"]:...");
				dbGame = new CBConnector(sId, mdb);
				Lib.getLogger().info(Lib.class.getName()+".getDBGame["+sId+"]:OK");
			}catch(Exception e){
				Lib.getLogger().error(Lib.class.getName()+".getDBGame["+sId+"]:"+e.getMessage());
				if(dbGame!=null) dbGame.close();
				dbGame = null;
			}
		}
		return dbGame;
	}
	static CBConnector dbLog = null;
	@SuppressWarnings("rawtypes")
	public synchronized static CBConnector getDBLog(boolean isReload) {
		if(dbLog==null || isReload)
		{
			if(isReload && dbLog!=null){
				dbLog.close();
			}
			Map mdb = (Map) Lib.getDBConfig(false).get("DBLOG");
			String sId = "DBL"+System.currentTimeMillis();
			try{
				Lib.getLogger().info(Lib.class.getName()+".getDBLog["+sId+"]:...");
				dbLog = new CBConnector(sId, mdb);
				Lib.getLogger().info(Lib.class.getName()+".getDBLog["+sId+"]:OK");
			}catch(Exception e){
				Lib.getLogger().error(Lib.class.getName()+".getDBLog["+sId+"]:"+e.getMessage());
				if(dbLog!=null) dbLog.close();
				dbLog = null;
			}
		}
		return dbLog;
	}
//	static CBConnector2 dbGame2 = null;
//	@SuppressWarnings("rawtypes")
//	public synchronized static CBConnector2 getDBGame2(boolean isReload) {
//		if(dbGame2==null || isReload)
//		{
//			if(isReload && dbGame2!=null){
//				dbGame2.close();
//			}
//			Map mdb = (Map) Lib.getDBConfig(false).get("DBGAME");
//			String sId = "DBG"+System.currentTimeMillis();
//			try{
//				Lib.getLogger().info(Lib.class.getName()+".getDBGame["+sId+"]:...");
//				dbGame2 = new CBConnector2(sId, mdb);
//				Lib.getLogger().info(Lib.class.getName()+".getDBGame["+sId+"]:OK");
//			}catch(Exception e){
//				Lib.getLogger().error(Lib.class.getName()+".getDBGame["+sId+"]:"+e.getMessage());
//				if(dbGame2!=null) dbGame2.close();
//				dbGame2 = null;
//			}
//		}
//		return dbGame2;
//	}
	private static Integer exeId = 0;
	public static synchronized String getExeId(){
		if(exeId==999999999) exeId = 0;
		else exeId++;
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+String.format("%09d",exeId);
	}
	
	private static Integer conId = 0;
	public static synchronized String getConId(){
		if(conId==999999999) conId = 0;
		else conId++;
		return String.format("%09d",conId);
	}
	private static Integer conSubId = 0;
	public static synchronized String getConSubId(){
		if(conSubId==99) conSubId = 0;
		else conSubId++;
		return String.format("%02d",conSubId);
	}
	private static Logger logMain = null;
	public synchronized static Logger getLogger(){
		if(logMain==null)
		{
			try {
				new File(Const.LOG_PATH).mkdir();
				logMain = Logger.getLogger("log"+Const.LOG_NAME);
				PatternLayout layout = new PatternLayout("%-d{dd/MM/yyyy HH:mm:ss:SSS} %5p - %m%n");
				System.out.println("Path:"+Const.LOG_PATH+Const.LOG_NAME);
				DailyRollingFileAppender confile = new DailyRollingFileAppender(layout,	Const.LOG_PATH+Const.LOG_NAME, "-yyyy-MM-dd");
				logMain.setLevel(Level.TRACE);
				logMain.addAppender((Appender) confile);
			} catch (Exception e) {
				e.printStackTrace();
				logMain = null;
			}
		}			
		return logMain;
	}
	private static Logger logService = null;
	public synchronized static Logger getLogService(){
		if(logService==null)
		{
			try {
				new File(Const.LOG_PATH).mkdir();
				logService = Logger.getLogger("log"+Const.LOG_NAME+"-service");
				PatternLayout layout = new PatternLayout("%-d{dd/MM/yyyy HH:mm:ss:SSS} %5p - %m%n");
				DailyRollingFileAppender confile = new DailyRollingFileAppender(layout,	Const.LOG_PATH+Const.LOG_NAME+"-service", "-yyyy-MM-dd");
				logService.setLevel(Level.TRACE);
				logService.addAppender((Appender) confile);
			} catch (Exception e) {
				e.printStackTrace();
				logService = null;
				Lib.getLogger().error(Lib.class.getName()+".getLogService:"+e.getMessage());
			}
		}			
		return logService;
	}
	public synchronized static void setLoggerLevel(String sLogLevel){
		sLogLevel = sLogLevel.toUpperCase();
		switch(sLogLevel){
			case "ALL":
				getLogger().setLevel(Level.ALL);
				getLogService().setLevel(Level.TRACE);
				break;
			case "TRACE":
				getLogger().setLevel(Level.TRACE);
				getLogService().setLevel(Level.TRACE);
				break;				
			case "DEBUG":
				getLogger().setLevel(Level.DEBUG);
				getLogService().setLevel(Level.DEBUG);
				break;	
			case "INFO":
				getLogger().setLevel(Level.INFO);
				getLogService().setLevel(Level.INFO);
				break;
			case "WARN":
				getLogger().setLevel(Level.WARN);
				getLogService().setLevel(Level.INFO);
				break;		
			case "ERROR":
				getLogger().setLevel(Level.ERROR);
				getLogService().setLevel(Level.INFO);
				break;	
			case "FATAL":
				getLogger().setLevel(Level.FATAL);
				getLogService().setLevel(Level.INFO);
				break;				
			case "OFF":
				getLogger().setLevel(Level.OFF);
				getLogService().setLevel(Level.INFO);
				break;
		}
	}
	public static String getStackTrace(Exception ex) {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bs);
		ex.printStackTrace(ps);
		ps.close();
		String s = new String(bs.toByteArray());
		return s;
	}
	public static String getStackTrace(Throwable ex) {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bs);
		ex.printStackTrace(ps);
		ps.close();
		String s = new String(bs.toByteArray());
		return s;
	}
	@SuppressWarnings("unchecked")
	public static <Any> Any invoke(Object oClass, int iLevel, String sMethodName, Class<?>[] params, Object[] args) throws Exception {
		Object r = null;
		try {
			Class<?> c = oClass.getClass();
			for(int i=0; i < iLevel; i++)
				c = c.getSuperclass();
			Method m = c.getDeclaredMethod(sMethodName, params);
			r = m.invoke(oClass, args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Lib.getLogger().error(Lib.class.getName()+".invoke["+oClass.getClass().getName()+"."+sMethodName+"("+Arrays.asList(args).toString()+")]:"+Lib.getStackTrace(e));
			throw e;
		}
		return (Any)r;
	}
	@SuppressWarnings("unchecked")
	public static <Any> Any invoke(Object oClass, String sMethodName, Class<?>[] params, Object[] args) throws Exception {
		Object r = null;
		try {
			Class<?> c = oClass.getClass();
			Method m = c.getDeclaredMethod(sMethodName, params);
			r = m.invoke(oClass, args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Lib.getLogger().error(Lib.class.getName()+".invoke["+oClass.getClass().getName()+"."+sMethodName+"("+Arrays.asList(args).toString()+")]:"+Lib.getStackTrace(e));
			throw e;
		}
		return (Any)r;
	}
	@SuppressWarnings("unchecked")
	public static <Any> Any invoke(Class<?> c, String sMethodName, Class<?>[] params, Object[] args) throws Exception {
		Object r = null;
		try {
			Method m = c.getDeclaredMethod(sMethodName, params);
			r = m.invoke(null, args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Lib.getLogger().error(Lib.class.getName()+".invoke["+c.getName()+"."+sMethodName+"("+Arrays.asList(args).toString()+")]:"+Lib.getStackTrace(e));
			throw e;
		}
		return (Any)r;
	}
	@SuppressWarnings("unchecked")
	public static <Any> Any parseLogToFlatBuffers(List<?> lLog){//class, hexstringdata, position
		Object oData = null;
		try {
		    Class<?> cls = Class.forName(lLog.get(0).toString());
			ByteBuffer bb = ByteBuffer.wrap(Hex.decodeHex(lLog.get(1).toString().toCharArray()));
			if(lLog.size()>2)
				bb.position((Integer) lLog.get(2));
			Method m = cls.getDeclaredMethod("getRootAs"+cls.getSimpleName(), new Class<?>[]{ByteBuffer.class});
			oData = m.invoke(null, new Object[]{bb});
			return (Any)cls.cast(oData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public static Map<String, String> convert(Map<String, Object> input) {
	    Map<String, String> ret = new HashMap<>();
	    for (Map.Entry<String, Object> entry : input.entrySet()) {
	        ret.put(entry.getKey(), String.valueOf(entry.getValue()));
	    }
	    return ret;
	}
	public static Map<String, Double> convertDouble(Map<String, Object> input) {
		Map<String, Double> ret = null;
	    try{
			ret = new HashMap<>();
		    for (Map.Entry<String, Object> entry : input.entrySet()) {
		    	Number n = (Number) entry.getValue();
		    	ret.put(entry.getKey(), n.doubleValue());
		    }
		}
	    catch(Exception e){
	    	ret = null;
	    }
	    return ret;
	}
	public static Long sum(Object... values){
		Long l = 0l;
		for(Object s : values){
			if(s instanceof String){
				if(s==null || "".equals(((String) s).trim())) s = "0";
				l += Long.parseLong((String) s);
			}
			else if(s instanceof Long)
				l += (long)s;
			else if(s instanceof Number)
				l += ((Number) s).longValue();
		}
		if(l<0) l=0l;
		return l;//String.valueOf(l);
	}
	public static String md5(String s){
        String sKQ = null;
        MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			sKQ = new String(Hex.encodeHex(md.digest(s.getBytes())));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sKQ=null;
		}
		return sKQ;
	}
	
	@SuppressWarnings("unchecked")
	public static <Any> Any getCB(String key){
		List<?> l = Lib.getDBGame(false).getCBConnection().get(key);
		if(l.get(1)!=null){
			JsonDocument j =  (JsonDocument) l.get(1);
			if (j != null) {
				return (Any) j.content();
			}
		}
		return null;
	}
}
