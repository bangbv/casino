package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

import vn.com.vng.gsmobile.casino.entries.Mail;
import vn.com.vng.gsmobile.casino.entries.SystemMail;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDDeleteMail;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

/**
 * @author bangbv
 */
public class DeleteMailService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDDeleteMail rq = (CMDDeleteMail) params.get(4);
		CMDDeleteMail rs = null;
		Long uid = rq.uid();
		String mid = rq.id();
		int mt = rq.mailType();
		JsonObject mo = LocalCache.get(Mail.MAIL_TABLENAME + uid);
		if (mo != null) {
			JsonObject mlo = mo.getObject(Mail.MAIL_LIST);
			if (mt == 1) {
				mlo.removeKey(mid);
			}
			if(mt == 2){
				JsonObject sm = mlo.getObject(mid);
				sm.put(SystemMail.IS_DELETED, 1);
			}
			// update
			JsonDocument nj = JsonDocument.create(Mail.MAIL_TABLENAME + uid, mo);
			Lib.getDBGame(false).getCBConnection().upsert(nj);
			bKq = ErrorCode.OK;
		} else {
			bKq = ErrorCode.UNDEFINE;
		}
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		return Arrays.asList(bKq, channels, outparams);
	}
}