package vn.com.vng.gsmobile.casino.test;

import vn.com.vng.gsmobile.casino.entries.RoomManager;

public class testGameConfig {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RoomManager.init();
		//System.out.println(RoomManager.getRoomList());
		//System.out.println(RoomManager.getRoomList((byte)1));
		//System.out.println(RoomManager.getRoomList((byte)1,(byte)1));
		System.out.println(RoomManager.getRoomList((byte)3,(byte)1));
	}

}
