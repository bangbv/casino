package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.ReceivedAchievementRewardService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDReceivedAchievementReward;

public class testReceiveAchievementReward {

	public static void main(String[] args) throws Exception {
		ReceivedAchievementRewardService service = new ReceivedAchievementRewardService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		int rar = CMDReceivedAchievementReward.createCMDReceivedAchievementReward(builder, builder.createString("3"), 0, 0, 0, 0, 0);
		builder.finish(rar);
		CMDReceivedAchievementReward rq = CMDReceivedAchievementReward.getRootAsCMDReceivedAchievementReward(builder.dataBuffer());		
		List<?> rs = service.execute("",  Arrays.asList("","","","",rq));
		System.out.println(rs.get(2));
	}

}
