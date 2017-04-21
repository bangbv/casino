package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.entries.bala.CaoBattle;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetGameInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.Game3LaGameInfo;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class BaLaBattleInfoService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGetGameInfo rq = (CMDGetGameInfo) params.get(4);
		Game3LaGameInfo rs = null;
		Room r = RoomManager.getRoom(rq.roomId());
		if(r != null){
			CaoBattle oBattle = (CaoBattle) r.getBattle();
			if(oBattle!=null){
				rs = (Game3LaGameInfo) oBattle.getData();
				if(rs != null){
					if(rq.gameId()==0 || rs.gameId() == rq.gameId()){
						synchronized (rs) {
							rs.mutateTimeRemaining(oBattle.getTimeRemaining());
						}
						bKq = ErrorCode.OK;
					}
					else
						bKq = ErrorCode.BATTLE_FINISHED;
				}
				else 
					bKq = ErrorCode.BATTLE_INFO_NOTFOUND;
			}
			else
				bKq = ErrorCode.BATTLE_NOTFOUND;
		}
		else
			bKq = ErrorCode.NOTEXISTS;
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,rs); //noi dung phan hoi
		return Arrays.asList(bKq, channels, outparams);
	}

}

