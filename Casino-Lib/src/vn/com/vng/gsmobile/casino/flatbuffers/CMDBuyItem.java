// automatically generated by the FlatBuffers compiler, do not modify

package vn.com.vng.gsmobile.casino.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class CMDBuyItem extends Table {
  public static CMDBuyItem getRootAsCMDBuyItem(ByteBuffer _bb) { return getRootAsCMDBuyItem(_bb, new CMDBuyItem()); }
  public static CMDBuyItem getRootAsCMDBuyItem(ByteBuffer _bb, CMDBuyItem obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public CMDBuyItem __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long uid() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateUid(long uid) { int o = __offset(4); if (o != 0) { bb.putLong(o + bb_pos, uid); return true; } else { return false; } }
  public long itemId() { int o = __offset(6); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0; }
  public boolean mutateItemId(long item_id) { int o = __offset(6); if (o != 0) { bb.putInt(o + bb_pos, (int)item_id); return true; } else { return false; } }
  public String transactionId() { int o = __offset(8); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer transactionIdAsByteBuffer() { return __vector_as_bytebuffer(8, 1); }
  public int os() { int o = __offset(10); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateOs(int os) { int o = __offset(10); if (o != 0) { bb.put(o + bb_pos, (byte)os); return true; } else { return false; } }

  public static int createCMDBuyItem(FlatBufferBuilder builder,
      long uid,
      long item_id,
      int transaction_idOffset,
      int os) {
    builder.startObject(4);
    CMDBuyItem.addUid(builder, uid);
    CMDBuyItem.addTransactionId(builder, transaction_idOffset);
    CMDBuyItem.addItemId(builder, item_id);
    CMDBuyItem.addOs(builder, os);
    return CMDBuyItem.endCMDBuyItem(builder);
  }

  public static void startCMDBuyItem(FlatBufferBuilder builder) { builder.startObject(4); }
  public static void addUid(FlatBufferBuilder builder, long uid) { builder.addLong(0, uid, 0); }
  public static void addItemId(FlatBufferBuilder builder, long itemId) { builder.addInt(1, (int)itemId, 0); }
  public static void addTransactionId(FlatBufferBuilder builder, int transactionIdOffset) { builder.addOffset(2, transactionIdOffset, 0); }
  public static void addOs(FlatBufferBuilder builder, int os) { builder.addByte(3, (byte)os, 0); }
  public static int endCMDBuyItem(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}
