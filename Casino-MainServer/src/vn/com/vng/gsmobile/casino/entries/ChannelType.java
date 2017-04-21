package vn.com.vng.gsmobile.casino.entries;

public final class ChannelType {
  private ChannelType() { }
  public static final byte None = 0;
  public static final byte Any = 1;
  public static final byte Main = 2;
  public static final byte Game = 3;

  public static final String[] names = { "None", "Any", "Main", "Game",};

  public static String name(int e) { return names[e]; }
}

