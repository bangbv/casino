package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

import vn.com.vng.gsmobile.casino.entries.Mail;
import vn.com.vng.gsmobile.casino.entries.User;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDSendMail;
import vn.com.vng.gsmobile.casino.flatbuffers.MailType;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

/**
 * @author bangbv
 */
public class SendMailService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDSendMail rq = (CMDSendMail) params.get(4);
		Long uid = rq.toUid();
		String msg = rq.message();
		Long fuid = rq.from();
		JsonObject ju = LocalCache.get(User.USER_TABLENAME + fuid);
		if (ju != null) {
			JsonObject j = Lib.getCB(Mail.MAIL_TABLENAME + uid);
			JsonObject ja = null;
			if (j != null) {
				ja = j.getObject(Mail.MAIL_LIST);
			} else {
				// create new document
				j = JsonObject.create();
				j.put(Mail.TYPE, Mail.TYPE_VALUE);
				ja = JsonObject.create();
				j.put(Mail.MAIL_LIST, ja);
			}			
			JsonObject nm = JsonObject.create();
			JsonObject f = JsonObject.create();
			f.put(Mail.FROM_UID, fuid);
			f.put(Mail.FROM_NAME, ju.getString(User.NAME));
			f.put(Mail.FROM_AVATAR, ju.getString(User.AVATAR));
			nm.put(Mail.MAIL_ID, String.valueOf(System.currentTimeMillis()));
			nm.put(Mail.MAIL_TYPE, MailType.MailUser);
			nm.put(Mail.MAIL_TIME, System.currentTimeMillis());				
			nm.put(Mail.MAIL_MSG, msg);
			nm.put(Mail.MAIL_FROM, f);
			ja.put(String.valueOf(System.currentTimeMillis()),nm);
			JsonDocument nj = JsonDocument.create(Mail.MAIL_TABLENAME + uid, j);
			Lib.getDBGame(false).getCBConnection().upsert(nj);			
			bKq = ErrorCode.OK;
		} else {
			bKq = ErrorCode.USER_NOTEXIST;
		}		
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, null);
		return Arrays.asList(bKq, channels, outparams);
	}
}