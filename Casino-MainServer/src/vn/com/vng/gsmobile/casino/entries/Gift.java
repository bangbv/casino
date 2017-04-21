package vn.com.vng.gsmobile.casino.entries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;
import vn.com.vng.gsmobile.casino.business.Service;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetGiftList;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetGiftListSystem;
import vn.com.vng.gsmobile.casino.flatbuffers.GiftItem;
import vn.com.vng.gsmobile.casino.flatbuffers.GiftState;
import vn.com.vng.gsmobile.casino.flatbuffers.ItemType;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.DateUtil;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class Gift {
	public static final String GIFT_TABLENAME = "8_";
	public static final String TYPE = "type";
	public static final String TYPE_VALUE = "8";
	public static final String GIFTCODE_TABLENAME = "24_";
	public static final String GIFT_LIST = "giftList";
	public static final String DATE = "date";
	public static final String DAY = "day";
	public static final String GIFT_ID = "giftId";
	public static final String GIFT_NAME = "giftName";
	public static final String GIFT_TYPE = "giftType";
	public static final String GIFT_VALUE = "giftValue";
	public static final String EXPIRED_TIME = "expiredTime";
	public static final String CONSECUTIVE_DAY = "consecutiveDay";
	public static final String STATUS = "status";
	public static final String DESC = "desc";

	public static final String GIFT_DAILY = "daily_gift";
	public static final String GIFT_VIP = "vipDay";
	public static final String GIFT_SYS = "giftSystem";

	public static void sendGiftSystem(Long uid, byte giftType, long giftValue, long expiredTime, String desc) {
		addGiftSystem(uid, giftType, giftValue, expiredTime, desc);
		Service.sendToClient(Gift.class.getSimpleName(), String.format("%d", System.currentTimeMillis()),
				Service.CMDTYPE_REQUEST, Arrays.asList(Handshake.getChannel(uid)), Arrays.asList(CMD.GIFTSYS_LIST.cmd,
						CMD.GIFTSYS_LIST.subcmd, CMD.GIFTSYS_LIST.version, (byte) 0, Gift.toGiftListSystem(uid)));
	}

	public static void addGiftSystem(Long uid, byte giftType, long giftValue, long expiredTime, String desc) {
		JsonObject j = Lib.getCB(Gift.GIFT_TABLENAME + uid);
		if (j != null) {
			JsonArray ja = j.getArray(GIFT_LIST);
			if (ja != null) {
				JsonObject g = JsonObject.create();
				g.put(Gift.GIFT_ID, String.format("S%d", Lib.getNanoTimeId()));
				g.put(Gift.GIFT_NAME, GiftName.name(GiftName.GIFTSYSTEM));
				g.put(Gift.GIFT_TYPE, giftType);
				g.put(Gift.DATE, new DateUtil().getCurrentDate());
				g.put(Gift.GIFT_VALUE, giftValue);
				g.put(Gift.EXPIRED_TIME, expiredTime);
				g.put(Gift.CONSECUTIVE_DAY, 0);
				g.put(Gift.STATUS, 1);
				g.put(Gift.DESC, desc);
				ja.add(g);
				j.put(Gift.GIFT_LIST, ja);
				JsonDocument nd = JsonDocument.create(Gift.GIFT_TABLENAME + uid, j);
				Lib.getDBGame(false).getCBConnection().upsert(nd);
			}
		}
	}

	public static CMDGetGiftListSystem toGiftListSystem(Long uid) {
		CMDGetGiftListSystem rs = null;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		List<Integer> lsgi = new ArrayList<>();
		JsonObject j = Lib.getCB(Gift.GIFT_TABLENAME + uid);
		if (j != null) {
			JsonArray gl = j.getArray(User.GIFT_LIST);
			if (gl != null) {
				for (int i = 0; i < gl.size(); i++) {
					JsonObject g = (JsonObject) gl.get(i);
					int status = g.getInt(Gift.STATUS);
					String giftName = g.getString(Gift.GIFT_NAME);
					if (status == 1 && Gift.GIFT_SYS.equalsIgnoreCase(giftName)) {
						String giftId = "" + g.get(Gift.GIFT_ID);
						String gifDesc = g.getString(Gift.DESC);
						List<Integer> lg = new ArrayList<Integer>();
						int gi = vn.com.vng.gsmobile.casino.flatbuffers.Gift.createGift(builder,
								g.getInt(Gift.GIFT_TYPE), g.getLong(Gift.GIFT_VALUE));
						lg.add(gi);
						int[] gli = ArrayUtils.toPrimitive(lg.toArray(new Integer[lg.size()]));
						int gvi = GiftItem.createGiftVector(builder, gli);
						int gii = GiftItem.createGiftItem(builder, builder.createString(giftId), gvi,
								g.getInt(Gift.CONSECUTIVE_DAY), g.getLong(Gift.EXPIRED_TIME),
								gifDesc == null ? 0 : builder.createString(gifDesc));
						lsgi.add(gii);
					}
				}
				int[] lsgii = ArrayUtils.toPrimitive(lsgi.toArray(new Integer[lsgi.size()]));
				int igl = CMDGetGiftListSystem.createCMDGetGiftListSystem(builder, uid,
						CMDGetGiftList.createGiftSystemVector(builder, lsgii));
				builder.finish(igl);
				rs = CMDGetGiftListSystem.getRootAsCMDGetGiftListSystem(builder.dataBuffer());
			}
		}
		return rs;
	}

	public static CMDGetGiftList giftList(Long uid) throws Exception {
		CMDGetGiftList rs = null;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		List<Integer> ldgi = new ArrayList<>();
		List<Integer> lsgi = new ArrayList<>();
		List<Integer> lvgi = new ArrayList<>();
		byte[] gsa = new byte[7];
		int gift_daily_day = 0;
		int gift_vip_day = 0;
		JsonObject go = Lib.getCB(Gift.GIFT_TABLENAME + uid);
		JsonObject vcfg = LocalCache.get(Const.VIP_ID);
		JsonObject uv = Lib.getCB(Vip.VIP_TABLENAME + uid);
		JsonObject ui = LocalCache.get(User.USER_TABLENAME + uid);
		if (vcfg != null && ui != null) {
			Integer vip = ui.getInt(User.VIP);
			// convert to date
			if (vip >= 1) {
				JsonObject vl = vcfg.getObject(Vip.VIP_LIST);
				JsonObject v = vl.getObject(vip.toString());
				if (v != null) {
					Long value_daily = v.getLong(Vip.VALUE_DAILY);
					int days = v.getInt(Vip.DAYS);
					Long ve = ui.getLong(User.VIP_EXPIRE);

					String currentDate = new DateUtil().getCurrentDate();
					String vipExpire = new DateUtil().convertDate(ve);
					String buyVipDate = new DateUtil().backNDate(days, vipExpire);
					Long bidl = new DateUtil().backNDateL(days, vipExpire);
					gift_vip_day = Lib.ConvertLongToDay(new Date(bidl), System.currentTimeMillis());
					List<Integer> lg = new ArrayList<Integer>();
					int gi = vn.com.vng.gsmobile.casino.flatbuffers.Gift.createGift(builder, ItemType.Item_Coin,
							value_daily);
					lg.add(gi);
					int[] gli = ArrayUtils.toPrimitive(lg.toArray(new Integer[lg.size()]));
					int gvi = GiftItem.createGiftVector(builder, gli);
					int gii = GiftItem.createGiftItem(builder, builder.createString(vip.toString()), gvi, 0, 0, 0);
					if (uv == null) {
						uv = JsonObject.create();
						uv.put(Vip.DATE, currentDate);
						uv.put(Vip.EXPIRED_TIME, vipExpire);
						uv.put(Vip.STATUS, 1);
						uv.put(Vip.VALUE_DAILY, value_daily);
						JsonDocument nuvd = JsonDocument.create(Vip.VIP_TABLENAME + uid, uv);
						Lib.getDBGame(false).getCBConnection().upsert(nuvd);
						if (!new DateUtil().getCurrentDate().equalsIgnoreCase(buyVipDate)) {
							lvgi.add(gii);
						}
					} else {
						String expireDate = uv.getString(Vip.EXPIRED_TIME);
						String date = uv.getString(Vip.DATE);
						Integer status = 1;
						try {
							status = uv.getInt(Vip.STATUS);
						} catch (Exception e) {
							// TODO: handle exception
							status = 1;
						}
						if (new DateUtil().compare(currentDate, expireDate)) {
							Lib.getDBGame(false).getCBConnection().remove(Vip.VIP_TABLENAME + uid);
						} else if (!date.equalsIgnoreCase(currentDate)) {
							uv.put(Vip.DATE, currentDate);
							uv.put(Vip.STATUS, 1);
							// add to giftList
							lvgi.add(gii);
							JsonDocument nuvd = JsonDocument.create(Vip.VIP_TABLENAME + uid, uv);
							Lib.getDBGame(false).getCBConnection().upsert(nuvd);
						} else if (date.equalsIgnoreCase(currentDate) && (status == 1)
								&& (!buyVipDate.equalsIgnoreCase(currentDate))) {
							lvgi.add(gii);
						}
					}
				}
			}
		}

		if (go != null) {
			JsonArray gl = go.getArray(User.GIFT_LIST);
			if (gl != null) {
				for (int i = 0; i < gl.size(); i++) {
					JsonObject g = (JsonObject) gl.get(i);
					String giftName = g.getString(Gift.GIFT_NAME);
					String giftId = g.getString(Gift.GIFT_ID);
					String gifDesc = g.getString(Gift.DESC);
					List<Integer> lg = new ArrayList<Integer>();
					int gi = vn.com.vng.gsmobile.casino.flatbuffers.Gift.createGift(builder, g.getInt(Gift.GIFT_TYPE),
							g.getLong(Gift.GIFT_VALUE));
					lg.add(gi);
					int[] gli = ArrayUtils.toPrimitive(lg.toArray(new Integer[lg.size()]));
					int gvi = GiftItem.createGiftVector(builder, gli);
					int gii = GiftItem.createGiftItem(builder, builder.createString(giftId), gvi,
							g.getInt(Gift.CONSECUTIVE_DAY), g.getLong(Gift.EXPIRED_TIME), gifDesc==null?0:builder.createString(gifDesc));
					int status = g.getInt(Gift.STATUS);
					gsa[i] = (byte) status;
					gift_daily_day = g.getInt(Gift.CONSECUTIVE_DAY);
					if(Gift.GIFT_DAILY.equalsIgnoreCase(giftName)) {						
						if(status == (int)GiftState.State_None){
							ldgi.add(gii);
						}
					}
				}				
			}
		}
		int[] ldgii = ArrayUtils.toPrimitive(ldgi.toArray(new Integer[ldgi.size()]));
		int[] lsgii = ArrayUtils.toPrimitive(lsgi.toArray(new Integer[lsgi.size()]));
		int[] lvgii = ArrayUtils.toPrimitive(lvgi.toArray(new Integer[lvgi.size()]));
		int gdov = CMDGetGiftList.createGiftDailyVector(builder, ldgii);
		int gvov = CMDGetGiftList.createGiftVipVector(builder, lvgii);
		int gsov = CMDGetGiftList.createGiftSystemVector(builder, lsgii);
		int lgsv = CMDGetGiftList.createGiftDailyStateVector(builder, gsa);
		int gl = CMDGetGiftList.createCMDGetGiftList(builder, uid, gdov, gift_daily_day, lgsv, gvov, gift_vip_day, gsov);
		builder.finish(gl);
		rs = CMDGetGiftList.getRootAsCMDGetGiftList(builder.dataBuffer());
		return rs;
	}
}
