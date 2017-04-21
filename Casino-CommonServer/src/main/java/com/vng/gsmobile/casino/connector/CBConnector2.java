package com.vng.gsmobile.casino.connector;
//package com.vng.cardgame.http_cardgame.connector;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//import com.couchbase.client.java.Bucket;
//import com.couchbase.client.java.Cluster;
//import com.couchbase.client.java.CouchbaseCluster;
//import com.couchbase.client.java.env.CouchbaseEnvironment;
//import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
//import com.vng.cardgame.http_cardgame.util.Lib;
//
//public class CBConnector2{
//	private String sId = "";
//	private int iMaxConnections = 20;
//	private AtomicInteger iConcurentConnections = new AtomicInteger(0);
//	private Cluster cluster = null;
//	
//	@SuppressWarnings("rawtypes")
//	Map mConfig = null;
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public CBConnector2(String sId, Map mdb){
//		try{
//			this.sId = sId;
//			Lib.getLogger().debug(Arrays.asList(this.sId, this.getClass().getSimpleName()+".open.request"));
//			CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder()
//					.sslEnabled(new Boolean(mdb.get("SSL_ENABLED").toString()))
//					.mutationTokensEnabled(new Boolean(mdb.get("MUTATION_TOKENS_ENABLED").toString()))
//					.queryTimeout(Long.parseLong(mdb.get("QUERY_TIMEOUT").toString()))
//					.connectTimeout(Long.parseLong(mdb.get("CONNECT_TIMEOUT").toString()))
//					.socketConnectTimeout(Integer.parseInt(mdb.get("SOCKET_CONNECT_TIMEOUT").toString()))
//					.maxRequestLifetime(Long.parseLong(mdb.get("MAX_REQUEST_LIFE_TIME").toString()))
//					.computationPoolSize(Integer.parseInt(mdb.get("COMPUTATION_POOL_SIZE").toString()))
//					.build();
//			cluster = CouchbaseCluster.create(env, (List<String>)mdb.get("NODES"));
//			this.iMaxConnections = new Double(mdb.get("MAX_OPEN_BUCKETS").toString()).intValue();
//			this.mConfig = mdb;
//			Lib.getLogger().debug(Arrays.asList(this.sId, "OK", this.getClass().getSimpleName()+".open.response"));
//		}catch(Exception e){
//			Lib.getLogger().error(Arrays.asList(this.sId, Lib.getStackTrace(e), this.getClass().getSimpleName()+".open.catch"));
//			cluster = null;
//			iConcurentConnections.set(0);
//			throw e;
//		}
//	}
//	public int sizeMax(){
//		return iMaxConnections;
//	}
//
//	public int size(){
//		return iConcurentConnections.get();
//	}
//
//	public synchronized boolean setMaxConnections(int iMaxConnections){
//		this.iMaxConnections=iMaxConnections;
//		Lib.getLogger().info(Arrays.asList(this.sId, "max="+this.iMaxConnections, "cc="+this.iConcurentConnections.get(), this.getClass().getSimpleName()+".setMaxConnections"));
//		return true;
//	}
//	public synchronized void close(){
//		try{
//			Lib.getLogger().info(Arrays.asList(this.sId, this.getClass().getSimpleName()+".close.request"));
//			cluster.disconnect();
//			cluster = null;
//			iConcurentConnections.set(0);
//			Lib.getLogger().info(Arrays.asList(this.sId, "OK", this.getClass().getSimpleName()+".close.response"));
//		}catch(Exception e){
//			Lib.getLogger().error(Arrays.asList(this.sId, Lib.getStackTrace(e), this.getClass().getSimpleName()+".close.catch"));
//			cluster = null;
//			iConcurentConnections.set(0);
//		}
//	}
//	private Bucket getBucket(String sTrid){
//		Lib.getLogger().trace(Arrays.asList(sTrid, this.getClass().getSimpleName()+".getBucket.request"));
//		Bucket bucket = cluster.openBucket((String)this.mConfig.get("BUCKET"), (String)this.mConfig.get("PASSWORD"), 10, TimeUnit.SECONDS);
//		Lib.getLogger().trace(Arrays.asList(sTrid, bucket, this.getClass().getSimpleName()+".getBucket.response"));
//		return bucket;
//	}
//	public Object execute(String sMethodName, Class<?>[] params, Object[] args){
//		Object oKq = null;
//		Bucket bucket = null;
//		String sTrid = this.sId+"-"+Lib.getExeId();
//		try{
//			Lib.getLogger().trace(Arrays.asList(sTrid, this.getClass().getSimpleName()+".execute.request"));
//			bucket = getBucket(sTrid);
//			if(bucket!=null){
//				iConcurentConnections.incrementAndGet();
//				oKq = Lib.invoke(bucket, sMethodName, params, args);
//			}
//			Lib.getLogger().trace(Arrays.asList(sTrid, oKq, this.getClass().getSimpleName()+".execute.response"));
//		}catch(Exception e){
//			oKq = null;
//			Lib.getLogger().error(Arrays.asList(sTrid, Lib.getStackTrace(e), this.getClass().getSimpleName()+".execute.catch"));
//		}finally {
//			if(bucket!=null){
//				bucket.close();
//				iConcurentConnections.decrementAndGet();
//			}
//		}
//		return oKq;
//	}
//	public Object get(String documentId){
//		return execute("get", new Class<?>[]{String.class}, new Object[]{documentId});
//	}
//}
