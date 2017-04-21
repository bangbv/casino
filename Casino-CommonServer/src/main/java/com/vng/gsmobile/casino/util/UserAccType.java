package com.vng.gsmobile.casino.util;

public class UserAccType {
	  private UserAccType() { }
	  public static final byte None = 0;
	  public static final byte Facebook = 1;
	  public static final byte Zalo = 2;
	  public static final byte Guest = 3;

	  public static final String[] names = { "None", "Facebook", "Zalo", "Guest", };

	  public static String name(int e) { return names[e]; }
}
