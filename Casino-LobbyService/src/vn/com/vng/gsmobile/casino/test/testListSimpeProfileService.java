package vn.com.vng.gsmobile.casino.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.ListSimpeProfileService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetListSimpleProfile;

public class testListSimpeProfileService {

	public static void main(String[] args) throws Exception {
		ListSimpeProfileService service = new ListSimpeProfileService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		List<Long> luid = new ArrayList<>();
		luid.add(48661781350006784L);
		long[] data = ArrayUtils.toPrimitive(luid.toArray(new Long[luid.size()]));
		int ulv = CMDGetListSimpleProfile.createUidListVector(builder, data);
		int rg = CMDGetListSimpleProfile.createCMDGetListSimpleProfile(builder, builder.createString("123"), ulv);
		builder.finish(rg);
		
		CMDGetListSimpleProfile rq = CMDGetListSimpleProfile.getRootAsCMDGetListSimpleProfile(builder.dataBuffer());
		List<?> rs = service.execute("1",  Arrays.asList("","","","",rq));
		System.out.println(rs.get(2));
	}
}
