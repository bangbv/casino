// automatically generated by the FlatBuffers compiler, do not modify

package vn.com.vng.gsmobile.casino.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Game3LaGameInfo extends Table {
  public static Game3LaGameInfo getRootAsGame3LaGameInfo(ByteBuffer _bb) { return getRootAsGame3LaGameInfo(_bb, new Game3LaGameInfo()); }
  public static Game3LaGameInfo getRootAsGame3LaGameInfo(ByteBuffer _bb, Game3LaGameInfo obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Game3LaGameInfo __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long roomId() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateRoomId(long room_id) { int o = __offset(4); if (o != 0) { bb.putLong(o + bb_pos, room_id); return true; } else { return false; } }
  public long gameId() { int o = __offset(6); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateGameId(long game_id) { int o = __offset(6); if (o != 0) { bb.putLong(o + bb_pos, game_id); return true; } else { return false; } }
  public Player3LaCardInfo cardList(int j) { return cardList(new Player3LaCardInfo(), j); }
  public Player3LaCardInfo cardList(Player3LaCardInfo obj, int j) { int o = __offset(8); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int cardListLength() { int o = __offset(8); return o != 0 ? __vector_len(o) : 0; }
  public vn.com.vng.gsmobile.casino.flatbuffers.GameResultInfo result(int j) { return result(new vn.com.vng.gsmobile.casino.flatbuffers.GameResultInfo(), j); }
  public vn.com.vng.gsmobile.casino.flatbuffers.GameResultInfo result(vn.com.vng.gsmobile.casino.flatbuffers.GameResultInfo obj, int j) { int o = __offset(10); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int resultLength() { int o = __offset(10); return o != 0 ? __vector_len(o) : 0; }
  public int state() { int o = __offset(12); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateState(int state) { int o = __offset(12); if (o != 0) { bb.put(o + bb_pos, (byte)state); return true; } else { return false; } }
  public long timeRemaining() { int o = __offset(14); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateTimeRemaining(long time_remaining) { int o = __offset(14); if (o != 0) { bb.putLong(o + bb_pos, time_remaining); return true; } else { return false; } }

  public static int createGame3LaGameInfo(FlatBufferBuilder builder,
      long room_id,
      long game_id,
      int card_listOffset,
      int resultOffset,
      int state,
      long time_remaining) {
    builder.startObject(6);
    Game3LaGameInfo.addTimeRemaining(builder, time_remaining);
    Game3LaGameInfo.addGameId(builder, game_id);
    Game3LaGameInfo.addRoomId(builder, room_id);
    Game3LaGameInfo.addResult(builder, resultOffset);
    Game3LaGameInfo.addCardList(builder, card_listOffset);
    Game3LaGameInfo.addState(builder, state);
    return Game3LaGameInfo.endGame3LaGameInfo(builder);
  }

  public static void startGame3LaGameInfo(FlatBufferBuilder builder) { builder.startObject(6); }
  public static void addRoomId(FlatBufferBuilder builder, long roomId) { builder.addLong(0, roomId, 0); }
  public static void addGameId(FlatBufferBuilder builder, long gameId) { builder.addLong(1, gameId, 0); }
  public static void addCardList(FlatBufferBuilder builder, int cardListOffset) { builder.addOffset(2, cardListOffset, 0); }
  public static int createCardListVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startCardListVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addResult(FlatBufferBuilder builder, int resultOffset) { builder.addOffset(3, resultOffset, 0); }
  public static int createResultVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startResultVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addState(FlatBufferBuilder builder, int state) { builder.addByte(4, (byte)state, 0); }
  public static void addTimeRemaining(FlatBufferBuilder builder, long timeRemaining) { builder.addLong(5, timeRemaining, 0); }
  public static int endGame3LaGameInfo(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

