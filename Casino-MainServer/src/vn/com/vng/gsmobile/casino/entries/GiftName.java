package vn.com.vng.gsmobile.casino.entries;

public final class GiftName {
	  private GiftName() { }
	  public static final byte DAILYGIFT = 0;
	  public static final byte GIFTSYSTEM = 1;
	  public static final byte GIFTVIP = 2;
	  
	  public static final String[] names = { "daily_gift", "giftSystem", "vipDay" };

	  public static String name(int e) { return names[e]; }
}
