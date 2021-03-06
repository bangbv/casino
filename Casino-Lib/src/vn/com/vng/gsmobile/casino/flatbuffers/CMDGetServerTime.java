// automatically generated by the FlatBuffers compiler, do not modify

package vn.com.vng.gsmobile.casino.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class CMDGetServerTime extends Table {
  public static CMDGetServerTime getRootAsCMDGetServerTime(ByteBuffer _bb) { return getRootAsCMDGetServerTime(_bb, new CMDGetServerTime()); }
  public static CMDGetServerTime getRootAsCMDGetServerTime(ByteBuffer _bb, CMDGetServerTime obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public CMDGetServerTime __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long serverTime() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateServerTime(long server_time) { int o = __offset(4); if (o != 0) { bb.putLong(o + bb_pos, server_time); return true; } else { return false; } }

  public static int createCMDGetServerTime(FlatBufferBuilder builder,
      long server_time) {
    builder.startObject(1);
    CMDGetServerTime.addServerTime(builder, server_time);
    return CMDGetServerTime.endCMDGetServerTime(builder);
  }

  public static void startCMDGetServerTime(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addServerTime(FlatBufferBuilder builder, long serverTime) { builder.addLong(0, serverTime, 0); }
  public static int endCMDGetServerTime(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

