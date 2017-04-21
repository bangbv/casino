package vn.com.vng.gsmobile.casino.test;

import java.util.concurrent.ConcurrentLinkedQueue;

public class testQueue {
	static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
	public static void main(String[] agrs){
		queue.add("1");
		queue.add("2");
		queue.add("3");
		queue.add("4");
		queue.add("5");
		queue.add("6");
		queue.add("7");
		queue.add("8");
		queue.add("9");
		queue.add("10");
		System.out.println(queue);
		queue.add(queue.poll());
		System.out.println(queue);
		queue.add(queue.poll());
		System.out.println(queue);
		queue.add(queue.poll());
		System.out.println(queue);
		System.out.println("3112".substring(5));
	}
}
