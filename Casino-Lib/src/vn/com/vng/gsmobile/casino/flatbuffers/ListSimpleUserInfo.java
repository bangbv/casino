// automatically generated by the FlatBuffers compiler, do not modify

package vn.com.vng.gsmobile.casino.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class ListSimpleUserInfo extends Table {
  public static ListSimpleUserInfo getRootAsListSimpleUserInfo(ByteBuffer _bb) { return getRootAsListSimpleUserInfo(_bb, new ListSimpleUserInfo()); }
  public static ListSimpleUserInfo getRootAsListSimpleUserInfo(ByteBuffer _bb, ListSimpleUserInfo obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public ListSimpleUserInfo __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public String trans() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer transAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public long offset() { int o = __offset(6); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0; }
  public boolean mutateOffset(long offset) { int o = __offset(6); if (o != 0) { bb.putInt(o + bb_pos, (int)offset); return true; } else { return false; } }
  public SimpleProfile list(int j) { return list(new SimpleProfile(), j); }
  public SimpleProfile list(SimpleProfile obj, int j) { int o = __offset(8); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int listLength() { int o = __offset(8); return o != 0 ? __vector_len(o) : 0; }

  public static int createListSimpleUserInfo(FlatBufferBuilder builder,
      int transOffset,
      long offset,
      int listOffset) {
    builder.startObject(3);
    ListSimpleUserInfo.addList(builder, listOffset);
    ListSimpleUserInfo.addOffset(builder, offset);
    ListSimpleUserInfo.addTrans(builder, transOffset);
    return ListSimpleUserInfo.endListSimpleUserInfo(builder);
  }

  public static void startListSimpleUserInfo(FlatBufferBuilder builder) { builder.startObject(3); }
  public static void addTrans(FlatBufferBuilder builder, int transOffset) { builder.addOffset(0, transOffset, 0); }
  public static void addOffset(FlatBufferBuilder builder, long offset) { builder.addInt(1, (int)offset, 0); }
  public static void addList(FlatBufferBuilder builder, int listOffset) { builder.addOffset(2, listOffset, 0); }
  public static int createListVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startListVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endListSimpleUserInfo(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

