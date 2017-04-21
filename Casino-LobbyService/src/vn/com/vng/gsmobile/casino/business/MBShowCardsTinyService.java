package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.entries.maubinh.HandType;
import vn.com.vng.gsmobile.casino.entries.maubinh.MBBattle;
import vn.com.vng.gsmobile.casino.entries.maubinh.MBHand;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGameMBShowCards;
import vn.com.vng.gsmobile.casino.flatbuffers.GameMBInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.flatbuffers.PlayerMBCardInfo;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class MBShowCardsTinyService implements IService {

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
					Number showId = b.getShowTiny(userIndex);
					if(showId.longValue() < rq.showId()){
						rs = (GameMBInfo) b.getData();
						if(rs != null){
							if(rs.gameId()==rq.gameId() && rs.state() == GameRoomState.Playing){
								pci = rs.cardList(userIndex);
								if(rq.cardsLength()>0){
									for (int x = 0; x < rq.cardsLength(); x++) {
										pci.mutateCards(x, rq.cards(x));
									}
									b.setShowTiny(userIndex, rq.showId());
									bKq = ErrorCode.OK;
								}
								else
									bKq = ErrorCode.CARDS_INVALID;
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
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3), bKq, null);
		Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_REQUEST, lc, outparams);
		if (bKq == ErrorCode.OK && b != null && pci != null) {
			synchronized (b) {
				MBHand h = new MBHand(pci);
				pci.mutateHandType(h.getType());
				pci.mutateChiType(0, h.getChi(HandType.CHI1).getType());
				pci.mutateChiType(1, h.getChi(HandType.CHI2).getType());
				pci.mutateChiType(2, h.getChi(HandType.CHI3).getType());
			}
		}
		return Arrays.asList(bKq, null, null);
	}
}
