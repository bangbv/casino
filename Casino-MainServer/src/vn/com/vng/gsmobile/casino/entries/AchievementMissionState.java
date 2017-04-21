package vn.com.vng.gsmobile.casino.entries;

public class AchievementMissionState {
	  private AchievementMissionState() { }
	  public static final byte State_Received = 0;
	  public static final byte State_None = 1;
	  public static final byte State_NotReceived = 2;

	  public static final String[] names = { "State_Received", "State_None", "State_NotReceived", };

	  public static String name(int e) { return names[e]; }
}
