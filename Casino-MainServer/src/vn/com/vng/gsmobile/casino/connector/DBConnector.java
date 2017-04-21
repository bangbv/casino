package vn.com.vng.gsmobile.casino.connector;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class DBConnector extends TimerTask{
	private String sId = "";
	private Timer tPing = null;
	private int iMaxConnections = 20;
	private ArrayList<DBConnection>  aConnections = null;
	@SuppressWarnings("rawtypes")
	Map mConfig = null;
	@SuppressWarnings("rawtypes")
	public DBConnector(String sId, Map mdb){
		try{
			aConnections = new ArrayList<DBConnection>();
			this.sId = sId;
			this.iMaxConnections = new Double(mdb.get("MAX").toString()).intValue();
			this.mConfig = mdb;//Arrays.asList(mdb.get("DRIVER"),mdb.get("URL"),mdb.get("USER"),mdb.get("PASS"));
			//tao lich ping duy tri ket noi
			tPing = new Timer();
			long lPingTime = new Double(mdb.get("PING_TIME").toString()).longValue();
			tPing.schedule(this, lPingTime, lPingTime);
		}catch(Exception e){
			Lib.getLogger().error(Arrays.asList(this.sId, Lib.getStackTrace(e), this.getClass().getSimpleName()+".DBConnector"));
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

	public DBConnection getDBConnection(){
		synchronized(aConnections){
			while(aConnections!=null){
				for(int i = 0; i < aConnections.size(); i++){
					if(aConnections.get(i).isAvailable()){
						aConnections.get(i).setBusy();
						return aConnections.get(i);
					}
				}
				if(aConnections.size() < iMaxConnections){
					DBConnection rdc = new DBConnection(sId+"-"+aConnections.size(), this.mConfig);
					rdc.setBusy();
					aConnections.add(rdc);
					return rdc;
				}
				else{
					try {
						Lib.getLogger().warn(Arrays.asList(this.sId, "system is busy!", this.getClass().getSimpleName()+".getDBConnection"));
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
			DBConnection dbc = aConnections.get(i);
			try{
				if(dbc!=null && dbc.isAvailable()){
					dbc.setBusy();
					dbc.query(Arrays.asList(mConfig.get("PING_QUERY").toString()));
				}
			}catch(Exception e){
				Lib.getLogger().error(Arrays.asList(this.sId+i, Lib.getStackTrace(e), this.getClass().getSimpleName()+".run"));
				if(dbc!=null){
					dbc.reconnect();
					dbc.setFree();
				}
			}
		}
	}

	//sub class here
	public class DBConnection{
		private String sId = "";
		private Connection conn = null;
		private boolean isFree = true;
		@SuppressWarnings("rawtypes")
		Map mConfig = null;
		@SuppressWarnings("rawtypes")
		public DBConnection(String sId, Map mConfig){
			this.sId = sId;
			this.mConfig = mConfig;
		}
	    public boolean connect() {
			if(conn==null){
				try {
					Lib.getLogger().trace(Arrays.asList(this.sId, mConfig.toString(), this.getClass().getSimpleName()+".connect.request"));
			        Class.forName(mConfig.get("DRIVER").toString());
			        conn = DriverManager.getConnection(mConfig.get("URL").toString(),mConfig.get("USER").toString(),mConfig.get("PASS").toString());
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
		        Class.forName(mConfig.get("DRIVER").toString());
		        conn = DriverManager.getConnection(mConfig.get("URL").toString(),mConfig.get("USER").toString(),mConfig.get("PASS").toString());
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
			} catch (SQLException e) {
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
		
		public List<String> execute(List<? extends String> lQuery){
			// TODO Auto-generated method stub
			List<String>  data = null;
			Statement cstmt = null;
			String sTrid = "";
			try{
				if(connect()){
					sTrid = Lib.getConId();
					cstmt = conn.createStatement();
					Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, lQuery.toString(), this.getClass().getSimpleName()+".execute.request"));
					for(int i=0; i < lQuery.size(); i++){
						String sKQ = "";
						try{
							String sSQL = lQuery.get(i);
							cstmt.execute(sSQL);
							sKQ = "00|"+cstmt.getUpdateCount();
							Lib.getLogger().debug(Arrays.asList(this.sId+"-"+sTrid+"-"+i, sKQ, this.getClass().getSimpleName()+".execute"));
						}catch(Exception e){
							sKQ = "99|"+Lib.getStackTrace(e);
							cstmt.close();
							Lib.getLogger().error(Arrays.asList(this.sId+"-"+sTrid+"-"+i, sKQ, this.getClass().getSimpleName()+".execute"));
							reconnect();
							cstmt = conn.createStatement();
						}
				    	if(data==null)
				    		data = new ArrayList<String>();
				    	data.add(sKQ);
					}
					Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, data.toString(), this.getClass().getSimpleName()+".execute.response"));
				}
			}catch(Exception e){
				data = null;
				reconnect();
				Lib.getLogger().error(Arrays.asList(this.sId+"-"+sTrid, lQuery.toString(),Lib.getStackTrace(e), this.getClass().getSimpleName()+".execute.catch"));
			}finally {
				if (cstmt != null)
					try {
						cstmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						cstmt = null;
						Lib.getLogger().error(Arrays.asList(this.sId+"-"+sTrid,Lib.getStackTrace(e), this.getClass().getSimpleName()+".execute.finally"));
					}
				setFree();
			}
	        return data;
		}

		public String execute(List<? extends String> lProcedure, List<? extends Object> lParams){
			// TODO Auto-generated method stub
			String sKQ = "";
			String paras = "";
			CallableStatement cstmt = null;
			String sTrid = "";
			String sProcedure = "";
			try{
				if(connect()){
					sTrid = Lib.getConId();
					for(@SuppressWarnings("unused") Object o : lParams){ 
						paras += "?,"; 
					};
					paras += "?";
					String sSchema = lProcedure.size()>1?lProcedure.get(1):null;
					String sPackage = lProcedure.size()>2?lProcedure.get(2):null;
					sProcedure = "{call ";
					if(!(sSchema==null || "".equals(sSchema) || "null".equalsIgnoreCase(sSchema)))
						sProcedure += lProcedure.get(1)+".";
					if(!(sPackage==null || "".equals(sPackage) || "null".equalsIgnoreCase(sPackage)))
						sProcedure += lProcedure.get(2)+".";
					sProcedure += lProcedure.get(0)+"("+paras+")}";
					cstmt = conn.prepareCall(sProcedure);
			        for(int i=1; i <= lParams.size(); i++){
			        	cstmt.setObject(i, lParams.get(i-1));
			        };
			        cstmt.registerOutParameter(lParams.size()+1, java.sql.Types.VARCHAR);
			        Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, sProcedure, lParams.toString(), this.getClass().getSimpleName()+".execute.request"));
					cstmt.execute();
					sKQ = cstmt.getObject(lParams.size()+1).toString();
			        Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, sKQ, this.getClass().getSimpleName()+".execute.response"));
				}
			}catch(Exception e){
				sKQ = "99";
				Lib.getLogger().error(Arrays.asList(this.sId+"-"+sTrid, sProcedure, lParams.toString(), Lib.getStackTrace(e), this.getClass().getSimpleName()+".execute.catch"));
				reconnect();
			}finally {
				if (cstmt != null)
					try {
						cstmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						cstmt = null;
						Lib.getLogger().error(Arrays.asList(this.sId+"-"+sTrid, Lib.getStackTrace(e), this.getClass().getSimpleName()+".execute.finally"));
					}
				setFree();				
			}
	        return sKQ;
		}
		public List<?> query(List<? extends String> lQuery){
			List<List<HashMap<String,Object>>>  data = null;
			Statement cstmt = null;
			String sTrid = "";
			try{
				if(connect()){
					sTrid = Lib.getConId();
					cstmt = conn.createStatement();
					Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, lQuery.toString(), this.getClass().getSimpleName()+".query.request"));
					for(int i=0; i < lQuery.size(); i++){
						String sSQL = lQuery.get(i);
						boolean hadResults = cstmt.execute(sSQL);
					    if (hadResults) {
					    	if(data==null)
					    		data = new ArrayList<List<HashMap<String,Object>>>();
					    	data.add(convertResultSetToList(cstmt.getResultSet()));
					    }
					    Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid+"-"+i, hadResults, this.getClass().getSimpleName()+".query"));
					}
					//Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, (data!=null?data.size():"null"), this.getClass().getSimpleName()+".query.response"));
					Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, data, this.getClass().getSimpleName()+".query.response"));
				}
			}catch(Exception e){
				data = null;
				Lib.getLogger().error(Arrays.asList(this.sId+"-"+sTrid, lQuery.toString(), Lib.getStackTrace(e), this.getClass().getSimpleName()+".query.catch"));
				reconnect();
			}finally {
				if (cstmt != null)
					try {
						cstmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						cstmt = null;
						Lib.getLogger().error(Arrays.asList(this.sId+"-"+sTrid, Lib.getStackTrace(e), this.getClass().getSimpleName()+".query.finally"));
					}
				setFree();				
			}
	        return data;
		}	
		@SuppressWarnings("unused")
		public List<?> query(List<? extends String> lProcedure, List<? extends Object> list){
			List<List<HashMap<String,Object>>>  data = null;
			String paras = "";
			CallableStatement cstmt = null;
			String sTrid = "";
			String sProcedure = "";
			try{
				if(connect()){
					sTrid =Lib.getConId();
					for(Object o : list){ 
						paras += "?,"; 
					};
					if(paras.endsWith(",")) paras = paras.substring(0, paras.length()-1);
					String sSchema = lProcedure.size()>1?lProcedure.get(1):null;
					String sPackage = lProcedure.size()>2?lProcedure.get(2):null;
					sProcedure = "{call ";
					if(!(sSchema==null || "".equals(sSchema) || "null".equalsIgnoreCase(sSchema)))
						sProcedure += lProcedure.get(1)+".";
					if(!(sPackage==null || "".equals(sPackage) || "null".equalsIgnoreCase(sPackage)))
						sProcedure += lProcedure.get(2)+".";
					sProcedure += lProcedure.get(0)+"("+paras+")}";
					cstmt = conn.prepareCall(sProcedure);
			        for(int i=1; i <= list.size(); i++){
			        	cstmt.setObject(i, list.get(i-1));
			        };
			        Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, sProcedure, list.toString(), this.getClass().getSimpleName()+".query.request"));
					boolean hadResults = cstmt.execute();
				    while (hadResults) {
				    	if(data==null)
				    		data = new ArrayList<List<HashMap<String,Object>>>();
				    	data.add(convertResultSetToList(cstmt.getResultSet()));
				        hadResults = cstmt.getMoreResults();
				    }
				    //Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, (data!=null?data.size():"null"), this.getClass().getSimpleName()+".query.response"));
				    Lib.getLogger().trace(Arrays.asList(this.sId+"-"+sTrid, data, this.getClass().getSimpleName()+".query.response"));
				}
			}catch(Exception e){
				data = null;
				Lib.getLogger().error(Arrays.asList(this.sId+"-"+sTrid, sProcedure, list.toString(),Lib.getStackTrace(e), this.getClass().getSimpleName()+".query.catch"));
				reconnect();
			}finally {
				if (cstmt != null)
					try {
						cstmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						cstmt = null;
						Lib.getLogger().error(Arrays.asList(this.sId+"-"+sTrid, Lib.getStackTrace(e), this.getClass().getSimpleName()+".query.finally"));
					}
				setFree();				
			}
	        return data;
		}	
		private List<HashMap<String, Object>> convertResultSetToList(ResultSet rs) throws SQLException {
		    ResultSetMetaData md = rs.getMetaData();
		    int columns = md.getColumnCount();
		    List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();

		    while (rs.next()) {
		        HashMap<String,Object> row = new HashMap<String, Object>(columns);
		        for(int i=1; i<=columns; ++i) {
		            row.put(md.getColumnName(i),rs.getObject(i));
		        }
		        list.add(row);
		    }

		    return list;
		}
	}
}
