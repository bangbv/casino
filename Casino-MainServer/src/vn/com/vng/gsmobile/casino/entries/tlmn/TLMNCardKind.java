
package vn.com.vng.gsmobile.casino.entries.tlmn;

public final class TLMNCardKind {
  private TLMNCardKind() { }
  public static final byte NONE = -1;
  public static final byte RAC = 0;
  public static final byte BO_2 = 1;
  public static final byte BO_3 = 2;
  public static final byte SANH_3 = 3;
  public static final byte SANH_4 = 4;
  public static final byte SANH_5 = 5;
  public static final byte SANH_6 = 6;
  public static final byte SANH_7 = 7;
  public static final byte SANH_8 = 8;
  public static final byte SANH_9 = 9;
  public static final byte SANH_10 = 10;
  public static final byte SANH_11 = 11;
  public static final byte SANH_12 = 12;
  public static final byte SANH_13 = 13;
  public static final byte NAM_DOI_THONG = 14;
  public static final byte SAU_DOI_THONG = 15;
  public static final byte HANG = 16;
  public static final byte TRANG = 17;

  public static final String[] names = { "None", "Rac", "Bo_2", "Bo_3", "Sanh_3", "Sanh_4", "Sanh_5", "Sanh_6", "Sanh_7", "Sanh_8", "Sanh_9", "Sanh_10", "Sanh_11", "Sanh_12", "Sanh_13", "Nam_Doi_Thong", "Sau_Doi_Thong", "Hang", "Trang", };

  public static String name(int e) { return names[e - NONE]; }
}

