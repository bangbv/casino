package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.LotteryListService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetLotteryList;

public class testLotteryListService {

	public static void main(String[] args) throws Exception {
		LotteryListService service = new LotteryListService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		int lli = CMDGetLotteryList.createCMDGetLotteryList(builder, 0, 0, 0, 44534008188600320l);
		builder.finish(lli);
		CMDGetLotteryList rq = CMDGetLotteryList.getRootAsCMDGetLotteryList(builder.dataBuffer());
		List<?> rs = service.execute("", Arrays.asList("", "", "", "", rq));
		System.out.println(rs.get(2));
	}
}
