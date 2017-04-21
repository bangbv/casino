package vn.com.vng.gsmobile.casino.test;

import java.util.HashMap;
import java.util.Map;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class testCBConnection3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(Lib.getDBGame(false).getCBConnection().exists("123456"));
		System.out.println(Lib.getDBGame(false).getCBConnection().remove("123456"));
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "Nguyễn Nhị Long");
		map.put("win",100);
		JsonDocument doc = JsonDocument.create("123456", JsonObject.from(map));
		System.out.println(Lib.getDBGame(false).getCBConnection().insert(doc));
		System.out.println(Lib.getDBGame(false).getCBConnection().get("123456"));
		
		map = new HashMap<String, Object>();
		map.put("name", "Nguyễn Nhị Long2");
		map.put("win",200);
		JsonDocument doc2 = JsonDocument.create("123456", JsonObject.from(map));
		System.out.println(Lib.getDBGame(false).getCBConnection().upsert(doc2));
		System.out.println(Lib.getDBGame(false).getCBConnection().get("123456"));
		
		map = new HashMap<String, Object>();
		map.put("name", "Nguyễn Nhị Long3");
		map.put("win",300);
		JsonDocument doc3 = JsonDocument.create("123456", JsonObject.from(map));
		System.out.println(Lib.getDBGame(false).getCBConnection().replace(doc3));
		System.out.println(Lib.getDBGame(false).getCBConnection().get("123456"));
		
		System.out.println(Lib.getDBGame(false).getCBConnection().query("select * from `travel-sample` limit 100"));
		
		System.out.println(Lib.getDBGame(false).getCBConnection().query("select * from `travel-sample` limit $1", 10));
	}

}
