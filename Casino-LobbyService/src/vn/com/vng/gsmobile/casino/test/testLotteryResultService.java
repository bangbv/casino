package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.LotteryResultService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetLotteryResult;

public class testLotteryResultService {

	public static void main(String[] args) throws Exception {
		LotteryResultService service = new LotteryResultService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
//		int lli = CMDGetLotteryList.createCMDGetLotteryList(builder, 0, 0, 0, 44534008188600320l);
//		builder.finish(lli);
//		CMDGetLotteryList rq = CMDGetLotteryList.getRootAsCMDGetLotteryList(builder.dataBuffer());
		
		int lli = CMDGetLotteryResult.createCMDGetLotteryResult(builder, 44534008188600320l, 0, 0, 0);
		builder.finish(lli);
		CMDGetLotteryResult rq = CMDGetLotteryResult.getRootAsCMDGetLotteryResult(builder.dataBuffer());
		
		List<?> rs = service.execute("", Arrays.asList("", "", "", "", rq));
		System.out.println(rs);
	}
}
