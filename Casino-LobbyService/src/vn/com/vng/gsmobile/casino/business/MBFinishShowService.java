package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.entries.maubinh.MBBattle;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGameMBFinishShow;
import vn.com.vng.gsmobile.casino.flatbuffers.GameMBInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class MBFinishShowService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGameMBFinishShow rq = (CMDGameMBFinishShow) params.get(4);
		Long rId = rq.roomId();
		Room r = RoomManager.getRoom(rId);
		GameMBInfo rs = null;
		MBBattle b = null;
		if (r != null) {
			b = (MBBattle) r.getBattle();
			if (b != null) {
				synchronized (b) {
					rs = (GameMBInfo) b.getData();
					if(rs != null){
						if(rs.gameId()==rq.gameId() && rs.state() == GameRoomState.Finished){
							b.setFinishShow(rq.playerIdx());
							bKq = ErrorCode.OK;
						}
						else
							bKq = ErrorCode.PLAYING_ROOM;
					}
					else
						bKq = ErrorCode.BATTLE_INFO_NOTFOUND;
				}
			} else
				bKq = ErrorCode.BATTLE_NOTFOUND;
		} else
			bKq = ErrorCode.ROOM_NOTMATCH;
		//Kiểm tra kết thúc show bài
		if(bKq == ErrorCode.OK && b.isFinishShowAll()){
			synchronized (b) {
				b.notifyAll();
			}
		}
		return Arrays.asList(bKq, null, null);
	}
}
