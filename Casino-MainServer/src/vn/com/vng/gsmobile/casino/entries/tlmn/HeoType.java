
package vn.com.vng.gsmobile.casino.entries.tlmn;

public final class HeoType {
  private HeoType() { }
  public static final byte NONE = -1;
  public static final byte HEO1 = 0;
  public static final byte HEO2 = 1;
  public static final byte HEO3 = 2;

  public static final String[] names = { "None", "1Heo", "2Heo", "3Heo", };

  public static String name(int e) { return names[e - NONE]; }
}

