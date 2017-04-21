package vn.com.vng.gsmobile.casino.entries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class BuyPending {
	public static final String PENDING_TABLENAME = "11_";
	public static final String PENDINGLIST_TABLENAME = "29_";
	public static final String LIST = "list";
	public static final String UID = "uid";
	public static final String TRID = "trid";
	public static final String ITEMID = "itemid";
	public static final String STATUS = "status";

	private static List<Object> list = new ArrayList<>();

	private long uid;
	private long itemId;
	private String transactionId;
	public BuyPending(long uid, long itemId, String transactionId){
		this.uid = uid;
		this.itemId = itemId;
		this.transactionId = transactionId;
	}
	
	public JsonDocument toJsonDocument(){
		HashMap<String, Object> rs = new HashMap<>();
		rs.put(UID, this.uid);
		rs.put(ITEMID, this.itemId);
		rs.put(TRID, this.transactionId);
		rs.put(STATUS, BuyStatusType.Pending);
		return JsonDocument.create(PENDING_TABLENAME+this.transactionId, 3600, JsonObject.from(rs));
	}
	
	public static synchronized void add(String trid){
		if(!list.contains(trid))
			list.add(trid);
	}
	
	public static synchronized void stateSchedule(){
		Iterator<Object> it = list.iterator();
		while(it.hasNext()){
			String trid = it.next().toString();
			JsonObject jo = Lib.getCB(PENDING_TABLENAME+trid);
			if(jo!=null){
				Long uid = jo.getLong(UID);
				Long itemId = jo.getLong(ITEMID);
				String payId = jo.getString(TRID);
				Number status = jo.getInt(STATUS);
				if(uid!=null && itemId!=null && payId!=null && status.byteValue()==BuyStatusType.Receipt){
					byte bKq = Shop.buy(uid, itemId, payId, Const.SERVER_HOST+"_"+System.currentTimeMillis(), null);
					if(bKq == ErrorCode.OK)
						it.remove();
				}
			}
		}
	}
	
	public static void init(){
		//đọc từ DB lên khi start server
		try{
			JsonObject jo = Lib.getCB(PENDINGLIST_TABLENAME+Const.SERVER_HOST);
			list = jo.getArray(LIST).toList();
		}catch(Exception e){}
		if(list==null)
			list = new ArrayList<>();
	}
	
	public static void release(){
		//ghi xuống DB khi shutdown server
		HashMap<String, Object> rs = new HashMap<>();
		rs.put(LIST, list);
		JsonDocument doc = JsonDocument.create(PENDINGLIST_TABLENAME+Const.SERVER_HOST, JsonObject.from(rs));
		Lib.getDBGame(false).getCBConnection().upsert(doc);
	}

}
