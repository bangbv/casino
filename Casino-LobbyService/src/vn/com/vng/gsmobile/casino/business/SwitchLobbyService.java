package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDJoinLobby;
import vn.com.vng.gsmobile.casino.flatbuffers.LobbyRoom;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class SwitchLobbyService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDJoinLobby rq = (CMDJoinLobby) params.get(4);
		LobbyRoom rs = null;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		List<Integer> l = new ArrayList<>();
		for(Room r : RoomManager.getRoomList((byte)rq.gameType(), (byte)rq.lobbyType())){
			l.add(r.toRoomInfo(builder));
		}
		int ilr = LobbyRoom.createLobbyRoom(builder, 
				rq.lobbyType(), 
				rq.gameType(), 
				LobbyRoom.createRoomListVector(builder, ArrayUtils.toPrimitive(l.toArray(new Integer[l.size()])))
			);
		builder.finish(ilr);
		rs = LobbyRoom.getRootAsLobbyRoom(builder.dataBuffer());
		bKq = ErrorCode.OK;
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,rs); //noi dung phan hoi
		return Arrays.asList(bKq, channels, outparams);
	}

}

