package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;
import vn.com.vng.gsmobile.casino.entries.CMD;
import vn.com.vng.gsmobile.casino.entries.CondUpdateType;
import vn.com.vng.gsmobile.casino.entries.Gift;
import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.entries.Vip;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDReceiveGift;
import vn.com.vng.gsmobile.casino.flatbuffers.GiftItem;
import vn.com.vng.gsmobile.casino.flatbuffers.GiftSource;
import vn.com.vng.gsmobile.casino.flatbuffers.ItemType;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfoDetail;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

/**
 * @author bangbv
 */
public class GiftReceiveService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDReceiveGift rq = (CMDReceiveGift) params.get(4);
		CMDReceiveGift rs = null;
		Long uid = rq.uid();
		String gid = rq.giftId();
		int gs = rq.giftSource();
		User u = new User(uid);
		List<List<?>> conds = new ArrayList<>();
		JsonObject ui = LocalCache.get(User.USER_TABLENAME + uid);
		JsonObject ur = Lib.getCB(User.USERESOURCE_TABLENAME + uid);
		switch (gs) {
		case GiftSource.From_VipGifts:
			FlatBufferBuilder builderV = new FlatBufferBuilder(0);
			JsonObject uv = Lib.getCB(Vip.VIP_TABLENAME + uid);
			if (uv != null) {
				Long value_daily = uv.getLong(Vip.VALUE_DAILY);
				long coin = ur.getLong(User.COIN) + value_daily;
				ur.put(User.COIN, coin);
				uv.put(Vip.STATUS, 0);
				JsonDocument nuvd = JsonDocument.create(Vip.VIP_TABLENAME + uid, uv);
				Lib.getDBGame(false).getCBConnection().upsert(nuvd);
				bKq = ErrorCode.OK;
				// build response
				List<Integer> lgv = new ArrayList<Integer>();
				int giv = vn.com.vng.gsmobile.casino.flatbuffers.Gift.createGift(builderV, ItemType.Item_Coin, value_daily);
				lgv.add(giv);
				int[] gliv = ArrayUtils.toPrimitive(lgv.toArray(new Integer[lgv.size()]));
				int gviv = GiftItem.createGiftVector(builderV, gliv);
				int giftOffsetv = GiftItem.createGiftItem(builderV, builderV.createString(gid), gviv, 0, 0, 0);
				int rgv = CMDReceiveGift.createCMDReceiveGift(builderV, uid, builderV.createString(gid), gs, giftOffsetv);
				builderV.finish(rgv);
				rs = CMDReceiveGift.getRootAsCMDReceiveGift(builderV.dataBuffer());
				//add condition
				conds.add(Arrays.asList(User.COND_COIN_WIN_TOTAL, value_daily, CondUpdateType.Increase));
				conds.add(Arrays.asList(User.COND_DAILY_COIN_WIN, value_daily, CondUpdateType.Increase));
				conds.add(Arrays.asList(User.COND_DAILY_COIN_BALANCE, value_daily, CondUpdateType.Increase));
			}
			break;
		case GiftSource.From_DailyGifts:
		case GiftSource.From_SystemGifts:
			FlatBufferBuilder builder = new FlatBufferBuilder(0);
			JsonObject go = Lib.getCB(Gift.GIFT_TABLENAME + uid);
			if (go != null && ur != null) {
				JsonArray gl = go.getArray(User.GIFT_LIST);
				JsonArray ngl = JsonArray.create();
				if (gl != null) {
					for (int i = 0; i < gl.size(); i++) {
						JsonObject g = (JsonObject) gl.get(i);
						String giftId = g.getString(Gift.GIFT_ID);
						String giftName = g.getString(Gift.GIFT_NAME);
						String gifDesc = g.getString(Gift.DESC);
						int status = g.getInt(Gift.STATUS);
						if (status == 1) {
							if (gid.equalsIgnoreCase(giftId)) {
								// add money to user
								int giftType = g.getInt(Gift.GIFT_TYPE);
								long giftValue = g.getLong(Gift.GIFT_VALUE);
								long et = g.getLong(Gift.EXPIRED_TIME);
								switch (giftType) {
								case ItemType.Item_Coin:
									long coin = ur.getLong(User.COIN) + giftValue;
									ur.put(User.COIN, coin);
									//add condition
									conds.add(Arrays.asList(User.COND_COIN_WIN_TOTAL, giftValue, CondUpdateType.Increase));
									conds.add(Arrays.asList(User.COND_DAILY_COIN_WIN, giftValue, CondUpdateType.Increase));
									conds.add(Arrays.asList(User.COND_DAILY_COIN_BALANCE, giftValue, CondUpdateType.Increase));
									break;
								case ItemType.Item_Star:
									long star = ur.getLong(User.STAR) + giftValue;
									ur.put(User.STAR, star);
									break;
								case ItemType.Item_EXP:
									long exp = ur.getLong(User.EXP) + giftValue;
									ur.put(User.EXP, exp);
									break;
								case ItemType.Item_VIP:
									// nhận vip
									break;
								default:
									break;
								}
								// build response
								List<Integer> lg = new ArrayList<Integer>();
								int gi = vn.com.vng.gsmobile.casino.flatbuffers.Gift.createGift(builder,
										g.getInt(Gift.GIFT_TYPE), g.getLong(Gift.GIFT_VALUE));
								lg.add(gi);
								int[] gli = ArrayUtils.toPrimitive(lg.toArray(new Integer[lg.size()]));
								int gvi = GiftItem.createGiftVector(builder, gli);
								int giftOffset = GiftItem.createGiftItem(builder,
										builder.createString(g.getString(Gift.GIFT_ID)), gvi,
										g.getInt(Gift.CONSECUTIVE_DAY), et,
										gifDesc == null ? 0 : builder.createString(gifDesc));
								int rg = CMDReceiveGift.createCMDReceiveGift(builder, uid, builder.createString(gid),
										gs, giftOffset);
								builder.finish(rg);
								rs = CMDReceiveGift.getRootAsCMDReceiveGift(builder.dataBuffer());
								// update status
								g.put(Gift.STATUS, 0);
								if (Gift.GIFT_DAILY.equalsIgnoreCase(giftName)) {
									ngl.add(g);
								}
								bKq = ErrorCode.OK;
							} else {
								ngl.add(g);
							}
						} else if (Gift.GIFT_DAILY.equalsIgnoreCase(giftName)) {
							ngl.add(g);
						}
					}
					go.put(Gift.GIFT_LIST, ngl);
					JsonDocument nj = JsonDocument.create(Gift.GIFT_TABLENAME + uid, go);
					Lib.getDBGame(false).getCBConnection().upsert(nj);
				} else {
					bKq = ErrorCode.UNDEFINE;
				}
			}
			break;
		default:
			break;
		}

		// send response
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_REQUEST, channels, outparams);
		// update user information
		if (ui != null && ur != null && bKq == ErrorCode.OK) {
			Long vip_expired = ui.getLong(User.VIP_EXPIRE);
			FlatBufferBuilder bd = new FlatBufferBuilder(0);
			int uii = UserInfo.createUserInfo(bd, uid, bd.createString(ui.getString(User.NAME)),
					bd.createString(ui.getString(User.AVATAR)), ui.getInt(User.STATUS), ui.getInt(User.ACCTYPE),
					ui.getInt(User.VIP), ur.getLong(User.COIN), ur.getInt(User.LEVEL),
					UserInfoDetail.createUserInfoDetail(bd, ur.getInt(User.EXP), ur.getInt(User.WIN),
							ur.getInt(User.LOSE), ur.getInt(User.STAR), ur.getInt(User.CASH),
							User.getVipDaysRemaining(vip_expired)));
			bd.finish(uii);
			UserInfo uro = UserInfo.getRootAsUserInfo(bd.dataBuffer());
			outparams = Arrays.asList(CMD.MY_INFO.cmd, CMD.MY_INFO.subcmd, CMD.MY_INFO.version, bKq, uro);
			Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_REQUEST, channels, outparams);
		}
		// update CB
		if (bKq == ErrorCode.OK) {
			JsonDocument nur = JsonDocument.create(User.USERESOURCE_TABLENAME + uid, ur);
			Lib.getDBGame(false).getCBConnection().upsert(nur);
			LocalCache.put(User.USERESOURCE_TABLENAME + uid, ur);
			u.setConditionValue(conds);
		}
		// save log
		return Arrays.asList(bKq, null, null);
	}
}