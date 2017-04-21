package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.GiftReceiveService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDReceiveGift;
import vn.com.vng.gsmobile.casino.flatbuffers.GiftSource;

public class testGiftReceive {

	public static void main(String[] args) throws Exception {
		GiftReceiveService service = new GiftReceiveService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		
		long uid = 47921862302351360l;
		int rg = CMDReceiveGift.createCMDReceiveGift(builder, uid, builder.createString("3"), GiftSource.From_VipGifts, 0);
		builder.finish(rg);
		
		CMDReceiveGift rq = CMDReceiveGift.getRootAsCMDReceiveGift(builder.dataBuffer());
		service.execute("1",  Arrays.asList("","","","",rq));
	}
}
