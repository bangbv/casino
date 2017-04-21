package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.flatbuffers.ChatMsgRoom;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class ChatInGameService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		ChatMsgRoom rq = (ChatMsgRoom) params.get(4);
		ChatMsgRoom rs = (ChatMsgRoom) rq.clone();
		Long rId = rq.roomId();
		Room r = RoomManager.getRoom(rId);
		List<Channel> lc = null;
		if (r != null) {
			lc = r.getChannels();
			bKq = ErrorCode.OK;
		} else {
			bKq = ErrorCode.ROOM_NOTMATCH;
		}

		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		return Arrays.asList(bKq, lc, outparams);
	}
}
