
package vn.com.vng.gsmobile.casino.entries;

public final class CardColor {
  private CardColor() { }
  public static final byte NONE = -1;
  public static final byte BLACK = 0;
  public static final byte RED = 1;

  public static final String[] names = { "None", "Black", "Red", };

  public static String name(int e) { return names[e - NONE]; }
}

