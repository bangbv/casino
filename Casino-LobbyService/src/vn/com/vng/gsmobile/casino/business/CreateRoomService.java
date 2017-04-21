package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDCreateRoom;
import vn.com.vng.gsmobile.casino.flatbuffers.RoomResponse;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class CreateRoomService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDCreateRoom rq = (CMDCreateRoom) params.get(4);
		RoomResponse rs = null;
		if(RoomManager.validCreateRoom(rq.uid())){
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
				String sDesc = rq.description()==null?RoomManager.getRoomDesc((byte)rq.gameType()):rq.description();
				List<?> lKq = RoomManager.createRoom(rq.uid(), sDesc, (byte)rq.gameType(), (byte)rq.lobbyType(), rq.betValue(), rq.bigBet(), rq.password());
				bKq = (byte)lKq.get(0);
				r = (Room)lKq.get(1);
			}
			if(r!=null)
				rs = r.toRoomResponse(rq.reqToken());
		}
		else
			bKq = ErrorCode.SPAM;
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,rs); //noi dung phan hoi
		return Arrays.asList(bKq, channels, outparams);
	}

}

