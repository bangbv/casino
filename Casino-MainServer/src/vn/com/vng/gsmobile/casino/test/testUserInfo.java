package vn.com.vng.gsmobile.casino.test;

import java.util.List;

import vn.com.vng.gsmobile.casino.connector.CBConnector.CBConnection;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class testUserInfo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CBConnection cn = Lib.getDBGame(false).getCBConnection();
		List<?> rs = cn.get("1_f10201325989228983");
		System.out.println(rs.get(1));
	}
}
