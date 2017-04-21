
package vn.com.vng.gsmobile.casino.entries;

public final class ResultType {
  private ResultType() { }
  public static final byte NONE = 0;
  public static final byte NHAT = 1;
  public static final byte NHI = 2;
  public static final byte BA = 3;
  public static final byte BET = 4;

  public static final String[] names = { "None", "Nhat", "Nhi", "Ba", "Bet"};

  public static String name(int e) { return names[e]; }
}

