package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.GiftListService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetGiftList;

public class testGiftListService {

	public static void main(String[] args) throws Exception {
		GiftListService service = new GiftListService();
		// TODO Auto-generated method stub
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		long uid = 47921862302351360l;
		int gl = CMDGetGiftList.createCMDGetGiftList(builder, uid, 0, 0, 0, 0, 0, 0);
		builder.finish(gl);
		CMDGetGiftList rq = CMDGetGiftList.getRootAsCMDGetGiftList(builder.dataBuffer());	
		List<?> listResult = service.execute("",  Arrays.asList("","","","",rq));
		System.out.println(listResult.get(2));
		
	}
}
