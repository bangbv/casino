package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.GetLobbyUserService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetLobbyUsers;

public class testGetLobbyUserService {

	public static void main(String[] args) throws Exception {
		GetLobbyUserService service = new GetLobbyUserService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		
		int rg = CMDGetLobbyUsers.createCMDGetLobbyUsers(builder, 0, 0, 0, 0, 0, 0);
		builder.finish(rg);
		
		CMDGetLobbyUsers rq = CMDGetLobbyUsers.getRootAsCMDGetLobbyUsers(builder.dataBuffer());
		service.execute("1",  Arrays.asList("","","","",rq));
	}
}
