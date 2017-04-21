package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import vn.com.vng.gsmobile.casino.entries.Gift;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetGiftListSystem;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

/**
 * @author bangbv
 */
public class SysGiftListService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGetGiftListSystem rq = (CMDGetGiftListSystem) params.get(4);
		CMDGetGiftListSystem rs = Gift.toGiftListSystem(rq.uid());
		if(rs!=null)
			bKq = ErrorCode.OK;
		else
			bKq = ErrorCode.NOTEXISTS;
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		return Arrays.asList(bKq, channels, outparams);
	}
}