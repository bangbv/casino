// automatically generated by the FlatBuffers compiler, do not modify

package vn.com.vng.gsmobile.casino.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class UserInfoDetail extends Table {
  public static UserInfoDetail getRootAsUserInfoDetail(ByteBuffer _bb) { return getRootAsUserInfoDetail(_bb, new UserInfoDetail()); }
  public static UserInfoDetail getRootAsUserInfoDetail(ByteBuffer _bb, UserInfoDetail obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public UserInfoDetail __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long exp() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateExp(long exp) { int o = __offset(4); if (o != 0) { bb.putLong(o + bb_pos, exp); return true; } else { return false; } }
  public long winCount() { int o = __offset(6); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0; }
  public boolean mutateWinCount(long win_count) { int o = __offset(6); if (o != 0) { bb.putInt(o + bb_pos, (int)win_count); return true; } else { return false; } }
  public long loseCount() { int o = __offset(8); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0; }
  public boolean mutateLoseCount(long lose_count) { int o = __offset(8); if (o != 0) { bb.putInt(o + bb_pos, (int)lose_count); return true; } else { return false; } }
  public long star() { int o = __offset(10); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0; }
  public boolean mutateStar(long star) { int o = __offset(10); if (o != 0) { bb.putInt(o + bb_pos, (int)star); return true; } else { return false; } }
  public long cash() { int o = __offset(12); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0; }
  public boolean mutateCash(long cash) { int o = __offset(12); if (o != 0) { bb.putInt(o + bb_pos, (int)cash); return true; } else { return false; } }
  public int vipExpired() { int o = __offset(14); return o != 0 ? bb.getShort(o + bb_pos) & 0xFFFF : 0; }
  public boolean mutateVipExpired(int vip_expired) { int o = __offset(14); if (o != 0) { bb.putShort(o + bb_pos, (short)vip_expired); return true; } else { return false; } }

  public static int createUserInfoDetail(FlatBufferBuilder builder,
      long exp,
      long win_count,
      long lose_count,
      long star,
      long cash,
      int vip_expired) {
    builder.startObject(6);
    UserInfoDetail.addExp(builder, exp);
    UserInfoDetail.addCash(builder, cash);
    UserInfoDetail.addStar(builder, star);
    UserInfoDetail.addLoseCount(builder, lose_count);
    UserInfoDetail.addWinCount(builder, win_count);
    UserInfoDetail.addVipExpired(builder, vip_expired);
    return UserInfoDetail.endUserInfoDetail(builder);
  }

  public static void startUserInfoDetail(FlatBufferBuilder builder) { builder.startObject(6); }
  public static void addExp(FlatBufferBuilder builder, long exp) { builder.addLong(0, exp, 0); }
  public static void addWinCount(FlatBufferBuilder builder, long winCount) { builder.addInt(1, (int)winCount, 0); }
  public static void addLoseCount(FlatBufferBuilder builder, long loseCount) { builder.addInt(2, (int)loseCount, 0); }
  public static void addStar(FlatBufferBuilder builder, long star) { builder.addInt(3, (int)star, 0); }
  public static void addCash(FlatBufferBuilder builder, long cash) { builder.addInt(4, (int)cash, 0); }
  public static void addVipExpired(FlatBufferBuilder builder, int vipExpired) { builder.addShort(5, (short)vipExpired, 0); }
  public static int endUserInfoDetail(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}
