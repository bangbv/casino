// automatically generated by the FlatBuffers compiler, do not modify

package vn.com.vng.gsmobile.casino.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class ChatMsgLobby extends Table {
  public static ChatMsgLobby getRootAsChatMsgLobby(ByteBuffer _bb) { return getRootAsChatMsgLobby(_bb, new ChatMsgLobby()); }
  public static ChatMsgLobby getRootAsChatMsgLobby(ByteBuffer _bb, ChatMsgLobby obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public ChatMsgLobby __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public String msg() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer msgAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public String fromName() { int o = __offset(6); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer fromNameAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }
  public int gameType() { int o = __offset(8); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateGameType(int game_type) { int o = __offset(8); if (o != 0) { bb.put(o + bb_pos, (byte)game_type); return true; } else { return false; } }

  public static int createChatMsgLobby(FlatBufferBuilder builder,
      int msgOffset,
      int from_nameOffset,
      int game_type) {
    builder.startObject(3);
    ChatMsgLobby.addFromName(builder, from_nameOffset);
    ChatMsgLobby.addMsg(builder, msgOffset);
    ChatMsgLobby.addGameType(builder, game_type);
    return ChatMsgLobby.endChatMsgLobby(builder);
  }

  public static void startChatMsgLobby(FlatBufferBuilder builder) { builder.startObject(3); }
  public static void addMsg(FlatBufferBuilder builder, int msgOffset) { builder.addOffset(0, msgOffset, 0); }
  public static void addFromName(FlatBufferBuilder builder, int fromNameOffset) { builder.addOffset(1, fromNameOffset, 0); }
  public static void addGameType(FlatBufferBuilder builder, int gameType) { builder.addByte(2, (byte)gameType, 0); }
  public static int endChatMsgLobby(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}
