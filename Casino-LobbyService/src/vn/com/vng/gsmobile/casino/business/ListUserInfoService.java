package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;
import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.flatbuffers.ListUID;
import vn.com.vng.gsmobile.casino.flatbuffers.ListUserInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfoDetail;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class ListUserInfoService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		ListUID rq = (ListUID) params.get(4);
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		ListUserInfo rs = null;
		List<Integer> l = new ArrayList<>();
		for (int i = 0; i < rq.listLength(); i++) {
			Long uid = rq.list(i);
			JsonObject j = (JsonObject) LocalCache.get(User.USER_TABLENAME + uid);
			JsonObject j2 = (JsonObject) LocalCache.get(User.USERESOURCE_TABLENAME + uid);
			if ((j != null) && (j2 != null)) {
				Long vip_expired = j.getLong(User.VIP_EXPIRE);
				int uiBuffer = UserInfo.createUserInfo(builder, 
						uid, 
						builder.createString(j.getString(User.NAME)), 
						builder.createString(j.getString(User.AVATAR)), 
						j.getInt(User.STATUS), 
						j.getInt(User.ACCTYPE),
						j.getInt(User.VIP), 
						j2.getLong(User.COIN), 
						j2.getInt(User.LEVEL), 
						UserInfoDetail.createUserInfoDetail(builder, 
								j2.getInt(User.EXP), 
								j2.getInt(User.WIN),
								j2.getInt(User.LOSE),
								j2.getInt(User.STAR),
								j2.getInt(User.CASH),
								User.getVipDaysRemaining(vip_expired)
						));
				l.add(uiBuffer);
			}
			bKq = ErrorCode.OK;
		}
		int iul = ListUserInfo.createListUserInfo(builder, builder.createString(rq.trans()), 0,
				ListUserInfo.createListVector(builder, ArrayUtils.toPrimitive(l.toArray(new Integer[l.size()]))));
		builder.finish(iul);
		rs = ListUserInfo.getRootAsListUserInfo(builder.dataBuffer());
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		return Arrays.asList(bKq, channels, outparams);
	}
}
