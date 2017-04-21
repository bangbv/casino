package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;

import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.entries.ChannelType;
import vn.com.vng.gsmobile.casino.entries.Handshake;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.entries.tlmn.TLMNBattle;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGameTLMNSkip;
import vn.com.vng.gsmobile.casino.flatbuffers.GameTLMNInfo;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class TLMNDLSkipTurnService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGameTLMNSkip rq = (CMDGameTLMNSkip) params.get(4);
		Long rId = rq.roomId();
		Room r = RoomManager.getRoom(rId);
		GameTLMNInfo rs = null;
		TLMNBattle b = null;
		if (r != null) {
			b = (TLMNBattle) r.getBattle();
			if (b != null) {
				synchronized (b) {
					rs = (GameTLMNInfo) b.getData();
					if(rs != null){
						if(rs.gameId()==rq.gameId() && !b.isFinish()){
							//1. Kiểm tra lượt đánh có hợp lệ không - đúng người, đúng lượt
							boolean turnValid = rq.turnIdx() == b.getTurnIdx();
							boolean playerValid = rq.playerIdx() == b.getPlayIdx();
							boolean userValid = Handshake.verify((Channel)params.get(0), rs.cardList(rq.playerIdx()).playerId(), ChannelType.Game);
							if(turnValid && playerValid && userValid){
								//2. Báo server bỏ qua lượt đánh
								b.notifyAll();
								bKq = ErrorCode.OK;
							}
							else
								bKq = turnValid?ErrorCode.TURN_INVALID:(playerValid?ErrorCode.PLAYER_INVALID:ErrorCode.USER_INVALID);
						}
						else
							bKq = ErrorCode.BATTLE_FINISHED;
					}
					else
						bKq = ErrorCode.BATTLE_INFO_NOTFOUND;
				}
			} else
				bKq = ErrorCode.BATTLE_NOTFOUND;
		} else
			bKq = ErrorCode.ROOM_NOTMATCH;
		return Arrays.asList(bKq, null, null);
	}
}
