
package vn.com.vng.gsmobile.casino.entries.tlmn;

public final class PenanceType {
  private PenanceType() { }
  public static final byte NONE = 0;
  public static final byte HEO_DEN = 1;
  public static final byte HEO_DO = 2;
  public static final byte BA_DOI_THONG = 3;
  public static final byte BON_DOI_THONG = 4;
  public static final byte HAI_BA_DOI_THONG = 5;
  public static final byte MOT_TU_QUY = 6;
  public static final byte HAI_TU_QUY = 7;
  public static final byte BA_TU_QUY = 8;


  public static final String[] names = { "None", "Heo_Den", "Heo_Do", "Ba_Doi_Thong", "Bon_Doi_Thong", "Hai_Ba_Doi_Thong", "Mot_Tu_Quy", "Hai_Tu_Quy", "Ba_Tu_Quy"};

  public static String name(int e) { return names[e]; }
}

