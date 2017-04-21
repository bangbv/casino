package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetInfoFromSocial;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfoDetail;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class GetInfoFromSocialService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGetInfoFromSocial rq = (CMDGetInfoFromSocial) params.get(4);		
		List<CMDGetInfoFromSocial> lifs = new ArrayList<>();
		int offset = 0;
		int block = 10;
		int current = 0;
		int length = rq.sidListLength();
		while (current < length) {
			int i = offset;
			FlatBufferBuilder builder = new FlatBufferBuilder(0);
			List<Integer> lsid = new ArrayList<>();
			List<Integer> luid = new ArrayList<>();
			while( (i < block) && (i<length)) {				
				String sid = rq.sidList(i);
				int sidi = builder.createString(sid);				
				JsonObject uso = LocalCache.get(User.USERSOCIAL_TABLENAME + sid);
				if (uso != null) {
					Long uid = uso.getLong(User.UID);
					JsonObject uo = LocalCache.get(User.USER_TABLENAME + uid);
					JsonObject uro = LocalCache.get(User.USERESOURCE_TABLENAME + uid);
					if ((uo != null) && (uro != null)) {
						Long vip_expired = uo.getLong(User.VIP_EXPIRE);
						int ui = UserInfo.createUserInfo(builder, uid, builder.createString(uo.getString(User.NAME)),
								builder.createString(uo.getString(User.AVATAR)), uo.getInt(User.STATUS),
								uo.getInt(User.ACCTYPE), uo.getInt(User.VIP), uro.getLong(User.COIN),
								uro.getInt(User.LEVEL),
								UserInfoDetail.createUserInfoDetail(builder, 
										uro.getInt(User.EXP), 
										uro.getInt(User.WIN),
										uro.getInt(User.LOSE), 
										uro.getInt(User.STAR), 
										uro.getInt(User.CASH),
										User.getVipDaysRemaining(vip_expired)
								)
						);
						luid.add(ui);
						lsid.add(sidi);
					}					
				}
				i++;
				current++;
			}
			offset = block;
			block=current+block;
			int[] asid = ArrayUtils.toPrimitive(lsid.toArray(new Integer[lsid.size()]));
			int[] auid = ArrayUtils.toPrimitive(luid.toArray(new Integer[luid.size()]));
			int slv = CMDGetInfoFromSocial.createSidListVector(builder, asid);
			int ulv = CMDGetInfoFromSocial.createUserListVector(builder, auid);
			int ifs = CMDGetInfoFromSocial.createCMDGetInfoFromSocial(builder, slv, ulv);
			builder.finish(ifs);
			CMDGetInfoFromSocial rs = CMDGetInfoFromSocial.getRootAsCMDGetInfoFromSocial(builder.dataBuffer());
			lifs.add(rs);
		}		
		bKq = ErrorCode.OK;
		for (CMDGetInfoFromSocial gifs : lifs) {
			List<?> channels = Arrays.asList(params.get(0));
			List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, gifs);
			if (gifs.userListLength() > 0) {
				Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_REQUEST, channels,
						outparams);
			}
		}
		return Arrays.asList(bKq, null, null);
	}
}
