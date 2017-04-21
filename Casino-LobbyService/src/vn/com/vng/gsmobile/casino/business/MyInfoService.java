package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;
import vn.com.vng.gsmobile.casino.entries.GameConfig;
import vn.com.vng.gsmobile.casino.entries.Gift;
import vn.com.vng.gsmobile.casino.entries.GiftName;
import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.flatbuffers.GiftState;
import vn.com.vng.gsmobile.casino.flatbuffers.ItemType;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfoDetail;
import vn.com.vng.gsmobile.casino.flatbuffers.VipType;
import vn.com.vng.gsmobile.casino.ulti.DateUtil;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

/**
 * 
 * @author bangbv
 *
 */
public class MyInfoService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		boolean update = false;
		byte bKq = ErrorCode.UNKNOWN;
		UserInfo rq = (UserInfo) params.get(4);
		UserInfo rs = null;
		String avatar = null;
		String name = null;
		Integer vip = null;
		Long vip_expired = null;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		Long uid = rq.uid();
		int ui = 0;
		JsonObject j = LocalCache.get(User.USER_TABLENAME + uid);
		JsonObject j2 = LocalCache.get(User.USERESOURCE_TABLENAME + uid);
		JsonObject dgo = Lib.getCB(Gift.GIFT_TABLENAME + uid);
		if ((j != null) && (j2 != null)) {
			name = j.getString(User.NAME);
			avatar = j.getString(User.AVATAR);
			if (!avatar.equalsIgnoreCase(rq.avatar()) && (rq.avatar() != null) && (rq.avatar().trim().length() > 0)) {
				j.put(User.AVATAR, rq.avatar());
				update = true;
			}
			vip_expired = j.getLong(User.VIP_EXPIRE);
			vip = j.getInt(User.VIP);
			ui = UserInfo.createUserInfo(builder, uid, builder.createString(j.getString(User.NAME)),
					builder.createString(j.getString(User.AVATAR)), j.getInt(User.STATUS), j.getInt(User.ACCTYPE),
					j.getInt(User.VIP), j2.getLong(User.COIN), j2.getInt(User.LEVEL),
					UserInfoDetail.createUserInfoDetail(builder, j2.getInt(User.EXP), j2.getInt(User.WIN),
							j2.getInt(User.LOSE), j2.getInt(User.STAR), j2.getInt(User.CASH),
							User.getVipDaysRemaining(vip_expired)));
			builder.finish(ui);
			rs = UserInfo.getRootAsUserInfo(builder.dataBuffer());
			bKq = ErrorCode.OK;
		}
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		// return result to Client
		Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_REQUEST, channels, outparams);
		// update CB if account is zalo or fb account
		
		if (!name.equalsIgnoreCase(rq.name()) && (rq.name() != null) && (rq.name().trim().length() > 0)) {
			j.put(User.NAME, rq.name());
			update = true;
		}
		if (vip != null && vip > 0 && vip_expired != null && System.currentTimeMillis() >= vip_expired) {
			j.put(User.VIP, VipType.None);
			j.put(User.VIP_EXPIRE, 0);
			update = true;
		}
		if ((bKq == ErrorCode.OK) && update) {
			JsonDocument newJsonDocument = JsonDocument.create(User.USER_TABLENAME + uid, j);
			Lib.getDBGame(false).getCBConnection().upsert(newJsonDocument);
			LocalCache.put(User.USER_TABLENAME + uid, j);
		}

		if (dgo != null) {
			update = false;
			// update day in giftList
			JsonArray gl = dgo.getArray(User.GIFT_LIST);
			// new gift list
			JsonArray ngl = JsonArray.create();
			// temp gift list
			JsonArray tgl = JsonArray.create();
			if (gl != null) {
				for (int i = 0; i < gl.size(); i++) {
					JsonObject g = (JsonObject) gl.get(i);
					String giftName = (String) g.get(Gift.GIFT_NAME);
					if (GiftName.name(GiftName.DAILYGIFT).equalsIgnoreCase(giftName)) {
						ngl.add(g);
					} else {
						tgl.add(g);
					}
				}
				JsonObject ng = (JsonObject) ngl.get(ngl.size() - 1);
				int day = ng.getInt(User.CONSECUTIVE_DAY);
				String date = ng.getString(User.DATE);
				boolean isNextDate = new DateUtil().isNextDate(date);
				boolean isCurrentDate = new DateUtil().isCurrentDate(date);
				if (!isNextDate && !isCurrentDate) {
					JsonObject ndg = JsonObject.create();
					updateDailyGift(ndg, 1);
					ngl = JsonArray.create();
					tgl.add(ndg);
					update = true;
				} else {
					if (isNextDate && (day == 7)) {
						JsonObject ndg = JsonObject.create();
						updateDailyGift(ndg, 1);
						ngl = JsonArray.create();
						ngl.add(ndg);
						update = true;
					}
					if (isNextDate && day < 7) {
						day = day + 1;
						JsonObject ndg = JsonObject.create();
						updateDailyGift(ndg, day);
						ngl.add(ndg);
						update = true;
					}
					for (int i = 0; i < ngl.size(); i++) {
						JsonObject g = (JsonObject) ngl.get(i);
						if (i != (ngl.size() - 1)) {
							int status = g.getInt(Gift.STATUS);
							if (status == (int)GiftState.State_None) {
								g.put(Gift.STATUS, GiftState.State_NotReceived);
								update = true;
							}
							tgl.add(g);
						} else {
							tgl.add(g);
						}
					}
				}
				if (update) {
					dgo.put(Gift.GIFT_LIST, tgl);
					JsonDocument ndgd = JsonDocument.create(Gift.GIFT_TABLENAME + uid, dgo);
					Lib.getDBGame(false).getCBConnection().upsert(ndgd);
				}
			}
		} else if (j != null) {
			JsonObject ngo = JsonObject.create();
			JsonArray gl = JsonArray.create();
			JsonObject igo = JsonObject.create();
			updateDailyGift(igo, 1);
			gl.add(igo);
			ngo.put(Gift.GIFT_LIST, gl);
			JsonDocument nd = JsonDocument.create(Gift.GIFT_TABLENAME + uid, ngo);
			Lib.getDBGame(false).getCBConnection().upsert(nd);
		}

		return Arrays.asList(bKq, null, null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateDailyGift(JsonObject ng, int day) {
		JsonObject gco = LocalCache.get(GameConfig.GAME_TABLENAME + GameConfig.PREFIX);
		Map gc = gco.toMap();
		ArrayList<Map<String, Object>> dg = (ArrayList<Map<String, Object>>) gc.get(GiftName.name(GiftName.DAILYGIFT));
		Map<String, Object> mgv = dg.get(day - 1);
		ng.put(Gift.GIFT_ID, Gift.TYPE_VALUE + String.valueOf(day));
		ng.put(Gift.GIFT_NAME, GiftName.name(GiftName.DAILYGIFT));
		ng.put(Gift.DATE, new DateUtil().getCurrentDate());
		ng.put(Gift.GIFT_TYPE, ItemType.Item_Coin);
		ng.put(Gift.GIFT_VALUE, Long.valueOf((String) mgv.get(Gift.GIFT_VALUE)));
		ng.put(Gift.EXPIRED_TIME, 0);
		ng.put(Gift.CONSECUTIVE_DAY, day);
		ng.put(Gift.STATUS, GiftState.State_None);
	}
}
