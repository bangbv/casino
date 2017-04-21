package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import com.google.flatbuffers.FlatBufferBuilder;
import vn.com.vng.gsmobile.casino.entries.CMD;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.entries.maubinh.MBBattle;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGameMBShowCards;
import vn.com.vng.gsmobile.casino.flatbuffers.GameMBInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameMBShowCardUpdate;
import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.flatbuffers.MBShowType;
import vn.com.vng.gsmobile.casino.flatbuffers.PlayerMBCardInfo;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class MBShowCardsService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGameMBShowCards rq = (CMDGameMBShowCards) params.get(4);
		Long rId = rq.roomId();
		Room r = RoomManager.getRoom(rId);
		int userIndex = (int) rq.playerIdx();

		GameMBInfo rs = null;
		PlayerMBCardInfo pci = null;
		MBBattle b = null;
		if (r != null) {
			b = (MBBattle) r.getBattle();
			if (b != null) {
				synchronized (b) {
					List<Number> showType = b.getShowType(userIndex);
					if(showType.get(1).longValue() < rq.showId()){
						rs = (GameMBInfo) b.getData();
						if(rs != null){
							if(rs.gameId()==rq.gameId() && rs.state() == GameRoomState.Playing){
								pci = rs.cardList(userIndex);
								int st = rq.showType();
								if(st!=MBShowType.None){
									for (int x = 0; x < rq.cardsLength(); x++) {
										pci.mutateCards(x, rq.cards(x));
									}
								}
								pci.mutateShowType(st);
								b.setShowType(userIndex, st, rq.showId());
								bKq = ErrorCode.OK;
							}
							else
								bKq = ErrorCode.BATTLE_FINISHED;
						}
						else
							bKq = ErrorCode.BATTLE_INFO_NOTFOUND;
					}
					else
						bKq = ErrorCode.TURN_EXPIRED;
				}
			} else
				bKq = ErrorCode.BATTLE_NOTFOUND;
		} else
			bKq = ErrorCode.ROOM_NOTMATCH;

		List<?> lc = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, null);
		Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_REQUEST, lc, outparams);
		if(bKq == ErrorCode.OK && r!=null){
			FlatBufferBuilder builder2 = new FlatBufferBuilder(0);
			builder2.finish(GameMBShowCardUpdate.createGameMBShowCardUpdate(builder2, rq.playerIdx(), rq.showType()));
			GameMBShowCardUpdate rs2 = GameMBShowCardUpdate.getRootAsGameMBShowCardUpdate(builder2.dataBuffer());
			List<?> lc2 = r.getChannels();
			lc2.remove(params.get(0));
			List<?> outparams2 = Arrays.asList(CMD.MB_PARTNER_SHOW.cmd,CMD.MB_PARTNER_SHOW.subcmd,CMD.MB_PARTNER_SHOW.version, (byte)0, rs2);
			Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_REQUEST, lc2, outparams2);
		}
		//Kiểm tra kết thúc ván đấu
		if(bKq == ErrorCode.OK && b.isShowAll()){
			synchronized (b) {
				b.notifyAll();
			}
		}
		return Arrays.asList(bKq, null, null);
	}
}
