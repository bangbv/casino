package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.entries.CMD;
import vn.com.vng.gsmobile.casino.entries.Dealer;
import vn.com.vng.gsmobile.casino.entries.Handshake;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetGameStatus;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDQuitGame;
import vn.com.vng.gsmobile.casino.flatbuffers.QuitState;
import vn.com.vng.gsmobile.casino.flatbuffers.RoomUpdateInfo;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class GetGameStatusService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGetGameStatus rq = (CMDGetGameStatus) params.get(4);
		RoomUpdateInfo rs = null;
		Room r = RoomManager.getRoom(rq.roomId());
		if(r != null){
			if(r.getGameType() == rq.gameType()){
				rs = r.toRoomUpdateInfo();//-1l);
				bKq = ErrorCode.OK;
			}
			else
				bKq = ErrorCode.ROOM_NOTMATCH;
		}
		else{
			bKq = ErrorCode.NOTEXISTS;
			Long uid = Handshake.getUser((Channel)params.get(0));
			FlatBufferBuilder builder = new FlatBufferBuilder(0);
			int irs = CMDQuitGame.createCMDQuitGame(builder, 
					uid, 
					rq.roomId(), 
					QuitState.QUIT_ACCEPT
				);
			builder.finish(irs);
			CMDQuitGame rs2 = CMDQuitGame.getRootAsCMDQuitGame(builder.dataBuffer());
			Service.sendToClient(
					Dealer.class.getSimpleName(), 
					rq.roomId()+"_"+System.currentTimeMillis(), Service.CMDTYPE_REQUEST, 
					Arrays.asList(params.get(0)),
					Arrays.asList(CMD.QUIT_ROOM.cmd,CMD.QUIT_ROOM.subcmd,CMD.QUIT_ROOM.version,(byte)0,rs2)							
				);
		}
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,rs); //noi dung phan hoi
		return Arrays.asList(bKq, channels, outparams);
	}

}

