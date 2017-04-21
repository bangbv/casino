package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.entries.Lottery;
import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetLotteryList;
import vn.com.vng.gsmobile.casino.flatbuffers.LotteryItemInfo;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

/**
 * @author bangbv
 */
public class LotteryListService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGetLotteryList rq = (CMDGetLotteryList) params.get(4);
		CMDGetLotteryList rs = null;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		JsonObject lo = (JsonObject) LocalCache.get(Lottery.LOTTERY_TABLENAME);
		if(lo == null){
			List<?> channels = Arrays.asList(params.get(0));
			List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
			return Arrays.asList(bKq, channels, outparams);
		}
		
		long userId = rq.uid();
		User user = new User(userId);
		int totalStar = Lottery.caculateStarPrice(lo, user);
		int totalCoin = Lottery.caculateCoinPrice(lo, user);
		
		List<Integer> llii = new ArrayList<>();
		JsonArray lia = lo.getArray(Lottery.LISTITEMS);
		for (int i = 0; i < lia.size(); i++) {
			JsonObject io = (JsonObject) lia.get(i);
			Number n = io.getLong(Lottery.ITEMID);
			int lii = LotteryItemInfo.createLotteryItemInfo(builder,
					builder.createString(io.getString(Lottery.ITEMNAME)), n.intValue(),
					io.getLong(Lottery.COUNT));
			llii.add(lii);
		}
		
		int[] lliii = ArrayUtils.toPrimitive(llii.toArray(new Integer[llii.size()]));
		int llvi = CMDGetLotteryList.createLotteryListVector(builder, lliii);
//		int lli = CMDGetLotteryList.createCMDGetLotteryList(builder, llvi, price, rq.uid());
		int lli = CMDGetLotteryList.createCMDGetLotteryList(builder, llvi, totalStar, totalCoin, userId);
		builder.finish(lli);
		rs = CMDGetLotteryList.getRootAsCMDGetLotteryList(builder.dataBuffer());
		bKq = ErrorCode.OK;
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		return Arrays.asList(bKq, channels, outparams);
	}
}