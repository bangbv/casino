package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.entries.ChannelType;
import vn.com.vng.gsmobile.casino.entries.Dealer;
import vn.com.vng.gsmobile.casino.entries.Handshake;
import vn.com.vng.gsmobile.casino.entries.Room;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.entries.tlmn.TLMNCardKind;
import vn.com.vng.gsmobile.casino.entries.tlmn.TLMNHand;
import vn.com.vng.gsmobile.casino.entries.tlmn.TLMNBattle;
import vn.com.vng.gsmobile.casino.entries.tlmn.TLMNCard;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGameTLMNShow;
import vn.com.vng.gsmobile.casino.flatbuffers.CardID;
import vn.com.vng.gsmobile.casino.flatbuffers.GameTLMNInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.PlayerTLMNCardInfo;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class TLMNDLBotShowCardsService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGameTLMNShow rq = (CMDGameTLMNShow) params.get(4);
		Long rId = rq.roomId();
		Room r = RoomManager.getRoom(rId);
		int userIndex = (int) rq.playerIdx();

		GameTLMNInfo rs = null;
		PlayerTLMNCardInfo pci = null;
		TLMNBattle b = null;
		if (r != null) {
			b = (TLMNBattle) r.getBattle();
			
			if (b != null) {
				synchronized (b) {
					Dealer d = r.getDealer();
					if(d!=null){
						rs = (GameTLMNInfo) b.getData();
						if(rs != null){
							if(rs.gameId()==rq.gameId() && !b.isFinish()){
								//1. Kiểm tra lượt đánh có hợp lệ không
								boolean turnValid = rq.turnIdx() == b.getTurnIdx();
								if(turnValid){
									TLMNHand lastPlayCards = b.getLastPlayCards();
									TLMNHand nowPlayCards = null;
									boolean isFirstTurnOfBattle = rq.turnIdx()==1;
									boolean isFirstTurnOfRoom = (isFirstTurnOfBattle && d.getFirstCard()!=null);
									if(lastPlayCards != null || isFirstTurnOfBattle){
										//2. Kiểm tra bài đánh hợp lệ không
										TLMNCard firstCard = (TLMNCard) (isFirstTurnOfRoom?d.getFirstCard():new TLMNCard(CardID.Card_None));
										pci = rs.cardList(userIndex);
										boolean cardValid = true;
										boolean hasCardMin = isFirstTurnOfRoom?false:true;
										List<TLMNCard> cl = new ArrayList<>();
										for (int x = 0; x < rq.cardListLength(); x++) {
											int cardIdx = rq.cardList(x);
											if(pci.cards(cardIdx).turnIdx()==0){
												TLMNCard c = new TLMNCard(pci.cards(cardIdx).cardId());
												cl.add(c);
												if(isFirstTurnOfRoom && c.Id == firstCard.Id)
													hasCardMin = true;
											}
											else{
												cardValid = false;
												break;
											}
										}
										if(cardValid){
											nowPlayCards = new TLMNHand(cl);
											if((isFirstTurnOfRoom && hasCardMin)||b.isRingNew()){
												if(nowPlayCards.getKind()==TLMNCardKind.NONE)
													cardValid = false;
												else
													cardValid = true;
											}
											else{
												if(nowPlayCards.compareTo(lastPlayCards)>0)
													cardValid = true;
												else
													cardValid = false;
											}
										}
										if(cardValid){
											//3. Kiểm tra người đánh hợp lệ không
											if(Handshake.verify((Channel)params.get(0), pci.playerId(), ChannelType.Game)){
												boolean interrupt = lastPlayCards!=null && (lastPlayCards.isHeo() || lastPlayCards.isHang()) && nowPlayCards.isHang();
												boolean interrupt_period = (interrupt && nowPlayCards.isBonDoiThong()) && b.getLastPlayerIdx()!=rq.playerIdx(); //chặt không cần vòng hoặc
												boolean playerValid = interrupt_period || rq.playerIdx() == b.getPlayIdx(); //đúng người
												if(playerValid){
													//4. Đánh dấu bài đã show
													for (int x = 0; x < rq.cardListLength(); x++) {
														int cardIdx = rq.cardList(x);
														pci.cards(cardIdx).mutateTurnIdx(b.getTurnIdx());
														pci.cards(cardIdx).mutateRingIdx(b.getRingIdx());
													}
													if(nowPlayCards.isHeo()){
														int score = nowPlayCards.getInterruptPenanceScore();
														b.setInterruptScoreHeo(score);
													}
													if(interrupt_period){
														int score = lastPlayCards.isHeo()?b.getInterruptScoreHeo():lastPlayCards.getInterruptPenanceScore();
														b.setShowCardInterruptPeriod(score, rq.playerIdx());
													}
													else if(interrupt){
														int score = lastPlayCards.isHeo()?b.getInterruptScoreHeo():lastPlayCards.getInterruptPenanceScore();
														b.setShowCardInterrupt(score);
													}
													else
														b.setShowCardNormal();
													b.setLastPlayCards(nowPlayCards, rq.playerIdx());
													b.notifyAll();
													bKq = ErrorCode.OK;
												}
												else
													bKq = ErrorCode.PLAYER_INVALID;
											}
											else
												bKq = ErrorCode.USER_INVALID;
										}
										else{
											b.notifyAll();
											bKq = ErrorCode.CARDS_INVALID;
										}
									}
									else
										bKq = ErrorCode.NOTEXISTS;
								}
								else
									bKq = ErrorCode.TURN_EXPIRED;
							}
							else
								bKq = ErrorCode.BATTLE_FINISHED;
						}
						else
							bKq = ErrorCode.BATTLE_INFO_NOTFOUND;
					}
					else
						bKq = ErrorCode.DEALER_NOTFOUND;
				}
			} 
			else
				bKq = ErrorCode.BATTLE_NOTFOUND;
		} 
		else
			bKq = ErrorCode.ROOM_NOTMATCH;

		List<?> channels = bKq == ErrorCode.OK?null:Arrays.asList(params.get(0));
		List<?> outparams = bKq == ErrorCode.OK?null:Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,null); //noi dung phan hoi
		return Arrays.asList(bKq, channels, outparams);
	}
}
