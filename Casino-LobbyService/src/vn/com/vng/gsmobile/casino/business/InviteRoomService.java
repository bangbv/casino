package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.entries.ChannelType;
import vn.com.vng.gsmobile.casino.entries.Handshake;
import vn.com.vng.gsmobile.casino.entries.Lobby;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDInviteGame;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class InviteRoomService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDInviteGame rq = (CMDInviteGame) params.get(4);
		List<Channel> channels = null;
		Long uid = Handshake.getUser((Channel)params.get(0));
		if(RoomManager.validInviteRoom(uid)){
			if(rq.toLength()==0){
				Lobby l = RoomManager.getLobby((byte)rq.gameType());
				if(l != null){
					channels = l.getChannels();
					bKq = ErrorCode.OK;
				}
				else
					bKq = ErrorCode.LOBBY_NOTFOUND;
			}
			else{
				channels = new ArrayList<>();
				for(int j=0; j < rq.toLength(); j++){
					Channel c = Handshake.getChannel(rq.to(j), ChannelType.Game);
					if(c!=null && !channels.contains(c))
						channels.add(c);
				}
				bKq = ErrorCode.OK;
			}
		}
		else
			bKq = ErrorCode.SPAM;
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,rq); //noi dung phan hoi
		return Arrays.asList(bKq, channels, outparams);
	}

}

