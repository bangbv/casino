package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.ChatInGameService;
import vn.com.vng.gsmobile.casino.flatbuffers.ChatMsgRoom;

public class testChatInGameService {

	public static void main(String[] args) throws Exception {
		ChatInGameService service = new ChatInGameService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		
		int msgOffset = builder.createString("hello");
		int from_uidOffset = builder.createString("g1483526264139");
		int from_nameOffset = builder.createString("Khách");
		int room_idOffset = builder.createString("21");
		
		int cmr = ChatMsgRoom.createChatMsgRoom(builder, msgOffset, from_uidOffset, from_nameOffset, room_idOffset);
		builder.finish(cmr);
		
		ChatMsgRoom rq = ChatMsgRoom.getRootAsChatMsgRoom(builder.dataBuffer());
		service.execute("",  Arrays.asList("","","","",rq));
	}
}
