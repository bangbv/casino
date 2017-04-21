package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.SendMailService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDSendMail;

public class testSendMailService {

	public static void main(String[] args) throws Exception {
		SendMailService service = new SendMailService();
		// TODO Auto-generated method stub
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		int gl = CMDSendMail.createCMDSendMail(builder, builder.createString("f1600324756649871"), builder.createString("z9135826616168185785"), builder.createString("Hello Hoang, My name is Bang"));
		builder.finish(gl);
		CMDSendMail rq = CMDSendMail.getRootAsCMDSendMail(builder.dataBuffer());
		List<?> rs = service.execute("", Arrays.asList("", "", "", "", rq));
		System.out.println(rs.get(2));
	}
}
