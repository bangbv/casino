package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.entries.AchievementMission;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetAchievement;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

/**
 * @author bangbv
 */
public class GetAchievementService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGetAchievement rq = (CMDGetAchievement) params.get(4);
		CMDGetAchievement rs = null;
		Long uid = rq.uid();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);		
		List<Integer> lua = new ArrayList<>();
		List<Integer> lum = new ArrayList<>();
		//AchievementMission.addAchievement(uid, builder, lua);
		AchievementMission.getlistMissionAchievement(uid, builder, lum);
		int[] lai = ArrayUtils.toPrimitive(lua.toArray(new Integer[lua.size()]));
		int[] lmi = ArrayUtils.toPrimitive(lum.toArray(new Integer[lum.size()]));
		int alv = CMDGetAchievement.createAchievementListVector(builder, lai);
		int dmlv = CMDGetAchievement.createAchievementListVector(builder, lmi);
		int ai = CMDGetAchievement.createCMDGetAchievement(builder, uid, alv, dmlv);
		builder.finish(ai);
		rs = CMDGetAchievement.getRootAsCMDGetAchievement(builder.dataBuffer());
		bKq = ErrorCode.OK;
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		return Arrays.asList(bKq, channels, outparams);
	}
}