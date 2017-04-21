
package vn.com.vng.gsmobile.casino.entries.maubinh;

public final class HandType {
  private HandType() { }
  public static final byte NONE = 0;
  public static final byte CHI1 = 1;
  public static final byte CHI2 = 2;
  public static final byte CHI3 = 3;
  public static final byte TOTAL = 4;

  public static final String[] names = { "None", "Chi1", "Chi2", "Chi3", "Total"};

  public static String name(int e) { return names[e]; }
}

