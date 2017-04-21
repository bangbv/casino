package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.MailListService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetMailList;

public class testMailListService {

	public static void main(String[] args) throws Exception {
		MailListService service = new MailListService();
		// TODO Auto-generated method stub
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		Long uid = 47921862302351360l;
		int ml = CMDGetMailList.createCMDGetMailList(builder, uid, 0);
		builder.finish(ml);
		CMDGetMailList rq = CMDGetMailList.getRootAsCMDGetMailList(builder.dataBuffer());
		List<?> rs = service.execute("", Arrays.asList("", "", "", "", rq));
		System.out.println(rs.get(2));
	}
}
