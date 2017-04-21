package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfo;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class UserInfoService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		UserInfo rq = (UserInfo) params.get(4);
		User u = new User(rq.uid());
		UserInfo rs = u.toUserInfo();
		if(rs!=null)
			bKq = ErrorCode.OK;
		else
			bKq = ErrorCode.NOTEXISTS;
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		// return result to Client
		return Arrays.asList(bKq, channels, outparams);
	}

}
