package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import com.google.flatbuffers.FlatBufferBuilder;
import vn.com.vng.gsmobile.casino.entries.CMD;
import vn.com.vng.gsmobile.casino.entries.Gift;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.entries.UnlockLobby;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDJoinLobby;
import vn.com.vng.gsmobile.casino.flatbuffers.ItemType;
import vn.com.vng.gsmobile.casino.flatbuffers.LobbyCondition;
import vn.com.vng.gsmobile.casino.flatbuffers.LobbyRoom;
import vn.com.vng.gsmobile.casino.flatbuffers.UserCondition;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class JoinLobbyService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDJoinLobby rq = (CMDJoinLobby) params.get(4);
		LobbyRoom rs = null;
		//Tìm lobby phù hợp
		byte bGameType = (byte)rq.gameType();
		byte bLobbyType = (byte) rq.lobbyType();
		//Lấy danh sách phòng
		FlatBufferBuilder builder = new FlatBufferBuilder(0);		
		List<Integer> l = new ArrayList<>();
		for(Room r : RoomManager.getRoomList(bGameType, bLobbyType)){
			l.add(r.toRoomInfo(builder));
		}
		int ilr = LobbyRoom.createLobbyRoom(builder, 
				bLobbyType, 
				bGameType, 
				LobbyRoom.createRoomListVector(builder, ArrayUtils.toPrimitive(l.toArray(new Integer[l.size()])))
			);
		builder.finish(ilr);
		rs = LobbyRoom.getRootAsLobbyRoom(builder.dataBuffer());
		Byte bOldGameType = RoomManager.getGameTypeByUser(rq.uid());
		if(bOldGameType!=null && bGameType!=bOldGameType){
			RoomManager.leaveLobby(rq.uid(), bOldGameType);
		}
		RoomManager.joinLobby(rq.uid(), bGameType);
		bKq = ErrorCode.OK;
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,rs); //noi dung phan hoi
		Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_RESPONSE, channels, outparams);
		//update unlock lobby
		if(UnlockLobby.updateMaxUnlock(rq.uid(), bGameType)){
			byte bNewUnlock = UnlockLobby.getMaxUnlock(rq.uid(), bGameType);
			FlatBufferBuilder builder2 = new FlatBufferBuilder(0);
			builder2.finish(UserCondition.createUserCondition(builder2, 
					rq.uid(),
					UserCondition.createGameLobbyVector(builder2, 
							new int[]{LobbyCondition.createLobbyCondition(builder2, bGameType, bNewUnlock)}
					)
			));
			UserCondition uc = UserCondition.getRootAsUserCondition(builder2.dataBuffer());
			Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_RESPONSE, channels, 
					Arrays.asList(CMD.USER_CONDITION.cmd,CMD.USER_CONDITION.subcmd,CMD.USER_CONDITION.version,(byte)0,uc)
			);
			//Thông báo unlock lobby
			Gift.sendGiftSystem(rq.uid(), ItemType.Item_Coin, 100000, 0, "Chúc mừng bạn đã lên sàn mới");
		}
		return Arrays.asList(bKq, null, null);
	}

}

