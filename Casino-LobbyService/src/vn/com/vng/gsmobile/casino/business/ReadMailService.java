package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.entries.Mail;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetFullMailMess;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

/**
 * @author bangbv
 */
public class ReadMailService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDGetFullMailMess rq = (CMDGetFullMailMess) params.get(4);
		CMDGetFullMailMess rs = null;
		Long uid = rq.uid();
		String mid = rq.mailId();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		JsonObject mo = LocalCache.get(Mail.MAIL_TABLENAME + uid);
		if (mo != null) {
			JsonObject mlo = mo.getObject(Mail.MAIL_LIST);
			JsonObject m = mlo.getObject(mid);
			try {
				int isRead = m.getInt(Mail.IS_READ);
				if(isRead == 0){
					m.put(Mail.IS_READ, 1);
					// update CB
					JsonDocument nmd = JsonDocument.create(Mail.MAIL_TABLENAME + uid, mo);
					Lib.getDBGame(false).getCBConnection().upsert(nmd);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			int fmm = CMDGetFullMailMess.createCMDGetFullMailMess(builder, uid,
					builder.createString(mid), builder.createString(m.getString(Mail.MAIL_MSG)), 1);
			builder.finish(fmm);
			rs = CMDGetFullMailMess.getRootAsCMDGetFullMailMess(builder.dataBuffer());
			bKq = ErrorCode.OK;
		} else {
			bKq = ErrorCode.UNDEFINE;
		}
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
		return Arrays.asList(bKq, channels, outparams);
	}
}