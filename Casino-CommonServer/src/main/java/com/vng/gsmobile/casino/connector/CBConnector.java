package com.vng.gsmobile.casino.connector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.Document;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.vng.gsmobile.casino.util.Lib;

public class CBConnector{
	private String sId = "";
	private int iMaxConnections = 20;
	private Cluster cluster = null;
	private ArrayList<CBConnection>  aConnections = null;
	@SuppressWarnings("rawtypes")
	Map mConfig = null;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CBConnector(String sId, Map mdb){
		try{
			aConnections = new ArrayList<CBConnection>();
			this.sId = sId;
			this.iMaxConnections = new Double(mdb.get("MAX_OPEN_BUCKETS").toString()).intValue();
			this.mConfig = mdb;
			CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder()
					.sslEnabled(new Boolean(mdb.get("SSL_ENABLED").toString()))
					.mutationTokensEnabled(new Boolean(mdb.get("MUTATION_TOKENS_ENABLED").toString()))
					.queryTimeout(Long.parseLong(mdb.get("QUERY_TIMEOUT").toString()))
					.connectTimeout(Long.parseLong(mdb.get("CONNECT_TIMEOUT").toString()))
					.socketConnectTimeout(Integer.parseInt(mdb.get("SOCKET_CONNECT_TIMEOUT").toString()))
					.maxRequestLifetime(Long.parseLong(mdb.get("MAX_REQUEST_LIFE_TIME").toString()))
					.computationPoolSize(Integer.parseInt(mdb.get("COMPUTATION_POOL_SIZE").toString()))
					.build();
			cluster = CouchbaseCluster.create(env, (List<String>)mdb.get("NODES"));
		}catch(Exception e){
			Lib.getLogger().error(Arrays.asList(this.sId, Lib.getStackTrace(e), this.getClass().getSimpleName()+".CBConnector"));
			cluster = null;
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
	public synchronized CBConnection getCBConnection(){
		int iWaitSecond = 18000;
		while(iWaitSecond-- > 0 && aConnections!=null){
			for(int i = 0; i < aConnections.size(); i++){
				if(aConnections.get(i).isAvailable()){
					aConnections.get(i).setBusy();
					return aConnections.get(i);
				}
			}
			if(aConnections.size() < iMaxConnections){
				CBConnection rdc = new CBConnection(sId+"-"+aConnections.size(), this.mConfig);
				rdc.setBusy();
				aConnections.add(rdc);
				return rdc;
			}
			else{
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Lib.getLogger().warn(Arrays.asList(this.sId, "system is busy!", this.getClass().getSimpleName()+".getCBConnection"));
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
			cluster.disconnect();
			cluster = null;
			Lib.getLogger().debug(Arrays.asList(this.sId, "OK", this.getClass().getSimpleName()+".close.response"));
		}catch(Exception e){
			Lib.getLogger().error(Arrays.asList(this.sId, Lib.getStackTrace(e), this.getClass().getSimpleName()+".close.catch"));
			cluster = null;
			aConnections = null;
		}
	}

	//sub class here
	public class CBConnection{
		private String sId = "";
		private Bucket conn = null;
		private boolean isFree = true;
		@SuppressWarnings("rawtypes")
		Map mConfig = null;
		@SuppressWarnings("rawtypes")
		public CBConnection(String sId, Map mConfig){
			this.sId = sId;
			this.mConfig = mConfig;
		}
	    public boolean connect() {
			if(conn==null){
				try {
					Lib.getLogger().trace(Arrays.asList(this.sId, mConfig.toString(), this.getClass().getSimpleName()+".connect.request"));
			        conn = cluster.openBucket((String)this.mConfig.get("BUCKET"), (String)this.mConfig.get("PASSWORD"), 10, TimeUnit.SECONDS);
			        Lib.getLogger().trace(Arrays.asList(this.sId, "OK", this.getClass().getSimpleName()+".connect.response"));
				}catch (Exception e) {
					Lib.getLogger().error(Arrays.asList(this.sId, mConfig.toString(),Lib.getStackTrace(e), this.getClass().getSimpleName()+".connect.catch"));
					conn = null;
				}
			}
			else if(conn.isClosed())
				reconnect();
			return conn!=null;
	    }
	    public boolean reconnect() {
	    	disconnect();
			try {
				Lib.getLogger().trace(Arrays.asList(this.sId, mConfig.toString(), this.getClass().getSimpleName()+".connect.request"));
		        conn = cluster.openBucket((String)this.mConfig.get("BUCKET"), (String)this.mConfig.get("PASSWORD"), 10, TimeUnit.SECONDS);
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
					conn.close();
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
		}
		public void setBusy(){
			isFree = false;
		}
		public boolean isConnected() {
			// TODO Auto-generated method stub
			return conn != null;
		}
		public List<?> execute(String sMethodName, Class<?>[] params, Object[] args){
			Boolean bKq = false;
			Object oKq = null;
			String sTrid = "";
			try {
				if(connect()){
					sTrid = Lib.getExeId();
					Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, sMethodName, Arrays.toString(params), Arrays.toString(args), this.getClass().getSimpleName()+".execute.request"));
					Object result = Lib.invoke(conn, sMethodName, params, args);
					Object logresullt = null;
					bKq = true;
					if(result instanceof N1qlQueryResult){
						List<JsonObject> content = new ArrayList<>();
						for (N1qlQueryRow row : (N1qlQueryResult) result) {
							content.add(row.value());
						}
						oKq = content;
						logresullt = content.size()<=10?content:content.size()+" rows";
					}
					else{
						logresullt = oKq = result;
					}
					Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, logresullt, this.getClass().getSimpleName()+".execute.response"));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				reconnect();
				Lib.getLogger().error(Arrays.asList(this.sId+"-"+sTrid, sMethodName, Arrays.toString(params), Arrays.toString(args), Lib.getStackTrace(e), this.getClass().getSimpleName()+".execute.catch"));
			}finally{
				setFree();
			}
			return Arrays.asList(bKq, oKq);
		}
		public Bucket getBucket(){
			return conn;
		}
		public String getId(){
			return sId;
		}
		
		//all command here
		public List<?> exists(String documentId){
			return execute("exists", new Class<?>[]{String.class}, new Object[]{documentId});
		}
		public List<?> get(String documentId){
			return execute("get", new Class<?>[]{String.class}, new Object[]{documentId});
		}

		public Object getObject(String documentId){
			return execute("get", new Class<?>[]{String.class}, new Object[]{documentId});
		}
		
		public List<?> insert(JsonDocument doc){
			return execute("insert", new Class<?>[]{Document.class}, new Object[]{doc});
		}
		public List<?> upsert(JsonDocument doc){
			return execute("upsert", new Class<?>[]{Document.class}, new Object[]{doc});
		}
		public List<?> append(JsonDocument doc){
			return execute("append", new Class<?>[]{Document.class}, new Object[]{doc});
		}
		public List<?> prepend(JsonDocument doc){
			return execute("prepend", new Class<?>[]{Document.class}, new Object[]{doc});
		}
		public List<?> replace(JsonDocument doc){
			return execute("replace", new Class<?>[]{Document.class}, new Object[]{doc});
		}
		public List<?> remove(String documentId){
			return execute("remove", new Class<?>[]{String.class}, new Object[]{documentId});
		}
		public List<?> query(N1qlQuery n1ql){
			return execute("query", new Class<?>[]{N1qlQuery.class}, new Object[]{n1ql});
		}
		public List<?> query(String sqlTemplate, Object... params){
			N1qlQuery n1ql = params!=null?N1qlQuery.parameterized(sqlTemplate, JsonArray.from(params)):N1qlQuery.simple(sqlTemplate);
			return execute("query", new Class<?>[]{N1qlQuery.class}, new Object[]{n1ql});
		}
	}
}
