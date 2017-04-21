package vn.com.vng.gsmobile.casino.test;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class testConcurrentHashMap {
	static ConcurrentHashMap<Long, String> c = new ConcurrentHashMap<>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(Long i=0l; i < 1000l;  i++)
			c.put(i, i.toString());
		MyThread a = new MyThread();
		MyThread b = new MyThread();
		a.start();
		b.start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(c);
	}
	public static void action(){
		Iterator<Entry<Long, String>> it = c.entrySet().iterator();
		while(it.hasNext()){
			Entry<Long, String> e = it.next();
			if(e.getKey()%2==0)
				it.remove();
		}
	}
}
class MyThread extends Thread{
	boolean isFinish = false;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		isFinish = false;
		testConcurrentHashMap.action();
		isFinish = true;
	}
	public boolean isFinish(){
		return isFinish;
	}
}
