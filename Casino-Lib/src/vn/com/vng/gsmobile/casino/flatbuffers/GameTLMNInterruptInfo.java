// automatically generated by the FlatBuffers compiler, do not modify

package vn.com.vng.gsmobile.casino.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class GameTLMNInterruptInfo extends Table {
  public static GameTLMNInterruptInfo getRootAsGameTLMNInterruptInfo(ByteBuffer _bb) { return getRootAsGameTLMNInterruptInfo(_bb, new GameTLMNInterruptInfo()); }
  public static GameTLMNInterruptInfo getRootAsGameTLMNInterruptInfo(ByteBuffer _bb, GameTLMNInterruptInfo obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public GameTLMNInterruptInfo __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public int turnIdx() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateTurnIdx(int turn_idx) { int o = __offset(4); if (o != 0) { bb.put(o + bb_pos, (byte)turn_idx); return true; } else { return false; } }
  public int playerIdx1() { int o = __offset(6); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutatePlayerIdx1(int player_idx_1) { int o = __offset(6); if (o != 0) { bb.put(o + bb_pos, (byte)player_idx_1); return true; } else { return false; } }
  public long coin1() { int o = __offset(8); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateCoin1(long coin_1) { int o = __offset(8); if (o != 0) { bb.putLong(o + bb_pos, coin_1); return true; } else { return false; } }
  public int playerIdx2() { int o = __offset(10); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutatePlayerIdx2(int player_idx_2) { int o = __offset(10); if (o != 0) { bb.put(o + bb_pos, (byte)player_idx_2); return true; } else { return false; } }
  public long coin2() { int o = __offset(12); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateCoin2(long coin_2) { int o = __offset(12); if (o != 0) { bb.putLong(o + bb_pos, coin_2); return true; } else { return false; } }

  public static int createGameTLMNInterruptInfo(FlatBufferBuilder builder,
      int turn_idx,
      int player_idx_1,
      long coin_1,
      int player_idx_2,
      long coin_2) {
    builder.startObject(5);
    GameTLMNInterruptInfo.addCoin2(builder, coin_2);
    GameTLMNInterruptInfo.addCoin1(builder, coin_1);
    GameTLMNInterruptInfo.addPlayerIdx2(builder, player_idx_2);
    GameTLMNInterruptInfo.addPlayerIdx1(builder, player_idx_1);
    GameTLMNInterruptInfo.addTurnIdx(builder, turn_idx);
    return GameTLMNInterruptInfo.endGameTLMNInterruptInfo(builder);
  }

  public static void startGameTLMNInterruptInfo(FlatBufferBuilder builder) { builder.startObject(5); }
  public static void addTurnIdx(FlatBufferBuilder builder, int turnIdx) { builder.addByte(0, (byte)turnIdx, 0); }
  public static void addPlayerIdx1(FlatBufferBuilder builder, int playerIdx1) { builder.addByte(1, (byte)playerIdx1, 0); }
  public static void addCoin1(FlatBufferBuilder builder, long coin1) { builder.addLong(2, coin1, 0); }
  public static void addPlayerIdx2(FlatBufferBuilder builder, int playerIdx2) { builder.addByte(3, (byte)playerIdx2, 0); }
  public static void addCoin2(FlatBufferBuilder builder, long coin2) { builder.addLong(4, coin2, 0); }
  public static int endGameTLMNInterruptInfo(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

