package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.entries.ChannelType;
import vn.com.vng.gsmobile.casino.entries.Handshake;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.entries.maubinh.MBBattle;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetGameInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.CardID;
import vn.com.vng.gsmobile.casino.flatbuffers.GameMBInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.flatbuffers.PlayerMBCardInfo;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class MBBattleInfoService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGetGameInfo rq = (CMDGetGameInfo) params.get(4);
		GameMBInfo rs = null;
		if(Handshake.verify((Channel)params.get(0), rq.userId(), ChannelType.Game)){
			Room r = RoomManager.getRoom(rq.roomId());
			if(r != null){
				MBBattle oBattle = (MBBattle) r.getBattle();
				if(oBattle!=null){
					synchronized (oBattle) {
						GameMBInfo data = (GameMBInfo) oBattle.getData().clone();
						if(data != null){
							rs = (GameMBInfo) data.clone();
							if(rq.gameId()==0 || rs.gameId()==rq.gameId()){
								//1. che bài người khác nếu ván đấu chưa kết thúc
								if(rs.state() == GameRoomState.Playing){
									for(int j = 0; j < rs.cardListLength(); j++){
										PlayerMBCardInfo pci = rs.cardList(j);
										if(rq.userId()!=pci.playerId()){
											for(int i = 0; i < pci.cardsLength(); i++){
												pci.mutateCards(i, CardID.Card_Hide);
											}
										}
									}
								}
								//2. cập nhật thời gian còn lại
								rs.mutateTransTime(System.currentTimeMillis());
								rs.mutateTimeRemaining(oBattle.getTimeRemaining());
								bKq = ErrorCode.OK;
							}
							else
								bKq = ErrorCode.BATTLE_FINISHED;
						}
						else
							bKq = ErrorCode.BATTLE_INFO_NOTFOUND;
					}
				}
				else
					bKq = ErrorCode.BATTLE_NOTFOUND;
			}
			else
				bKq = ErrorCode.NOTEXISTS;
		}
		else
			bKq = ErrorCode.USER_INVALID;
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,rs); //noi dung phan hoi
		return Arrays.asList(bKq, channels, outparams);
	}

}

