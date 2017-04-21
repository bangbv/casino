package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import vn.com.vng.gsmobile.casino.entries.Dealer;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDStartGame;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class StartGameService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDStartGame rq = (CMDStartGame) params.get(4);
		Long rId = rq.roomId();
		Room r = RoomManager.getRoom(rId);
		if (r != null) {
			Dealer d = r.getDealer();
			if (d != null) {
				synchronized (d) {
					d.notify();
				}
				bKq = ErrorCode.OK;
			}else
				bKq = ErrorCode.DEALER_NOTFOUND;
		}else
			bKq = ErrorCode.NOTEXISTS;
		return Arrays.asList(bKq, null, null);
	}

}
