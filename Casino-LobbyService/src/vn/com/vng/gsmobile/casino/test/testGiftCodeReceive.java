package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.GiftCodeReceiveService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDReceiveGiftCode;

public class testGiftCodeReceive {

	public static void main(String[] args) throws Exception {
		GiftCodeReceiveService service = new GiftCodeReceiveService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		
		int rg = CMDReceiveGiftCode.createCMDReceiveGiftCode(builder, builder.createString("3"), builder.createString("PRRVCFUK"), 0);
		builder.finish(rg);
		
		CMDReceiveGiftCode rq = CMDReceiveGiftCode.getRootAsCMDReceiveGiftCode(builder.dataBuffer());
		List<?> r = service.execute("",  Arrays.asList("","","","",rq));
		System.out.println(r.get(2));
	}
}
