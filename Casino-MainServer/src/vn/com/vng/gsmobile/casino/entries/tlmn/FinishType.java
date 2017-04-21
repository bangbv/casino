
package vn.com.vng.gsmobile.casino.entries.tlmn;

public final class FinishType {
  private FinishType() { }
  public static final byte NONE = 0;
  public static final byte SOLE_WINNER = 1;
  public static final byte RANKING = 2;

  public static final String[] names = { "None", "Sole_Winner", "Ranking", };

  public static String name(int e) { return names[e]; }
}

