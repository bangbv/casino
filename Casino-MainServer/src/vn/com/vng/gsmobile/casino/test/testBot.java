package vn.com.vng.gsmobile.casino.test;

import vn.com.vng.gsmobile.casino.entries.Bot;
import vn.com.vng.gsmobile.casino.flatbuffers.GameType;

public class testBot {
	public static void main(String[] agrs){
		Bot b = new Bot(73319113887399936l);
		b.connect("ws://120.138.76.130:8080/websocket", GameType.TLMN);
		Bot.list.put(b.getId(), b);
		b.close();
	}
}
