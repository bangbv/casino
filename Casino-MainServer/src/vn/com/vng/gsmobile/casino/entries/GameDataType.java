
package vn.com.vng.gsmobile.casino.entries;

public final class GameDataType {
  private GameDataType() { }
  public static final byte NONE = 0;
  public static final byte HIDE = 1;
  public static final byte CLEAR = 2;
  public static final byte UPDATE = 3;

  public static final String[] names = { "None", "Hide", "Clear", "Update", };

  public static String name(int e) { return names[e]; }
}

