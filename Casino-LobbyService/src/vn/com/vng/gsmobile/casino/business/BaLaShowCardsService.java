package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import vn.com.vng.gsmobile.casino.entries.CMD;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.entries.bala.CaoBattle;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGame3LaShowCard;
import vn.com.vng.gsmobile.casino.flatbuffers.Game3LaGameInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.flatbuffers.Player3LaCardInfo;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class BaLaShowCardsService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGame3LaShowCard rq = (CMDGame3LaShowCard) params.get(4);
		Long rId = rq.roomId();
		Room r = RoomManager.getRoom(rId);
		int userIndex = (int) rq.userIndex();

		Game3LaGameInfo rs = null;
		Player3LaCardInfo pci = null;
		CaoBattle b = null;
		if (r != null) {
			b = (CaoBattle) r.getBattle();
			if (b != null) {
				rs = (Game3LaGameInfo) b.getData();
				if(rs != null){
					if(rs.gameId()==rq.gameId() && rs.state() == GameRoomState.Playing){
						synchronized (rs) {
							pci = rs.cardList(userIndex);
							for (int x = 0; x < rq.showedCardsLength(); x++) {
								int cardId = rq.showedCards(x);
								pci.cards(cardId).mutateShowFlag(1);
							}
							rs.mutateTimeRemaining(b.getTimeRemaining());
						}
						bKq = ErrorCode.OK;
					}
					else
						bKq = ErrorCode.BATTLE_FINISHED;
				}
				else
					bKq = ErrorCode.BATTLE_INFO_NOTFOUND;
			} else
				bKq = ErrorCode.BATTLE_NOTFOUND;
		} else
			bKq = ErrorCode.ROOM_NOTMATCH;

		List<?> lc = r!=null?r.getChannels():Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(CMD.CAO_BATTLE_INFO.cmd, CMD.CAO_BATTLE_INFO.subcmd, CMD.CAO_BATTLE_INFO.version, bKq, rs);
		Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_REQUEST, lc, outparams);
		//Kiểm tra kết thúc ván đấu
		if(bKq == ErrorCode.OK){
			boolean isFinished = true;
			for(int i = 0 ; i < rs.cardListLength(); i++){
				int cnt = rs.cardList(i).cardsLength();
				for(int j = 0; j < cnt; j++)
					if(rs.cardList(i).cards(j).showFlag()==0)
						isFinished = false;
			}
			if(isFinished)
				synchronized (b) {
					b.notifyAll();
				}
		}
		return Arrays.asList(bKq, null, null);
	}
}
