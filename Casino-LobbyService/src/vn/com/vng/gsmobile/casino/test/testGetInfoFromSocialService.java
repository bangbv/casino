package vn.com.vng.gsmobile.casino.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.GetInfoFromSocialService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetInfoFromSocial;

public class testGetInfoFromSocialService {

	public static void main(String[] args) throws Exception {
		GetInfoFromSocialService service = new GetInfoFromSocialService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		List<Integer> lsid = new ArrayList<>();
//		int sidi0 = builder.createString("7596991611009478384");
//		lsid.add(sidi0);
//		int sidi1 = builder.createString("10202676751837204");
//		lsid.add(sidi1);
//		int sidi2 = builder.createString("10206782286594835");
//		lsid.add(sidi2);

		int[] asid = ArrayUtils.toPrimitive(lsid.toArray(new Integer[lsid.size()]));
		int slv = CMDGetInfoFromSocial.createSidListVector(builder, asid);
		int ifs  = CMDGetInfoFromSocial.createCMDGetInfoFromSocial(builder, slv, 0);
		builder.finish(ifs);
		CMDGetInfoFromSocial rq = CMDGetInfoFromSocial.getRootAsCMDGetInfoFromSocial(builder.dataBuffer());
		List<?> rs = service.execute("", Arrays.asList("", "", "", "", rq));
		System.out.println(rs.get(2));
	}
}
