package vn.com.vng.gsmobile.casino.test;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.flatbuffers.LobbyRoom;
import vn.com.vng.gsmobile.casino.flatbuffers.RoomInfo;

public class testFlatBuffer {
	public static void main(String[] agrs){
		LobbyRoom rs = null;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		builder.createString("roomId1");
		int isl1 = RoomInfo.createSitListVector(builder, new byte[]{0,0,0,0,0,0,0,0});
		int iri1 = RoomInfo.createRoomInfo(
					builder, 
					builder.createString("roomId1"), 
					builder.createString("room1"), 
					1000,
					0,
					0,
					0, 
					isl1,
					0,
					GameRoomState.Destroyed,
					0,
					builder.createString("Chơi lớn đi"),
					0
				);
		int isl2 = RoomInfo.createSitListVector(builder, new byte[]{1,0,0,0,0,0,0,0});
		int iri2 = RoomInfo.createRoomInfo(
				builder, 
				builder.createString("roomId2"), 
				builder.createString("room2"), 
				1000,
				0,
				0,
				0, 
				isl2,
				0,
				GameRoomState.Waiting_Player,
				0,
				builder.createString("Chơi lớn đi"),
				0
			);
		int isl3 = RoomInfo.createSitListVector(builder, new byte[]{1,1,0,0,0,0,0,0});
		int iri3 = RoomInfo.createRoomInfo(
				builder, 
				builder.createString("roomId3"), 
				builder.createString("room3"), 
				1000,
				0,
				0,
				0, 
				isl3,
				0,
				GameRoomState.Waiting_Game,
				0,
				builder.createString("Chơi lớn đi"),
				0
			);
		int isl4 = RoomInfo.createSitListVector(builder, new byte[]{0,1,1,1,0,0,0,0});
		int iri4 = RoomInfo.createRoomInfo(
				builder, 
				builder.createString("roomId4"), 
				builder.createString("room4"), 
				2000,
				0,
				0,
				0, 
				isl4,
				0,
				GameRoomState.Waiting_Game,
				0,
				builder.createString("Chơi lớn đi"),
				0
			);
		int irl = LobbyRoom.createRoomListVector(builder, new int[]{iri1, iri2, iri3, iri4});
		int ilr = LobbyRoom.createLobbyRoom(
				builder, 
				1, 
				1,
				irl
			);
		builder.finish(ilr);
		rs = LobbyRoom.getRootAsLobbyRoom(builder.dataBuffer());
		System.out.println(rs);
	}
}
