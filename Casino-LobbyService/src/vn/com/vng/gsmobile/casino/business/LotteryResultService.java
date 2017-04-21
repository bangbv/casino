package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.entries.CondUpdateType;
import vn.com.vng.gsmobile.casino.entries.Lottery;
import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetLotteryResult;
import vn.com.vng.gsmobile.casino.flatbuffers.LotteryItemInfo;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

/**
 * @author bangbv
 */
public class LotteryResultService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGetLotteryResult rq = (CMDGetLotteryResult) params.get(4);
		CMDGetLotteryResult rs = null;
		Long uid = rq.uid();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		JsonObject lo = (JsonObject) LocalCache.get(Lottery.LOTTERY_TABLENAME);
		JsonObject uro = (JsonObject) LocalCache.get(User.USERESOURCE_TABLENAME + uid);
		if (lo == null) {
			List<?> channels = Arrays.asList(params.get(0));
			List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
			return Arrays.asList(bKq, channels, outparams);
		}
		User user = new User(uid);
		int totalCoin = Lottery.caculateCoinPrice(lo, user);
		if(uro.getLong(User.COIN) < totalCoin){
			bKq = ErrorCode.MONEY_NOTENOUGH;
			List<?> channels = Arrays.asList(params.get(0));
			List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
			return Arrays.asList(bKq, channels, outparams);
		}
		
		//update lottery count daily
		List<List<?>> conds = new ArrayList<>();
		conds.add(Arrays.asList(User.COND_LOTTERY_CNT, 1, CondUpdateType.Increase));
		conds.add(Arrays.asList(User.COND_DAILY_LOTTERY_CNT, 1, CondUpdateType.Increase));
		user.setConditionValue(conds);
		
		JsonArray lia = lo.getArray(Lottery.LISTITEMS);
		int randomNum = ThreadLocalRandom.current().nextInt(0, lia.size());
		JsonObject io = (JsonObject) lia.get(randomNum);
		long coin = io.getLong(Lottery.COUNT) + uro.getLong(User.COIN) - totalCoin;
		Number n = io.getLong(Lottery.ITEMID);
		int lii = LotteryItemInfo.createLotteryItemInfo(builder,
				builder.createString(io.getString(Lottery.ITEMNAME)), n.intValue(),
				io.getLong(Lottery.COUNT));
		
		//next turn's price
		totalCoin = Lottery.caculateCoinPrice(lo, user);
		
		int lri = CMDGetLotteryResult.createCMDGetLotteryResult(builder, uid, lii, 0, totalCoin);
		builder.finish(lri);
		rs = CMDGetLotteryResult.getRootAsCMDGetLotteryResult(builder.dataBuffer());
		uro.put(User.COIN, coin);
		JsonDocument nurd = JsonDocument.create(User.USERESOURCE_TABLENAME + uid, uro);
		Lib.getDBGame(false).getCBConnection().upsert(nurd);
		LocalCache.put(User.USERESOURCE_TABLENAME + uid, uro);
		
		bKq = ErrorCode.OK;
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		return Arrays.asList(bKq, channels, outparams);
	}
}