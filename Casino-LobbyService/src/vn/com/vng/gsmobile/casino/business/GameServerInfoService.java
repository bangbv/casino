package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import com.google.flatbuffers.FlatBufferBuilder;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.entries.UnlockLobby;
import vn.com.vng.gsmobile.casino.flatbuffers.GameServerInfo;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class GameServerInfoService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		GameServerInfo rq = (GameServerInfo) params.get(4);
		GameServerInfo rs = null;
		byte bGameType = (byte)rq.gameType();
		byte bLobbyType = UnlockLobby.getMaxUnlock(rq.uid(), bGameType);
		List<?> lKq = RoomManager.getBetAndLobby(rq.uid(), bGameType, rq.coin(), bLobbyType);
		if(lKq!=null)
			bLobbyType = (byte) lKq.get(1);
		FlatBufferBuilder builder = new FlatBufferBuilder();
		int irs = GameServerInfo.createGameServerInfo(builder, 
				rq.uid(),
				rq.gameType(), 
				bLobbyType, 
				builder.createString("ws://120.138.76.130:8080/websocket"),
				rq.coin()
			);
		builder.finish(irs);
		rs = GameServerInfo.getRootAsGameServerInfo(builder.dataBuffer());
		bKq = ErrorCode.OK;
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,rs); //noi dung phan hoi
		return Arrays.asList(bKq, channels, outparams);
	}

}

