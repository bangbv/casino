package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.OptionUpdateService;
import vn.com.vng.gsmobile.casino.flatbuffers.UserAppSetting;

public class testOptionUpdateService {

	public static void main(String[] args) throws Exception {
		OptionUpdateService service = new OptionUpdateService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);		
		int usi = UserAppSetting.createUserAppSetting(builder, builder.createString("3"), 1, 0, 0, 0);
		builder.finish(usi);		
		UserAppSetting rq = UserAppSetting.getRootAsUserAppSetting(builder.dataBuffer());
		List<?> rs = service.execute("",  Arrays.asList("","","","",rq));
		System.out.println(rs.get(2));
	}
}
