package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetServerTime;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class GetServerTimeService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		builder.finish(CMDGetServerTime.createCMDGetServerTime(builder, System.currentTimeMillis()));
		CMDGetServerTime rs = CMDGetServerTime.getRootAsCMDGetServerTime(builder.dataBuffer());
		bKq = ErrorCode.OK;
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,rs); //noi dung phan hoi
		return Arrays.asList(bKq, channels, outparams);
	}
}

