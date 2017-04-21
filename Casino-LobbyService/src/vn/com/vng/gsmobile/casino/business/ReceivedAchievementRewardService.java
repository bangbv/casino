package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;
import vn.com.vng.gsmobile.casino.entries.AchievementMission;
import vn.com.vng.gsmobile.casino.entries.CondUpdateType;
import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDReceivedAchievementReward;
import vn.com.vng.gsmobile.casino.flatbuffers.GiftItem;
import vn.com.vng.gsmobile.casino.flatbuffers.ItemType;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;

/**
 * @author bangbv
 */
public class ReceivedAchievementRewardService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDReceivedAchievementReward rq = (CMDReceivedAchievementReward) params.get(4);
		CMDReceivedAchievementReward rs = null;
		Long uid = rq.uid();
		Long idAchievement = rq.id();
		Long level = rq.level();
		int type = rq.type();
		User u = new User(uid);
		List<List<?>> conds = new ArrayList<>();

		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		JsonObject umo = (JsonObject) Lib.getCB(AchievementMission.ACHIEVEMENT_MISSION_TB + uid);
		JsonObject dmdo = (JsonObject) Lib.getCB(AchievementMission.ACHIEVEMENT_MISSION_TB + AchievementMission.PREFIX);
		JsonObject ur = (JsonObject) Lib.getCB(User.USERESOURCE_TABLENAME + uid);
		if ((umo != null) && (dmdo != null) && (ur != null)) {
			JsonObject dmuo = umo.getObject(AchievementMission.MISSION_LIST);
			JsonObject dmwu = dmuo.getObject(idAchievement.toString());
			if (dmwu != null) {
				dmwu.put(AchievementMission.LEVEL, level + 1);
				dmwu.put(AchievementMission.COUNT, 0);
				JsonObject dmdlo = (JsonObject) dmdo.getObject(AchievementMission.MISSION_LIST);
				JsonObject dmwd = dmdlo.getObject(idAchievement.toString());
				long coin = dmwd.getObject(AchievementMission.LEVEL).getObject(level.toString()).getLong(AchievementMission.COIN);
				switch (type) {
				case ItemType.Item_Coin:
					ur.put(User.COIN, ur.getLong(User.COIN) + coin);
					//add condition
					conds.add(Arrays.asList(User.COND_COIN_WIN_TOTAL, coin, CondUpdateType.Increase));
					conds.add(Arrays.asList(User.COND_DAILY_COIN_WIN, coin, CondUpdateType.Increase));
					conds.add(Arrays.asList(User.COND_DAILY_COIN_BALANCE, coin, CondUpdateType.Increase));
					break;
				default:
					break;
				}
				List<Integer> lg = new ArrayList<Integer>();
				int gi = vn.com.vng.gsmobile.casino.flatbuffers.Gift.createGift(builder,type,coin);
				lg.add(gi);
				int[] gli = ArrayUtils.toPrimitive(lg.toArray(new Integer[lg.size()]));
				int gvi = GiftItem.createGiftVector(builder, gli);
				int gii = GiftItem.createGiftItem(builder, 0, gvi, 0, 0, 0);
				int rari = CMDReceivedAchievementReward.createCMDReceivedAchievementReward(builder,
						uid, idAchievement, level, type, gii, 0);
				builder.finish(rari);
				rs = CMDReceivedAchievementReward.getRootAsCMDReceivedAchievementReward(builder.dataBuffer());
				bKq = ErrorCode.OK;
			} else {
				bKq = ErrorCode.UNDEFINE;
			}
		} else {
			bKq = ErrorCode.USER_NOTEXIST;
		}
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		// send response
		Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_REQUEST, channels, outparams);
		// save CB
		if (bKq == ErrorCode.OK) {
			JsonDocument urd = JsonDocument.create(User.USERESOURCE_TABLENAME + uid, ur);
			JsonDocument umd = JsonDocument.create(AchievementMission.ACHIEVEMENT_MISSION_TB + uid, umo);
			Lib.getDBGame(false).getCBConnection().upsert(urd);
			Lib.getDBGame(false).getCBConnection().upsert(umd);
			u.setConditionValue(conds);
		}
		return Arrays.asList(bKq, null, null);
	}
}