package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;

import vn.com.vng.gsmobile.casino.entries.AntiSpamManager;

public class testAntiSpam {
	public static void main(String[] agrs) throws InterruptedException{
		AntiSpamManager a = new AntiSpamManager(3, 10, 10);
		for(int i = 1; i < 20;){
			boolean bKq = a.get(123456).valid();
			System.out.println(Arrays.asList(i, bKq));
			if(bKq) i++;
			Thread.sleep(1000);
		}
	}
}
