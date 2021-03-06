// automatically generated by the FlatBuffers compiler, do not modify

package vn.com.vng.gsmobile.casino.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class CMDCreateRoom extends Table {
  public static CMDCreateRoom getRootAsCMDCreateRoom(ByteBuffer _bb) { return getRootAsCMDCreateRoom(_bb, new CMDCreateRoom()); }
  public static CMDCreateRoom getRootAsCMDCreateRoom(ByteBuffer _bb, CMDCreateRoom obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public CMDCreateRoom __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long uid() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateUid(long uid) { int o = __offset(4); if (o != 0) { bb.putLong(o + bb_pos, uid); return true; } else { return false; } }
  public int gameType() { int o = __offset(6); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateGameType(int game_type) { int o = __offset(6); if (o != 0) { bb.put(o + bb_pos, (byte)game_type); return true; } else { return false; } }
  public int lobbyType() { int o = __offset(8); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateLobbyType(int lobby_type) { int o = __offset(8); if (o != 0) { bb.put(o + bb_pos, (byte)lobby_type); return true; } else { return false; } }
  public long betValue() { int o = __offset(10); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0; }
  public boolean mutateBetValue(long bet_value) { int o = __offset(10); if (o != 0) { bb.putInt(o + bb_pos, (int)bet_value); return true; } else { return false; } }
  public String password() { int o = __offset(12); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer passwordAsByteBuffer() { return __vector_as_bytebuffer(12, 1); }
  public String reqToken() { int o = __offset(14); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer reqTokenAsByteBuffer() { return __vector_as_bytebuffer(14, 1); }
  public String description() { int o = __offset(16); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer descriptionAsByteBuffer() { return __vector_as_bytebuffer(16, 1); }
  public int bigBet() { int o = __offset(18); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateBigBet(int big_bet) { int o = __offset(18); if (o != 0) { bb.put(o + bb_pos, (byte)big_bet); return true; } else { return false; } }

  public static int createCMDCreateRoom(FlatBufferBuilder builder,
      long uid,
      int game_type,
      int lobby_type,
      long bet_value,
      int passwordOffset,
      int req_tokenOffset,
      int descriptionOffset,
      int big_bet) {
    builder.startObject(8);
    CMDCreateRoom.addUid(builder, uid);
    CMDCreateRoom.addDescription(builder, descriptionOffset);
    CMDCreateRoom.addReqToken(builder, req_tokenOffset);
    CMDCreateRoom.addPassword(builder, passwordOffset);
    CMDCreateRoom.addBetValue(builder, bet_value);
    CMDCreateRoom.addBigBet(builder, big_bet);
    CMDCreateRoom.addLobbyType(builder, lobby_type);
    CMDCreateRoom.addGameType(builder, game_type);
    return CMDCreateRoom.endCMDCreateRoom(builder);
  }

  public static void startCMDCreateRoom(FlatBufferBuilder builder) { builder.startObject(8); }
  public static void addUid(FlatBufferBuilder builder, long uid) { builder.addLong(0, uid, 0); }
  public static void addGameType(FlatBufferBuilder builder, int gameType) { builder.addByte(1, (byte)gameType, 0); }
  public static void addLobbyType(FlatBufferBuilder builder, int lobbyType) { builder.addByte(2, (byte)lobbyType, 0); }
  public static void addBetValue(FlatBufferBuilder builder, long betValue) { builder.addInt(3, (int)betValue, 0); }
  public static void addPassword(FlatBufferBuilder builder, int passwordOffset) { builder.addOffset(4, passwordOffset, 0); }
  public static void addReqToken(FlatBufferBuilder builder, int reqTokenOffset) { builder.addOffset(5, reqTokenOffset, 0); }
  public static void addDescription(FlatBufferBuilder builder, int descriptionOffset) { builder.addOffset(6, descriptionOffset, 0); }
  public static void addBigBet(FlatBufferBuilder builder, int bigBet) { builder.addByte(7, (byte)bigBet, 0); }
  public static int endCMDCreateRoom(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

