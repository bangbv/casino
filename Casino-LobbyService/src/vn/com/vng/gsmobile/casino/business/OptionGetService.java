package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.entries.UserSetting;
import vn.com.vng.gsmobile.casino.flatbuffers.UserAppSetting;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class OptionGetService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		UserAppSetting rq = (UserAppSetting) params.get(4);
		UserAppSetting rs = null;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		Long uid = rq.uid();
		JsonObject u = LocalCache.get(User.USER_TABLENAME + uid);
		JsonObject us = Lib.getCB(UserSetting.USERSETTING_TABLENAME + uid);
		if (u != null) {
			if (us == null) {
				// create new document for user setting
				us = JsonObject.create();
				us.put(UserSetting.SOUND, 1);
				us.put(UserSetting.MUSIC, 1);
				us.put(UserSetting.VIBRATE, 1);
				us.put(UserSetting.RECEIVED_INVITE, 1);
				JsonDocument nus = JsonDocument.create(UserSetting.USERSETTING_TABLENAME + uid, us);
				Lib.getDBGame(false).getCBConnection().upsert(nus);
			}
			// user setting exists
			int upsi = UserAppSetting.createUserAppSetting(builder, uid,
					us.getInt(UserSetting.SOUND), us.getInt(UserSetting.MUSIC), us.getInt(UserSetting.VIBRATE),
					us.getInt(UserSetting.RECEIVED_INVITE));
			builder.finish(upsi);
			rs = UserAppSetting.getRootAsUserAppSetting(builder.dataBuffer());
			bKq = ErrorCode.OK;
		} else {
			bKq = ErrorCode.USER_NOTEXIST;
		}
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		// return result to Client
		return Arrays.asList(bKq, channels, outparams);
	}
}
