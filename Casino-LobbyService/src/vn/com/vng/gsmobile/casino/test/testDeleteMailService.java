package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.DeleteMailService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDDeleteMail;

public class testDeleteMailService {

	public static void main(String[] args) throws Exception {
		DeleteMailService service = new DeleteMailService();
		// TODO Auto-generated method stub
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		Long uid = 47921862302351360l;
		int ml = CMDDeleteMail.createCMDDeleteMail(builder, uid, builder.createString("1491473354817"), 2);
		builder.finish(ml);
		CMDDeleteMail rq = CMDDeleteMail.getRootAsCMDDeleteMail(builder.dataBuffer());
		List<?> rs = service.execute("", Arrays.asList("", "", "", "", rq));
		System.out.println(rs.get(2));
	}
}
