// automatically generated by the FlatBuffers compiler, do not modify

package vn.com.vng.gsmobile.casino.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class ChatMsgPrivate extends Table {
  public static ChatMsgPrivate getRootAsChatMsgPrivate(ByteBuffer _bb) { return getRootAsChatMsgPrivate(_bb, new ChatMsgPrivate()); }
  public static ChatMsgPrivate getRootAsChatMsgPrivate(ByteBuffer _bb, ChatMsgPrivate obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public ChatMsgPrivate __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public String msg() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer msgAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public long fromUid() { int o = __offset(6); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateFromUid(long from_uid) { int o = __offset(6); if (o != 0) { bb.putLong(o + bb_pos, from_uid); return true; } else { return false; } }
  public String fromName() { int o = __offset(8); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer fromNameAsByteBuffer() { return __vector_as_bytebuffer(8, 1); }
  public long toUid() { int o = __offset(10); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateToUid(long to_uid) { int o = __offset(10); if (o != 0) { bb.putLong(o + bb_pos, to_uid); return true; } else { return false; } }

  public static int createChatMsgPrivate(FlatBufferBuilder builder,
      int msgOffset,
      long from_uid,
      int from_nameOffset,
      long to_uid) {
    builder.startObject(4);
    ChatMsgPrivate.addToUid(builder, to_uid);
    ChatMsgPrivate.addFromUid(builder, from_uid);
    ChatMsgPrivate.addFromName(builder, from_nameOffset);
    ChatMsgPrivate.addMsg(builder, msgOffset);
    return ChatMsgPrivate.endChatMsgPrivate(builder);
  }

  public static void startChatMsgPrivate(FlatBufferBuilder builder) { builder.startObject(4); }
  public static void addMsg(FlatBufferBuilder builder, int msgOffset) { builder.addOffset(0, msgOffset, 0); }
  public static void addFromUid(FlatBufferBuilder builder, long fromUid) { builder.addLong(1, fromUid, 0); }
  public static void addFromName(FlatBufferBuilder builder, int fromNameOffset) { builder.addOffset(2, fromNameOffset, 0); }
  public static void addToUid(FlatBufferBuilder builder, long toUid) { builder.addLong(3, toUid, 0); }
  public static int endChatMsgPrivate(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

