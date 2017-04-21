// automatically generated by the FlatBuffers compiler, do not modify

package vn.com.vng.gsmobile.casino.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class CMDReceiveGift extends Table {
  public static CMDReceiveGift getRootAsCMDReceiveGift(ByteBuffer _bb) { return getRootAsCMDReceiveGift(_bb, new CMDReceiveGift()); }
  public static CMDReceiveGift getRootAsCMDReceiveGift(ByteBuffer _bb, CMDReceiveGift obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public CMDReceiveGift __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long uid() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateUid(long uid) { int o = __offset(4); if (o != 0) { bb.putLong(o + bb_pos, uid); return true; } else { return false; } }
  public String giftId() { int o = __offset(6); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer giftIdAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }
  public int giftSource() { int o = __offset(8); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateGiftSource(int gift_source) { int o = __offset(8); if (o != 0) { bb.put(o + bb_pos, (byte)gift_source); return true; } else { return false; } }
  public GiftItem gift() { return gift(new GiftItem()); }
  public GiftItem gift(GiftItem obj) { int o = __offset(10); return o != 0 ? obj.__init(__indirect(o + bb_pos), bb) : null; }

  public static int createCMDReceiveGift(FlatBufferBuilder builder,
      long uid,
      int gift_idOffset,
      int gift_source,
      int giftOffset) {
    builder.startObject(4);
    CMDReceiveGift.addUid(builder, uid);
    CMDReceiveGift.addGift(builder, giftOffset);
    CMDReceiveGift.addGiftId(builder, gift_idOffset);
    CMDReceiveGift.addGiftSource(builder, gift_source);
    return CMDReceiveGift.endCMDReceiveGift(builder);
  }

  public static void startCMDReceiveGift(FlatBufferBuilder builder) { builder.startObject(4); }
  public static void addUid(FlatBufferBuilder builder, long uid) { builder.addLong(0, uid, 0); }
  public static void addGiftId(FlatBufferBuilder builder, int giftIdOffset) { builder.addOffset(1, giftIdOffset, 0); }
  public static void addGiftSource(FlatBufferBuilder builder, int giftSource) { builder.addByte(2, (byte)giftSource, 0); }
  public static void addGift(FlatBufferBuilder builder, int giftOffset) { builder.addOffset(3, giftOffset, 0); }
  public static int endCMDReceiveGift(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}
