package com.vng.gsmobile.casino.Casino_CommonServer;

import com.vng.gsmobile.casino.util.Lib;

public class testCBConnector {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Lib.getDBGame(false);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				long lStartTime = System.nanoTime();
				System.out.println(Lib.getDBGame(false).getCBConnection().get("101_1"));
				long lEndTime = System.nanoTime() - lStartTime;
				System.out.println("airline_10748["+lEndTime/1000000d+"]:OK");
			}
		}).start();

		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				long lStartTime = System.nanoTime();
//				System.out.println(Lib.getDBGame(false).getCBConnection().get("airline_10226"));
//				long lEndTime = System.nanoTime() - lStartTime;
//				System.out.println("airline_10226["+lEndTime/1000000d+"]:OK");
//			}
//		}).start();
//		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				long lStartTime = System.nanoTime();
//				System.out.println(Lib.getDBGame(false).getCBConnection().get("airline_10123"));
//				long lEndTime = System.nanoTime() - lStartTime;
//				System.out.println("airline_10123["+lEndTime/1000000d+"]:OK");
//			}
//		}).start();
//		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				long lStartTime = System.nanoTime();
//				System.out.println(Lib.getDBGame(false).getCBConnection().get("airline_10748"));
//				long lEndTime = System.nanoTime() - lStartTime;
//				System.out.println("airline_10748["+lEndTime/1000000d+"]:OK");
//			}
//		}).start();
//		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				long lStartTime = System.nanoTime();
//				System.out.println(Lib.getDBGame(false).getCBConnection().get("airline_10765"));
//				long lEndTime = System.nanoTime() - lStartTime;
//				System.out.println("airline_10765["+lEndTime/1000000d+"]:OK");
//			}
//		}).start();
//		
//		
//		try {
//			Thread.sleep(60000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				long lStartTime = System.nanoTime();
//				System.out.println(Lib.getDBGame(false).getCBConnection().get("airline_10748"));
//				long lEndTime = System.nanoTime() - lStartTime;
//				System.out.println("airline_10748["+lEndTime/1000000d+"]:OK");
//			}
//		}).start();
//
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				long lStartTime = System.nanoTime();
//				System.out.println(Lib.getDBGame(false).getCBConnection().get("airline_10226"));
//				long lEndTime = System.nanoTime() - lStartTime;
//				System.out.println("airline_10226["+lEndTime/1000000d+"]:OK");
//			}
//		}).start();
//		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				long lStartTime = System.nanoTime();
//				System.out.println(Lib.getDBGame(false).getCBConnection().get("airline_10123"));
//				long lEndTime = System.nanoTime() - lStartTime;
//				System.out.println("airline_10123["+lEndTime/1000000d+"]:OK");
//			}
//		}).start();
//		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				long lStartTime = System.nanoTime();
//				System.out.println(Lib.getDBGame(false).getCBConnection().get("airline_10748"));
//				long lEndTime = System.nanoTime() - lStartTime;
//				System.out.println("airline_10748["+lEndTime/1000000d+"]:OK");
//			}
//		}).start();
//		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				long lStartTime = System.nanoTime();
//				System.out.println(Lib.getDBGame(false).getCBConnection().get("airline_10765"));
//				long lEndTime = System.nanoTime() - lStartTime;
//				System.out.println("airline_10765["+lEndTime/1000000d+"]:OK");
//			}
//		}).start();
//		
//		
//		try {
//			Thread.sleep(60000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Lib.getDBGame(false).close();
	}

}
