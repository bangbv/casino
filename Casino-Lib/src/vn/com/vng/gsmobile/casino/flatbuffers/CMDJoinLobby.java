// automatically generated by the FlatBuffers compiler, do not modify

package vn.com.vng.gsmobile.casino.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class CMDJoinLobby extends Table {
  public static CMDJoinLobby getRootAsCMDJoinLobby(ByteBuffer _bb) { return getRootAsCMDJoinLobby(_bb, new CMDJoinLobby()); }
  public static CMDJoinLobby getRootAsCMDJoinLobby(ByteBuffer _bb, CMDJoinLobby obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public CMDJoinLobby __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long uid() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateUid(long uid) { int o = __offset(4); if (o != 0) { bb.putLong(o + bb_pos, uid); return true; } else { return false; } }
  public long coin() { int o = __offset(6); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public boolean mutateCoin(long coin) { int o = __offset(6); if (o != 0) { bb.putLong(o + bb_pos, coin); return true; } else { return false; } }
  public int gameType() { int o = __offset(8); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateGameType(int game_type) { int o = __offset(8); if (o != 0) { bb.put(o + bb_pos, (byte)game_type); return true; } else { return false; } }
  public int lobbyType() { int o = __offset(10); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateLobbyType(int lobby_type) { int o = __offset(10); if (o != 0) { bb.put(o + bb_pos, (byte)lobby_type); return true; } else { return false; } }

  public static int createCMDJoinLobby(FlatBufferBuilder builder,
      long uid,
      long coin,
      int game_type,
      int lobby_type) {
    builder.startObject(4);
    CMDJoinLobby.addCoin(builder, coin);
    CMDJoinLobby.addUid(builder, uid);
    CMDJoinLobby.addLobbyType(builder, lobby_type);
    CMDJoinLobby.addGameType(builder, game_type);
    return CMDJoinLobby.endCMDJoinLobby(builder);
  }

  public static void startCMDJoinLobby(FlatBufferBuilder builder) { builder.startObject(4); }
  public static void addUid(FlatBufferBuilder builder, long uid) { builder.addLong(0, uid, 0); }
  public static void addCoin(FlatBufferBuilder builder, long coin) { builder.addLong(1, coin, 0); }
  public static void addGameType(FlatBufferBuilder builder, int gameType) { builder.addByte(2, (byte)gameType, 0); }
  public static void addLobbyType(FlatBufferBuilder builder, int lobbyType) { builder.addByte(3, (byte)lobbyType, 0); }
  public static int endCMDJoinLobby(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

