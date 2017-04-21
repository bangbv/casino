package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.entries.Lobby;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.flatbuffers.ChatMsgLobby;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class ChatInLobbyService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		ChatMsgLobby rq = (ChatMsgLobby) params.get(4);
		ChatMsgLobby rs = (ChatMsgLobby) rq.clone();
		byte gt = (byte) rq.gameType();
		Lobby l = RoomManager.getLobby(gt);
		List<Channel> lc = null;
		if (l != null) {
			lc = l.getChannels();
			bKq = ErrorCode.OK;
		} 
		else 
			bKq = ErrorCode.LOBBY_NOTFOUND;
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		return Arrays.asList(bKq, lc, outparams);
	}
}
