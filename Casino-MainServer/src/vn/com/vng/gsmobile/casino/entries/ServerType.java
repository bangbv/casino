package vn.com.vng.gsmobile.casino.entries;

public final class ServerType {
  private ServerType() { }
  public static final byte AllInOne = 0;
  public static final byte Main = 1;
  public static final byte Game = 2;
  public static final byte Bot = 3;
  public static final byte Log = 4;

  public static final String[] names = { "main", "main", "game", "bot", "log",};

  public static String name(int e) { return names[e]; }
}

