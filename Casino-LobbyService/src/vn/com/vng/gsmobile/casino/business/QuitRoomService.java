package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDQuitGame;
import vn.com.vng.gsmobile.casino.flatbuffers.QuitState;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class QuitRoomService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDQuitGame rq = (CMDQuitGame) params.get(4);
		CMDQuitGame rs = null;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		int irs = 0;
		switch(rq.state()){
		case QuitState.QUIT_REGISTER:
			bKq = RoomManager.leaveRoom(rq.uid(), rq.roomId());
			irs = CMDQuitGame.createCMDQuitGame(builder, 
					rq.uid(), 
					rq.roomId(), 
					(bKq==ErrorCode.OK||bKq==ErrorCode.NOTEXISTS)?QuitState.QUIT_ACCEPT:QuitState.QUIT_REJECT
				);
			builder.finish(irs);
			break;
		case QuitState.QUIT_UNREGISTER:
			RoomManager.getLeaveList().remove(rq.uid());
			irs = CMDQuitGame.createCMDQuitGame(builder, 
					rq.uid(), 
					rq.roomId(), 
					QuitState.QUIT_REJECT
				);
			bKq = ErrorCode.OK;
			break;
		}
		builder.finish(irs);
		rs = CMDQuitGame.getRootAsCMDQuitGame(builder.dataBuffer());
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,rs); //noi dung phan hoi
		return Arrays.asList(bKq, channels, outparams);
	}

}

