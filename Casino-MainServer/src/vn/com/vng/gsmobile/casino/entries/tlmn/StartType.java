
package vn.com.vng.gsmobile.casino.entries.tlmn;

public final class StartType {
  private StartType() { }
  public static final byte NONE = 0;
  public static final byte MIN_CARD = 1;
  public static final byte WINNER = 2;
  public static final byte PENANCE = 3;
  public static final byte NEXT_TURN = 4;

  public static final String[] names = { "None", "Min_Card", "Winner", "Penance", "Next_Turn", };

  public static String name(int e) { return names[e]; }
}

