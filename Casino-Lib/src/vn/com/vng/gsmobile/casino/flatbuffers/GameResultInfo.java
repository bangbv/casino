// automatically generated by the FlatBuffers compiler, do not modify

package vn.com.vng.gsmobile.casino.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class GameResultInfo extends Table {
  public static GameResultInfo getRootAsGameResultInfo(ByteBuffer _bb) { return getRootAsGameResultInfo(_bb, new GameResultInfo()); }
  public static GameResultInfo getRootAsGameResultInfo(ByteBuffer _bb, GameResultInfo obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public GameResultInfo __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long playerId() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutatePlayerId(long player_id) { int o = __offset(4); if (o != 0) { bb.putLong(o + bb_pos, player_id); return true; } else { return false; } }
  public long gold() { int o = __offset(6); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateGold(long gold) { int o = __offset(6); if (o != 0) { bb.putLong(o + bb_pos, gold); return true; } else { return false; } }
  public int rank() { int o = __offset(8); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateRank(int rank) { int o = __offset(8); if (o != 0) { bb.put(o + bb_pos, (byte)rank); return true; } else { return false; } }

  public static int createGameResultInfo(FlatBufferBuilder builder,
      long player_id,
      long gold,
      int rank) {
    builder.startObject(3);
    GameResultInfo.addGold(builder, gold);
    GameResultInfo.addPlayerId(builder, player_id);
    GameResultInfo.addRank(builder, rank);
    return GameResultInfo.endGameResultInfo(builder);
  }

  public static void startGameResultInfo(FlatBufferBuilder builder) { builder.startObject(3); }
  public static void addPlayerId(FlatBufferBuilder builder, long playerId) { builder.addLong(0, playerId, 0); }
  public static void addGold(FlatBufferBuilder builder, long gold) { builder.addLong(1, gold, 0); }
  public static void addRank(FlatBufferBuilder builder, int rank) { builder.addByte(2, (byte)rank, 0); }
  public static int endGameResultInfo(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}
