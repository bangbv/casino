package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.GetAchievementService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetAchievement;

public class testGetAchievement {

	public static void main(String[] args) throws Exception {
		GetAchievementService service = new GetAchievementService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		Long uid = 47921862302351360l;
		int ga = CMDGetAchievement.createCMDGetAchievement(builder, uid, 0, 0);
		builder.finish(ga);
		CMDGetAchievement rq = CMDGetAchievement.getRootAsCMDGetAchievement(builder.dataBuffer());		
		List<?> rs = service.execute("",  Arrays.asList("","","","",rq));
		System.out.println(rs.get(2));
	}

}
