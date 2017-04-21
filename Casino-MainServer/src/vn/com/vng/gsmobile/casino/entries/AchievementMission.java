package vn.com.vng.gsmobile.casino.entries;

import java.util.List;
import java.util.Map;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.flatbuffers.GameType;
import vn.com.vng.gsmobile.casino.flatbuffers.UserAchievement;
import vn.com.vng.gsmobile.casino.ulti.DateUtil;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class AchievementMission {

	public static final String ACHIEVEMENT_MISSION_TB = "12_";
	public static final String PREFIX = "20170210";
	public static final String MISSION_LIST = "daily_mission";
	public static final String ACHIEVEMENT_LIST = "achievement";
	public static final String NAME = "name";
	public static final String ID = "id";
	public static final String LEVEL = "level";
	public static final String COUNT = "count";
	public static final String TYPE = "type";
	public static final String COIN = "coin";
	public static final String STAR = "star";
	public static final String DATE = "date";
	public static final String STATUS = "status";

	private static boolean update;
	
	public static void getlistMissionAchievement(Long uid, FlatBufferBuilder builder, List<Integer> lum) {
		update = false;
		JsonObject amo = LocalCache.get(AchievementMission.ACHIEVEMENT_MISSION_TB + AchievementMission.PREFIX);
		JsonObject mdlo = amo.getObject(AchievementMission.MISSION_LIST);
		JsonObject uamo = Lib.getCB(AchievementMission.ACHIEVEMENT_MISSION_TB + uid);
		if (uamo == null) {
			uamo = createAchievementMission(uamo, uid);
		}
		User u = new User(uid);
		if (mdlo != null) {
			Map<String, Object> dmlm = mdlo.toMap();
			for (Map.Entry<String, Object> mentry : dmlm.entrySet()) {
				String key = mentry.getKey();
				JsonObject umo = uamo.getObject(AchievementMission.MISSION_LIST);
				JsonObject udmo = umo.getObject(key);
				if (udmo != null) {
					String date = udmo.getString(AchievementMission.DATE);
					if (!(new DateUtil().isCurrentDate(date))) {
						uamo = checkCondition(key, mdlo, uid, builder, lum, uamo, u, true);
					}
				} else {
					uamo = checkCondition(key, mdlo, uid, builder, lum, uamo, u, false);
				}
			}
		}
		if (update) {
			Lib.getDBGame(false).getCBConnection()
					.upsert(JsonDocument.create(AchievementMission.ACHIEVEMENT_MISSION_TB + uid, uamo));
			LocalCache.put(AchievementMission.ACHIEVEMENT_MISSION_TB + uid, uamo);
		}
	}

	public static JsonObject checkCondition(String key, JsonObject mdlo, Long uid, FlatBufferBuilder builder,
			List<Integer> lum, JsonObject uamo, User u, boolean isExist) {
		Integer id = Integer.valueOf(key);
		JsonObject mo = mdlo.getObject(key);
		int count = mo.getInt(AchievementMission.COUNT);
		long coin = mo.getLong(AchievementMission.COIN);
		int star = mo.getInt(AchievementMission.STAR);
		switch (id) {
		case MissionType.GAME_TLMN:
			int p_tlmn = u.getConditionValue(CondType.PlayCountDaily, GameType.TLMN).intValue();
			if (p_tlmn > count) {
				if (isExist) {
					uamo = updateAchievementMission(uid, builder, lum, uamo, 1, id, count, coin, star);
					update = true;
				} else {
					uamo = addAchievementMission(uid, builder, lum, uamo, 1, id, count, coin, star);					
				}
				update = true;
			}
			break;
		case MissionType.WIN_TLMN:
			int w_tlmn = u.getConditionValue(CondType.WinCountDaily, GameType.TLMN).intValue();
			if (w_tlmn > count) {
				uamo = addAchievementMission(uid, builder, lum, uamo, 1, id, count, coin, star);
				update = true;
			}
			break;
		case MissionType.GAME_BL:
			break;
		case MissionType.WIN_BL:
			break;
		default:
			break;
		}
		return uamo;
	}

	public static JsonObject createAchievementMission(JsonObject umo, Long uid) {
		umo = JsonObject.create();
		JsonObject al = JsonObject.create();
		umo.put(AchievementMission.ACHIEVEMENT_LIST, al);
		JsonObject ml = JsonObject.create();
		umo.put(AchievementMission.MISSION_LIST, ml);
		Lib.getDBGame(false).getCBConnection()
				.upsert(JsonDocument.create(AchievementMission.ACHIEVEMENT_MISSION_TB + uid, umo));
		LocalCache.put(AchievementMission.ACHIEVEMENT_MISSION_TB + uid, umo);
		return umo;
	}

	public static JsonObject addAchievementMission(Long uid, FlatBufferBuilder builder, List<Integer> lum,
			JsonObject uamo, int type, Integer id, int count, Long coin, int star) {
		switch (type) {
		case 1:
			JsonObject mlo = uamo.getObject(AchievementMission.MISSION_LIST);
			JsonObject mo = JsonObject.create();
			mo.put(AchievementMission.COUNT, count);
			mo.put(AchievementMission.COIN, coin);
			mo.put(AchievementMission.STAR, star);
			mo.put(AchievementMission.DATE, new DateUtil().getCurrentDate());
			mo.put(AchievementMission.STATUS, AchievementMissionState.State_None);
			mlo.put(id.toString(), mo);

			int uai = UserAchievement.createUserAchievement(builder, id, 0, count);
			lum.add(uai);
			break;
		case 2:
			break;
		default:
			break;
		}
		return uamo;
	}

	public static JsonObject updateAchievementMission(Long uid, FlatBufferBuilder builder, List<Integer> lum,
			JsonObject uamo, int type, Integer id, int count, Long coin, int star) {
		switch (type) {
		case 1:
			JsonObject mlo = uamo.getObject(AchievementMission.MISSION_LIST);
			JsonObject mo = mlo.getObject(id.toString());
			mo.put(AchievementMission.COUNT, count);
			mo.put(AchievementMission.COIN, coin);
			mo.put(AchievementMission.STAR, star);
			mo.put(AchievementMission.DATE, new DateUtil().getCurrentDate());
			mo.put(AchievementMission.STATUS, AchievementMissionState.State_None);
			int uai = UserAchievement.createUserAchievement(builder, id, 0, count);
			lum.add(uai);
			break;
		case 2:
			break;
		default:
			break;
		}
		return uamo;
	}
}
