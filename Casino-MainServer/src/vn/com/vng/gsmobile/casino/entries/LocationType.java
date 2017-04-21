package vn.com.vng.gsmobile.casino.entries;

public final class LocationType {
  private LocationType() { }
  public static final byte VN = 0;
  public static final byte Foreign = 1;

  public static final String[] names = { "VN", "QD",};

  public static String name(int e) { return names[e]; }
  public static byte value(String s) { 
	  byte bKq = Foreign;
	  switch (s) {
	  case "VN":
		bKq = VN;
		break;
	  }
	  return bKq;
  }
}

