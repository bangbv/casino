package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetListSimpleProfile;
import vn.com.vng.gsmobile.casino.flatbuffers.ListSimpleUserInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.SimpleProfile;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class ListSimpeProfileService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGetListSimpleProfile rq = (CMDGetListSimpleProfile) params.get(4);
		String trans = rq.trans();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		ListSimpleUserInfo rs = null;		
		List<Integer> lsp = new ArrayList<>();
		for (int i = 0; i < rq.uidListLength(); i++) {
			Long uid = rq.uidList(i);
			JsonObject uo = LocalCache.get(User.USER_TABLENAME + uid);
			if (uo != null) {
			int spi = SimpleProfile.createSimpleProfile(builder, 
					uid, 
					builder.createString(uo.getString(User.NAME)),
					builder.createString(uo.getString(User.AVATAR))
				);				
				lsp.add(spi);
			}
			bKq = ErrorCode.OK;
		}
		int[] data = ArrayUtils.toPrimitive(lsp.toArray(new Integer[lsp.size()]));
		int lvi = ListSimpleUserInfo.createListVector(builder, data);
		int lsuii = ListSimpleUserInfo.createListSimpleUserInfo(builder, builder.createString(trans), 0, lvi);
		builder.finish(lsuii);
		rs = ListSimpleUserInfo.getRootAsListSimpleUserInfo(builder.dataBuffer());
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		return Arrays.asList(bKq, channels, outparams);
	}
}
