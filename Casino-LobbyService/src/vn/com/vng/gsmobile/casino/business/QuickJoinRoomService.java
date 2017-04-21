package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDQuickJoinGame;
import vn.com.vng.gsmobile.casino.flatbuffers.RoomResponse;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class QuickJoinRoomService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDQuickJoinGame rq = (CMDQuickJoinGame) params.get(4);
		RoomResponse rs = null;
		Room r = null;
		r = RoomManager.getRoomByUser(rq.uid());
		if(r!=null){
			if(r.getDealer().checkIsPlaying(rq.uid())){
				bKq = ErrorCode.USER_PLAYING;
			}
			else{
				RoomManager.leaveRoom(rq.uid(), r.getId());
				if(r.getGameType()!=rq.gameType())
					RoomManager.leaveLobby(rq.uid(), r.getGameType());
			}
		}
		if(bKq!=ErrorCode.USER_PLAYING){
			List<?> lKq = RoomManager.quickJoinRoom(rq.uid(), rq.coin(), (byte)rq.gameType(), (byte)rq.lobbyType());
			bKq = (byte)lKq.get(0);
			r = (Room)lKq.get(1);
		}
		if(r!=null){
			rs = r.toRoomResponse(rq.reqToken());
		}
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,rs); //noi dung phan hoi
		return Arrays.asList(bKq, channels, outparams);
	}

}

