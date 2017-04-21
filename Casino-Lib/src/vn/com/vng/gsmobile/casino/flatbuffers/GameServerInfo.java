// automatically generated by the FlatBuffers compiler, do not modify

package vn.com.vng.gsmobile.casino.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class GameServerInfo extends Table {
  public static GameServerInfo getRootAsGameServerInfo(ByteBuffer _bb) { return getRootAsGameServerInfo(_bb, new GameServerInfo()); }
  public static GameServerInfo getRootAsGameServerInfo(ByteBuffer _bb, GameServerInfo obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public GameServerInfo __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long uid() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateUid(long uid) { int o = __offset(4); if (o != 0) { bb.putLong(o + bb_pos, uid); return true; } else { return false; } }
  public int gameType() { int o = __offset(6); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateGameType(int game_type) { int o = __offset(6); if (o != 0) { bb.put(o + bb_pos, (byte)game_type); return true; } else { return false; } }
  public int lobbyType() { int o = __offset(8); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateLobbyType(int lobby_type) { int o = __offset(8); if (o != 0) { bb.put(o + bb_pos, (byte)lobby_type); return true; } else { return false; } }
  public String server() { int o = __offset(10); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer serverAsByteBuffer() { return __vector_as_bytebuffer(10, 1); }
  public long coin() { int o = __offset(12); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateCoin(long coin) { int o = __offset(12); if (o != 0) { bb.putLong(o + bb_pos, coin); return true; } else { return false; } }

  public static int createGameServerInfo(FlatBufferBuilder builder,
      long uid,
      int game_type,
      int lobby_type,
      int serverOffset,
      long coin) {
    builder.startObject(5);
    GameServerInfo.addCoin(builder, coin);
    GameServerInfo.addUid(builder, uid);
    GameServerInfo.addServer(builder, serverOffset);
    GameServerInfo.addLobbyType(builder, lobby_type);
    GameServerInfo.addGameType(builder, game_type);
    return GameServerInfo.endGameServerInfo(builder);
  }

  public static void startGameServerInfo(FlatBufferBuilder builder) { builder.startObject(5); }
  public static void addUid(FlatBufferBuilder builder, long uid) { builder.addLong(0, uid, 0); }
  public static void addGameType(FlatBufferBuilder builder, int gameType) { builder.addByte(1, (byte)gameType, 0); }
  public static void addLobbyType(FlatBufferBuilder builder, int lobbyType) { builder.addByte(2, (byte)lobbyType, 0); }
  public static void addServer(FlatBufferBuilder builder, int serverOffset) { builder.addOffset(3, serverOffset, 0); }
  public static void addCoin(FlatBufferBuilder builder, long coin) { builder.addLong(4, coin, 0); }
  public static int endGameServerInfo(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

