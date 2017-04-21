package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;
import vn.com.vng.gsmobile.casino.entries.CondUpdateType;
import vn.com.vng.gsmobile.casino.entries.Gift;
import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDReceiveGiftCode;
import vn.com.vng.gsmobile.casino.flatbuffers.GiftItem;
import vn.com.vng.gsmobile.casino.flatbuffers.ItemType;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;

/**
 * @author bangbv
 */
public class GiftCodeReceiveService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDReceiveGiftCode rq = (CMDReceiveGiftCode) params.get(4);
		CMDReceiveGiftCode rs = null;
		Long uid = rq.uid();
		String gc = rq.giftCode().toUpperCase();
		User u = new User(uid);
		List<List<?>> conds = new ArrayList<>();

		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		JsonObject j = Lib.getCB(Gift.GIFTCODE_TABLENAME + gc);
		JsonObject ur = Lib.getCB(User.USERESOURCE_TABLENAME + uid);
		if (j != null && ur != null) {
			// do something
			int giftType = j.getInt(Gift.GIFT_TYPE);
			long giftValue = j.getLong(Gift.GIFT_VALUE);
			long et = j.getLong(Gift.EXPIRED_TIME);
			int day = j.getInt(Gift.DAY);
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
			List<Integer> lg = new ArrayList<Integer>();
			int g = vn.com.vng.gsmobile.casino.flatbuffers.Gift.createGift(builder, j.getInt(Gift.GIFT_TYPE),
					j.getLong(Gift.GIFT_VALUE));
			lg.add(g);
			int[] gli = ArrayUtils.toPrimitive(lg.toArray(new Integer[lg.size()]));
			int gvi = GiftItem.createGiftVector(builder, gli);
			int gi = GiftItem.createGiftItem(builder, builder.createString(j.getString(Gift.GIFT_ID)), gvi, day, et, 0);
			int rgc = CMDReceiveGiftCode.createCMDReceiveGiftCode(builder, uid,
					builder.createString(gc), gi);
			builder.finish(rgc);
			rs = CMDReceiveGiftCode.getRootAsCMDReceiveGiftCode(builder.dataBuffer());
			bKq = ErrorCode.OK;
		}

		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_REQUEST, channels, outparams);
		if (bKq == ErrorCode.OK) {
			JsonDocument nj = JsonDocument.create(User.USERESOURCE_TABLENAME + uid, ur);
			Lib.getDBGame(false).getCBConnection().upsert(nj);
			Lib.getDBGame(false).getCBConnection().remove(Gift.GIFTCODE_TABLENAME + gc);
			u.setConditionValue(conds);
		}
		return Arrays.asList(bKq, null, null);
	}
}