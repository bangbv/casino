package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.RankListService;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDRankList;
import vn.com.vng.gsmobile.casino.flatbuffers.RankType;

public class testRankList {

	public static void main(String[] args) throws Exception {
		RankListService service = new RankListService();
		// TODO Auto-generated method stub
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		Long uid = 47921862302351360l;
		int rl = CMDRankList.createCMDRankList(builder, uid, 0, 0, RankType.RankGlobalLevel, 0, 0);
		builder.finish(rl);
		CMDRankList rq = CMDRankList.getRootAsCMDRankList(builder.dataBuffer());		
		List<?> listResult = service.execute("",  Arrays.asList("","","","",rq));
		System.out.println(listResult.get(2));
	}
}
