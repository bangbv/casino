package vn.com.vng.gsmobile.casino.test;

import com.couchbase.client.java.document.json.JsonObject;

import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class test {

	public static void main(String[] args) throws Exception {
		Long uid = 47921002654564352l;
		JsonObject uo = LocalCache.get(User.USER_TABLENAME + uid);
		System.out.println(uo);
		//int name = (int) uo.getObject("bangbv");
//		System.out.println(name);
	}
}
