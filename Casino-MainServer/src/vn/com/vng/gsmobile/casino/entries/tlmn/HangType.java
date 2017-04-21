
package vn.com.vng.gsmobile.casino.entries.tlmn;

public final class HangType {
  private HangType() { }
  public static final byte NONE = -1;
  public static final byte BA_DOI_THONG = 0;
  public static final byte TU_QUY = 1;
  public static final byte BON_DOI_THONG = 2;

  public static final String[] names = { "None", "Ba_Doi_Thong", "Tu_Quy", "Bon_Doi_Thong", };

  public static String name(int e) { return names[e - NONE]; }
}

