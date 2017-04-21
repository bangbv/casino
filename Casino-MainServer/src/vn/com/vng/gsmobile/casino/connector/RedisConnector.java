package vn.com.vng.gsmobile.casino.connector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import redis.clients.jedis.Jedis;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class RedisConnector extends TimerTask{
	private String sId = "";
	private Timer tPing = null;
	private int iMaxConnections = 20;
	private ArrayList<RedisConnection>  aConnections = null;
	@SuppressWarnings("rawtypes")
	Map mConfig = null;
	@SuppressWarnings("rawtypes")
	public RedisConnector(String sId, Map mdb){
		try{
			aConnections = new ArrayList<RedisConnection>();
			this.sId = sId;
			this.iMaxConnections = new Double(mdb.get("MAX").toString()).intValue();
			this.mConfig = mdb;
			//tao lich ping duy tri ket noi
			tPing = new Timer();
			long lPingTime = new Double(mdb.get("PING_TIME").toString()).longValue();
			tPing.schedule(this, lPingTime, lPingTime);
		}catch(Exception e){
			Lib.getLogger().error(Arrays.asList(this.sId, Lib.getStackTrace(e), this.getClass().getSimpleName()+".RedisConnector"));
			throw e;
		}
	}
	public int sizeMax(){
		return iMaxConnections;
	}

	public int size(){
		return aConnections!=null?aConnections.size():0;
	}
	public int sizeFree(){
		int cnt = 0;
		for(int i = 0; i < aConnections.size(); i++){
			if(aConnections.get(i).isAvailable()){
				cnt++;
			}
		}
		return cnt;
	}
	public RedisConnection getRedisConnection(){
		synchronized(aConnections){
			while(aConnections!=null){
				for(int i = 0; i < aConnections.size(); i++){
					if(aConnections.get(i).isAvailable()){
						aConnections.get(i).setBusy();
						return aConnections.get(i);
					}
				}
				if(aConnections.size() < iMaxConnections){
					RedisConnection rdc = new RedisConnection(sId+"-"+aConnections.size(), this.mConfig);
					rdc.setBusy();
					aConnections.add(rdc);
					return rdc;
				}
				else{
					try {
						Lib.getLogger().warn(Arrays.asList(this.sId, "system is busy!", this.getClass().getSimpleName()+".RedisConnection"));
						aConnections.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	public synchronized boolean setMaxConnections(int iMaxConnections){
		if(aConnections.size() > iMaxConnections){
			int iRemove = aConnections.size() - iMaxConnections;
			for(int i = 0; i++ < iRemove;){
				int iLastEntry = aConnections.size()-1;
				if(aConnections.get(iLastEntry).isAvailable()){
					aConnections.get(iLastEntry).disconnect();
					aConnections.remove(iLastEntry);
				}
				else{
					Lib.getLogger().info(Arrays.asList(this.sId, "max="+this.iMaxConnections, "cc="+aConnections.size(), this.getClass().getSimpleName()+".setMaxConnections"));
					return false;
				}
			}
		}
		else
			this.iMaxConnections=iMaxConnections;
		Lib.getLogger().info(Arrays.asList(this.sId, "max="+this.iMaxConnections, "cc="+aConnections.size(), this.getClass().getSimpleName()+".setMaxConnections"));
		return true;
	}
	public synchronized void close(){
		try{
			Lib.getLogger().debug(Arrays.asList(this.sId, this.getClass().getSimpleName()+".close.request"));
			tPing.cancel();
			tPing.purge();
			for(int i = 0; i < aConnections.size(); i++){
				if(aConnections.get(i).isAvailable()){
					aConnections.get(i).setBusy();
					aConnections.get(i).disconnect();
				}
				else{
					int iWaitSecond = 30;
					while(iWaitSecond-- > 0){
						Thread.sleep(1000);
						if(aConnections.get(i).isAvailable())
							break;
					}
					aConnections.get(i).disconnect();//force close
				}
			}
			aConnections.clear();
			aConnections = null;
			Lib.getLogger().debug(Arrays.asList(this.sId, "OK", this.getClass().getSimpleName()+".close.response"));
		}catch(Exception e){
			Lib.getLogger().error(Arrays.asList(this.sId, Lib.getStackTrace(e), this.getClass().getSimpleName()+".close.catch"));
			tPing = null;
			aConnections = null;
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(int i = 0; i < aConnections.size(); i++){
			RedisConnection rdc = aConnections.get(i);
			try{
				if(rdc!=null && rdc.isAvailable()){
					rdc.setBusy();
					String sKQ = rdc.execute(Const.REDIS_DB_PVP_MAIN, 1, "ping", new Class<?>[]{}, new Object[]{}).toString();
					if(!"PONG".equalsIgnoreCase(sKQ))
						rdc.reconnect();
				}
			}catch(Exception e){
				Lib.getLogger().error(Arrays.asList(this.sId+i, Lib.getStackTrace(e), this.getClass().getSimpleName()+".run"));
				if(rdc!=null){
					rdc.reconnect();
					rdc.setFree();
				}
			}
		}
	}

	//sub class here
	public class RedisConnection{
		private String sId = "";
		private Jedis conn = null;
		private boolean isFree = true;
		private int DB = 0;
		@SuppressWarnings("rawtypes")
		Map mConfig = null;
		@SuppressWarnings("rawtypes")
		public RedisConnection(String sId, Map mConfig){
			this.sId = sId;
			this.mConfig = mConfig;
		}
	    public boolean connect() {
			if(conn==null){
				try {
					Lib.getLogger().trace(Arrays.asList(this.sId, mConfig.toString(), this.getClass().getSimpleName()+".connect.request"));
			        conn = new Jedis(
			        			mConfig.get("HOST").toString(),
			        			new Double(mConfig.get("PORT").toString()).intValue(),
			        			60000
			        		);
			        Lib.getLogger().trace(Arrays.asList(this.sId, "OK", this.getClass().getSimpleName()+".connect.response"));
				}catch (Exception e) {
					Lib.getLogger().error(Arrays.asList(this.sId, mConfig.toString(),Lib.getStackTrace(e), this.getClass().getSimpleName()+".connect.catch"));
					conn = null;
				}
			}
			return conn!=null;
	    }
	    public boolean reconnect() {
	    	disconnect();
			try {
				Lib.getLogger().trace(Arrays.asList(this.sId, mConfig.toString(), this.getClass().getSimpleName()+".connect.request"));
		        conn = new Jedis(
	        			mConfig.get("HOST").toString(),
	        			new Double(mConfig.get("PORT").toString()).intValue()
	        		);
		        Lib.getLogger().trace(Arrays.asList(this.sId, "OK", this.getClass().getSimpleName()+".connect.response"));
			}catch (Exception e) {
				Lib.getLogger().error(Arrays.asList(this.sId, mConfig.toString(),Lib.getStackTrace(e), this.getClass().getSimpleName()+".connect.catch"));
				conn = null;
			}
			return conn!=null;
	    }
	    public boolean disconnect(){
			try {
				if(conn!=null){
					Lib.getLogger().trace(Arrays.asList(this.sId, this.getClass().getSimpleName()+".diconnect.request"));
					conn.quit();
					Lib.getLogger().trace(Arrays.asList(this.sId, "OK", this.getClass().getSimpleName()+".diconnect.response"));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Lib.getLogger().error(Arrays.asList(this.sId, Lib.getStackTrace(e), this.getClass().getSimpleName()+".diconnect.catch"));
			}finally {
				conn = null;		
			}
			return true;
	    }
		public boolean isAvailable(){
			return isFree;
		}
		public void setFree(){
			isFree = true;
			synchronized (aConnections) {
				aConnections.notify();
			}
		}
		public void setBusy(){
			isFree = false;
		}
		public boolean isConnected() {
			// TODO Auto-generated method stub
			return conn != null;
		}
		public Object execute(int DB, int iLevel, String sMethodName, Class<?>[] params, Object[] args){
			Object oKq = null;
			String sTrid = "";
			try {
				if(connect()){
					sTrid =Lib.getConId();
					Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, iLevel, sMethodName, Arrays.toString(params), Arrays.toString(args), this.getClass().getSimpleName()+".execute.request"));
					if(this.DB!=DB){
						conn.select(DB);
						this.DB = DB;
					}
					oKq = Lib.invoke(conn, iLevel, sMethodName, params, args);
					Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, oKq, this.getClass().getSimpleName()+".execute.response"));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				reconnect();
				Lib.getLogger().error(Arrays.asList(this.sId+"-"+sTrid, iLevel, sMethodName, Arrays.toString(params), Arrays.toString(args), Lib.getStackTrace(e), this.getClass().getSimpleName()+".execute.catch"));
			}finally{
				setFree();
			}
			return oKq;
		}
		public Jedis getJedis(){
			return conn;
		}
		public String getId(){
			return sId;
		}
		
		//add a row to list
		public Object zadd(String key, double score, String member){
			return execute(this.DB, 0, "zadd", new Class<?>[]{String.class, Double.TYPE, String.class}, new Object[]{key, score, member});
		}	
		
		//get number of row
		public Object zcard(String key){
			return execute(this.DB, 0, "zcard", new Class<?>[]{String.class}, new Object[]{key});
		}		
		//get list Top from max to min with scores
		public Object zrevrangeWithScores(String key, long start, long end){
			return execute(this.DB, 0, "zrevrangeWithScores", new Class<?>[]{String.class, Long.TYPE, Long.TYPE}, new Object[]{key, start, end});
		}
		
		//get list Top max to min
		public Object zrevrange(String key, long start, long stop){
			return execute(this.DB, 0, "zrevrange", new Class<?>[]{String.class, Long.TYPE, Long.TYPE}, new Object[]{key, start, stop});
		}
		//get score of member
		@SuppressWarnings("unchecked")
		public <Any> Any zscore(String key,String member){
			return (Any) execute(this.DB, 0, "zscore", new Class<?>[]{String.class, String.class}, new Object[]{key,member});
		}
		//get rank of member
		@SuppressWarnings("unchecked")
		public <Any> Any zrevrank(String key,String member){
			return (Any) execute(this.DB, 0, "zrevrank", new Class<?>[]{String.class, String.class}, new Object[]{key,member});
		}		
	}
}
