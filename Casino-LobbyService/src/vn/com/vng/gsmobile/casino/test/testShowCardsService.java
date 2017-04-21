package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.BaLaShowCardsService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGame3LaShowCard;

public class testShowCardsService {

	public static void main(String[] args) throws Exception {
		BaLaShowCardsService service = new BaLaShowCardsService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		
		int room_idOffset = builder.createString("21");
		int game_idOffset = builder.createString("123");
		int user_index = builder.createString("0");
		int showed_cardsOffset = builder.createString("0");
		
		int g3sc = CMDGame3LaShowCard.createCMDGame3LaShowCard(builder, room_idOffset, game_idOffset, user_index, showed_cardsOffset);
		builder.finish(g3sc);
		
		CMDGame3LaShowCard rq = CMDGame3LaShowCard.getRootAsCMDGame3LaShowCard(builder.dataBuffer());
		service.execute("",  Arrays.asList("","","","",rq));
	}
}
