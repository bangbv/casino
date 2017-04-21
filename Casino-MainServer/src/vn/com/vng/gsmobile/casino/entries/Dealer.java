package vn.com.vng.gsmobile.casino.entries;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.business.Service;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDQuitGame;
import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.flatbuffers.QuitState;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class Dealer implements Runnable {
	private boolean bInterrupt = false;
	private Room oRoom = null;
	private Long sFirstPlayer = null;
	private Card oFirstCard = null; 
	public Dealer(Long lRId) {
		// TODO Auto-generated constructor stub
		this.oRoom = RoomManager.getRoom(lRId);
	}
	
	public Dealer(Room oRoom) {
		// TODO Auto-generated constructor stub
		this.oRoom = oRoom;
	}
	
	public void release(){
		this.oRoom = null;
	}
	
	public void execute(){
		byte bKq = ErrorCode.UNKNOWN;
		this.setInterrupt(false);
		if(oRoom!=null){
			//0. release ván bài cũ
			oRoom.setBattle(null);
			//1. Kiểm tra người chơi: đăng ký rời phòng / mất kết nối/ không đủ tiền => kickout
			Iterator<Long> itPlayers = oRoom.getPlayers().iterator();
			while(itPlayers.hasNext()){
				Long uid = itPlayers.next();
				if(uid!=null && uid > 0){
					byte bReason = verify(uid);
					if(bReason!=ErrorCode.OK)
						bKq = kickout(uid, bReason);
				}
			}
			Iterator<Long> it = oRoom.getViewers().iterator();
			while(it.hasNext()){
				Long uid = it.next();
				if(uid!=null && uid > 0){
					byte bReason = verify(uid);
					if(bReason!=ErrorCode.OK)
						bKq = kickout(uid, bReason);
				}
			}
			//2. Nếu phòng còn 2 người trở lên thì chuẩn bị ván mới (không đủ thì bỏ qua)
			if(oRoom.getCurrentSit() >= 2){
				new Thread(this).start();
				bKq = ErrorCode.OK;
			}
		}
		Lib.getLogger().trace(Arrays.asList("execute", oRoom, this.bInterrupt, bKq, Dealer.class.getSimpleName()));
	}
	
	public void interrupt(){
		synchronized (this) {
			this.setInterrupt(true);
			this.notifyAll();
			Lib.getLogger().trace(Arrays.asList("interrupt", oRoom, this.bInterrupt, Dealer.class.getSimpleName()));
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(Const.IS_STOPPING) return; //Nếu đang tắt server thì bỏ qua
		Long lBattleId = null;
		try{
			if(oRoom!=null){
				Long lTimeInitBattle = GameConfig.load(false).getObject(GameConfig.GAMELIST).getObject(""+oRoom.getGameType()).getLong(GameConfig.TIME_INITBATTLE);
				//1. Báo chuẩn bị ván đấu mới sau lTimeInitBattle ms
				lBattleId = Lib.getNanoTimeId();
				List<Channel> lc = oRoom.getChannels();
				oRoom.setState(GameRoomState.Waiting_Game);
				Service.sendToClient(
						Dealer.class.getSimpleName(), 
						lBattleId.toString(), Service.CMDTYPE_REQUEST, 
						lc,
						Arrays.asList(CMD.PUSH_UPDATEROOM.cmd,CMD.PUSH_UPDATEROOM.subcmd,CMD.PUSH_UPDATEROOM.version,(byte)0, oRoom.toRoomUpdateInfo(lTimeInitBattle))							
					);
				synchronized (this) {
					try {
						this.wait(lTimeInitBattle);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(!this.bInterrupt){
					//2.1 Nếu không bị hủy thì tạo và bắt đầu ván đấu
					try{
						Battle oBattle = oRoom.setBattle(newBattle(lBattleId, oRoom));
						if(oBattle!=null){
							new Thread(oBattle).start();
						}
					}catch (Exception e) {
						// TODO: handle exception
						kickall();
					}
				}
				else{
					//2.2 Báo hủy tạo ván đấu mới: Đoạn này có thể bỏ qua vì đã có schedule push roomupdateinfo khi thay đổi người chơi
					oRoom.setState(GameRoomState.Waiting_Player);
					Service.sendToClient(
							Dealer.class.getSimpleName(), 
							lBattleId.toString(), Service.CMDTYPE_REQUEST, 
							lc,
							Arrays.asList(CMD.PUSH_UPDATEROOM.cmd,CMD.PUSH_UPDATEROOM.subcmd,CMD.PUSH_UPDATEROOM.version,(byte)0, oRoom.toRoomUpdateInfo(0l))							
						);
				}
			}
			Lib.getLogger().trace(Arrays.asList("run", lBattleId, oRoom, this.bInterrupt, Dealer.class.getSimpleName()));
		}catch(Exception e){
			Lib.getLogger().error(Arrays.asList("run", lBattleId, oRoom, this.bInterrupt, Lib.getStackTrace(e), Dealer.class.getSimpleName()));
		}
	}

	public void setInterrupt(boolean bInterrupt){
		this.bInterrupt = bInterrupt;
	}
	
	public Battle newBattle(Long lId, Room oRoom){
		Battle oBattle = null;
		try {
			Class<?> clsService = Class.forName(GameConfig.load(false).getObject(GameConfig.GAMELIST).getObject(""+oRoom.getGameType()).getString(GameConfig.GAMESERVICE));
			oBattle = (Battle) clsService.newInstance();
			oBattle.setId(lId);
			oBattle.setRoom(oRoom);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Lib.getLogger().error(Arrays.asList("newBattle", lId, oRoom, Lib.getStackTrace(e), Dealer.class.getSimpleName()));
			oBattle = null;
		}
		return oBattle;
	}
	
	public byte verify(Long uid){
		byte bKq = ErrorCode.OK;
		if(oRoom!=null){
			//1.1 check đăng ký rời phòng
			if(oRoom.getId().equals(RoomManager.getLeaveList().get(uid)))
				bKq = ErrorCode.LEAVED_ROOM;
			//1.2 check kết nối
			if(bKq == ErrorCode.OK && !Handshake.checkChannel(uid, ChannelType.Game))
				bKq = ErrorCode.PLAYER_CONNECTION_SLOWLY;
			//1.3 check tiền
			JsonObject rsc = (JsonObject) LocalCache.get(User.USERESOURCE_TABLENAME+uid);
			if(rsc!=null){
				Long coin = rsc.getLong(User.COIN);
				if(coin==null) coin = Long.MAX_VALUE;
				if(coin < oRoom.getRequireCoin())
					bKq = ErrorCode.MONEY_NOTENOUGH;
			}
			
		}
		else
			bKq = ErrorCode.NOTEXISTS;
		Lib.getLogger().trace(Arrays.asList("verify", uid, bKq, oRoom, Dealer.class.getSimpleName()));
		return bKq;
	}
	
	public void kickall(){
		Iterator<Long> itPlayers = oRoom.getPlayers().iterator();
		while(itPlayers.hasNext()){
			Long uid = itPlayers.next();
			if(uid!=null && uid > 0){
				kickout(uid, ErrorCode.BATTLE_DONTSTART);
			}
		}
		Iterator<Long> it = oRoom.getViewers().iterator();
		while(it.hasNext()){
			Long uid = it.next();
			if(uid!=null && uid > 0){
				byte bReason = verify(uid);
				if(bReason!=ErrorCode.OK)
					kickout(uid, ErrorCode.BATTLE_DONTSTART);
			}
		}
	}
	public byte kickout(Long uid, byte bReason){
		byte bKq = ErrorCode.UNKNOWN;
		if(oRoom!=null){
			bKq = RoomManager.leaveRoom(uid, oRoom.getId());
			if(bKq == ErrorCode.OK){
				byte quitAccept = QuitState.QUIT_ACCEPT;
				switch(bReason){
				case ErrorCode.MONEY_NOTENOUGH:
					quitAccept = QuitState.QUIT_NOT_ENOUGH_MONEY;
					break;
				case ErrorCode.PLAYER_CONNECTION_SLOWLY:
					quitAccept = QuitState.QUIT_POOR_CONNECTION;
					break;
				}
				FlatBufferBuilder builder = new FlatBufferBuilder(0);
				int irs = CMDQuitGame.createCMDQuitGame(builder, 
						uid, 
						oRoom.getId(), 
						quitAccept
					);
				builder.finish(irs);
				CMDQuitGame rs = CMDQuitGame.getRootAsCMDQuitGame(builder.dataBuffer());
				Channel c = Handshake.getChannel(uid, ChannelType.Game);
				Service.sendToClient(
						Dealer.class.getSimpleName(), 
						oRoom.getId()+"_"+System.currentTimeMillis(), Service.CMDTYPE_REQUEST, 
						c!=null?Arrays.asList(c):null,
						Arrays.asList(CMD.QUIT_ROOM.cmd,CMD.QUIT_ROOM.subcmd,CMD.QUIT_ROOM.version,(byte)0,rs)							
					);
			}
		}
		else
			bKq = ErrorCode.NOTEXISTS;
		RoomManager.getLeaveList().remove(uid);
		Lib.getLogger().trace(Arrays.asList("kickout", uid, bReason, bKq, oRoom, Dealer.class.getSimpleName()));
		return bKq;
	}
	
	public synchronized byte joinSit(Long sUID){
		if(oRoom.isFake()) return ErrorCode.ROOM_ISFULL_VIEWERS;
		
		byte bKq = verify(sUID);
		if(bKq==ErrorCode.OK){
			int iIdx = -1;
			for(int i=0; i<oRoom.getPlayers().size(); i++){
				if(sUID.equals(oRoom.getPlayers().get(i))){
					iIdx = i;
					break;
				}
			}
			if(iIdx==-1){
				for(iIdx = 0; iIdx < oRoom.getMaxSit(); iIdx++){
					if(oRoom.getSitList().get(iIdx) == (byte)0){
						oRoom.getSitList().set(iIdx, (byte)1);
						oRoom.getPlayers().set(iIdx, sUID);
						oRoom.setCurrentSit(oRoom.getCurrentSit()+1);
						break;
					}
				}
			}
			if(iIdx>=0 && iIdx < oRoom.getMaxSit())	{
				if(oRoom.getCurrentSit()==1){
					oRoom.setHostIdx(iIdx);
					oRoom.setState(GameRoomState.Waiting_Player);
				}
				else if(oRoom.getCurrentSit()==2 && oRoom.getState() < GameRoomState.Playing){
					this.setFirstPlayer(null);
					this.setFirstCard(null);
					this.execute();
				}
				bKq = ErrorCode.OK;
			}
			else
				bKq = ErrorCode.ROOM_ISFULL;
		}
		Lib.getLogger().trace(Arrays.asList("joinSit", sUID, bKq, oRoom, Dealer.class.getSimpleName()));
		return bKq;
	}
	public byte joinViewer(Long sUID){
		synchronized (oRoom.getViewers()) {
			byte bKq = ErrorCode.UNKNOWN;
			if(!oRoom.isFake() && oRoom.getViewers().size() < oRoom.getMaxViewers()){
				if(!oRoom.getViewers().contains(sUID)){
					oRoom.getViewers().add(sUID);
				}
				bKq = ErrorCode.OK;
			}
			else
				bKq = ErrorCode.ROOM_ISFULL_VIEWERS;
			Lib.getLogger().trace(Arrays.asList("joinViewer", sUID, bKq, oRoom, Dealer.class.getSimpleName()));
			return bKq;
		}
	}
	public byte leaveViewer(Long sUID){
		synchronized (oRoom.getViewers()) {
			byte bKq = ErrorCode.UNKNOWN;
			RoomManager.getLeaveList().remove(sUID);
			oRoom.getViewers().remove(sUID);
			bKq = ErrorCode.OK;
			Lib.getLogger().trace(Arrays.asList("leaveViewer", sUID, bKq, oRoom, Dealer.class.getSimpleName()));
			return bKq;
		}
	}
	public boolean isViewer(Long sUID){
		synchronized (oRoom.getViewers()) {
			return oRoom.getViewers().contains(sUID);
		}
	}
	public synchronized byte leaveSit(Long sUID){
		byte bKq = ErrorCode.UNKNOWN;
		if(!checkIsPlaying(sUID)){
			RoomManager.getLeaveList().remove(sUID);
			int iIdx = -1;
			for(int i=0; i<oRoom.getPlayers().size(); i++){
				if(sUID.equals(oRoom.getPlayers().get(i))){
					iIdx = i;
					break;
				}
			}
			if(iIdx >=0 && iIdx < oRoom.getMaxSit()){
				oRoom.getSitList().set(iIdx, (byte)0);
				oRoom.getPlayers().set(iIdx, null);
				oRoom.setCurrentSit(oRoom.getCurrentSit()-1);
				if(oRoom.getCurrentSit()==0){
					oRoom.setHostIdx(-1);
					oRoom.setState(GameRoomState.Idle);
				}
				else if(iIdx == oRoom.getHostIdx())
					setNextHostIdx();
				
				//Nếu là người được chơi trước trong ván tiếp theo => thì chọn người kế bên (theo vòng) làm người đánh trước
				if(sUID.equals(sFirstPlayer)){
					for(int i=iIdx+1; i < oRoom.getMaxSit() + iIdx; i++){
						int iNextPlayer = i % oRoom.getMaxSit();
						if(oRoom.getSitList().get(iNextPlayer) == (byte)1){
							setFirstPlayer(oRoom.getPlayers().get(iNextPlayer));
							setFirstCard(null);
							break;
						}
					}
				}
				if(oRoom.getCurrentSit()==1){// && oRoom.getState() != GameRoomState.Playing){
					oRoom.setState(GameRoomState.Waiting_Player);
					this.interrupt();
				}
				bKq = ErrorCode.OK;
			}
			else
				bKq = ErrorCode.NOTEXISTS;
		}
		else{
			RoomManager.getLeaveList().put(sUID, oRoom.getId());
			bKq = ErrorCode.PLAYING_ROOM;
		}
		Lib.getLogger().trace(Arrays.asList("leaveSit", sUID, bKq, oRoom, Dealer.class.getSimpleName()));
		return bKq;
	}
	
	
	public boolean checkIsPlaying(Long uid){
		Battle oBattle = oRoom.getBattle();
		return oRoom.getState() == GameRoomState.Playing && oBattle != null && oBattle.isPlaying(uid);
	}
	
	public Long getFirstPlayer(){
		return this.sFirstPlayer;
	}
	
	public void setFirstPlayer(Long uid){
		this.sFirstPlayer = uid;

	}	
	
	public Card getFirstCard(){
		return this.oFirstCard;
	}
	
	public void setFirstCard(Card card){
		this.oFirstCard = card;
	}
	
	public void setNextHostIdx(){
		int iIdx = oRoom.getHostIdx();
		for(int i=iIdx+1; i < oRoom.getMaxSit() + iIdx; i++){
			int iNextHost = i % oRoom.getMaxSit();
			if(oRoom.getSitList().get(iNextHost) == (byte)1){
				oRoom.setHostIdx(iNextHost);
				break;
			}
		}

	}
}
