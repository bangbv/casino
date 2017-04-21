package com.vng.gsmobile.casino.util;

public class Lobby {
	  private Lobby() { }
	  public static final byte None = 0;
	  public static final byte NongDan = 1;
	  public static final byte QuyToc = 2;
	  public static final byte HoangGia = 3;

	  public static final String[] names = { "None", "NongDan", "QuyToc", "HoangGia"};

	  public static final int[] listLobby = {NongDan,QuyToc,HoangGia};
	  
	  public static String name(int l) { return names[l]; }
}
