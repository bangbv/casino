package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;

import vn.com.vng.gsmobile.casino.entries.ChannelType;
import vn.com.vng.gsmobile.casino.entries.Handshake;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDQuitLobby;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class QuitLobbyService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDQuitLobby rq = (CMDQuitLobby) params.get(4);
		RoomManager.leaveLobby(rq.uid(), (byte)rq.gameType());
		Handshake.closeChannel(rq.uid(), ChannelType.Game);
		bKq = ErrorCode.OK;
		return Arrays.asList(bKq, null, null);
	}

}

