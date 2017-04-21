package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.entries.ChannelType;
import vn.com.vng.gsmobile.casino.entries.Handshake;
import vn.com.vng.gsmobile.casino.entries.Lobby;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetLobbyUsers;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class GetLobbyUserService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGetLobbyUsers rq = (CMDGetLobbyUsers) params.get(4);
		CMDGetLobbyUsers rs = null;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		Long uid = rq.uid();
		byte gt = (byte) rq.gameType();
		int lobby_type = rq.lobbyType();
		Long room_id = rq.roomId();
		List<Long> lu = new ArrayList<>();
		Lobby l = RoomManager.getLobby(gt);
		if (l != null) {
			int cnt = 0;
			for (Long pid : l.getPlayers()) {
				if(Handshake.getChannel(pid, ChannelType.Game)!=null){
					lu.add(pid);
					cnt++;
					if(cnt==100) break;
				}
			}
			bKq = ErrorCode.OK;
		} else {
			bKq = ErrorCode.LOBBY_NOTFOUND;
		}		
		long[] lua = ArrayUtils.toPrimitive(lu.toArray(new Long[lu.size()])); 
		int user_listOffset = CMDGetLobbyUsers.createUidListVector(builder, lua);
		int glui = CMDGetLobbyUsers.createCMDGetLobbyUsers(builder, uid, gt, lobby_type, room_id, 0, user_listOffset);
		builder.finish(glui);
		rs = CMDGetLobbyUsers.getRootAsCMDGetLobbyUsers(builder.dataBuffer());
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		return Arrays.asList(bKq, channels, outparams);
	}
}
