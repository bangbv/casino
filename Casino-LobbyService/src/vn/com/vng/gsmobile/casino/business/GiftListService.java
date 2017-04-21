package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;

import vn.com.vng.gsmobile.casino.entries.Gift;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetGiftList;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

/**
 * @author bangbv
 */
public class GiftListService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGetGiftList rq = (CMDGetGiftList) params.get(4);
		CMDGetGiftList rs = null;
		Long uid = rq.uid();
		rs = Gift.giftList(uid);
		if (rs != null) {
			bKq = ErrorCode.OK;
		} else {
			bKq = ErrorCode.UNKNOWN;
		}
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		return Arrays.asList(bKq, channels, outparams);
	}
}