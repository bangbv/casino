package vn.com.vng.gsmobile.casino.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.entries.Mail;
import vn.com.vng.gsmobile.casino.entries.SystemMail;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGetMailList;
import vn.com.vng.gsmobile.casino.flatbuffers.MailInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.SimpleProfile;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

/**
 * @author bangbv
 */
public class MailListService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data
		// FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		boolean update = false;
		CMDGetMailList rq = (CMDGetMailList) params.get(4);
		CMDGetMailList rs = null;
		Long uid = rq.uid();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		List<Integer> lm = new ArrayList<>();
		JsonObject mo = Lib.getCB(Mail.MAIL_TABLENAME + uid);
		JsonObject smo = LocalCache.get(SystemMail.SYSTEM_TABLENAME);
		if (mo == null) {
			mo = JsonObject.create();
			JsonObject mlo = JsonObject.create();
			mo.put(Mail.MAIL_LIST, mlo);
		}
		JsonObject mlo = mo.getObject(Mail.MAIL_LIST);
		Map<String, Object> mlm = mlo.toMap();
		JsonObject smlo = null;
		if (smo != null) {
			smlo = smo.getObject(Mail.MAIL_LIST);
			Map<String, Object> smlm = smlo.toMap();
			for (Entry<String, Object> entry : smlm.entrySet()) {
				String mailId = entry.getKey();
				JsonObject om = mlo.getObject(mailId);
				JsonObject sm = smlo.getObject(mailId);
				if (om == null) {
					mlo.put(mailId, sm);
					update = true;
				}
			}
		}
		mlm = mlo.toMap();
		for (Entry<String, Object> entry : mlm.entrySet()) {
			JsonObject m = mlo.getObject(entry.getKey());
			JsonObject mf = m.getObject(Mail.MAIL_FROM);
			String fromName = mf.getString(Mail.FROM_NAME);
			String msg = m.getString(Mail.MAIL_MSG);
			int isRead = 0;
			if (msg.length() > 20) {
				msg = msg.substring(0, 20);
			}
			int sp = SimpleProfile.createSimpleProfile(builder, mf.getLong(Mail.FROM_UID),
					builder.createString(fromName), builder.createString(mf.getString(Mail.FROM_AVATAR)));
			try{
				isRead = m.getInt(Mail.IS_READ);
			}catch (Exception e) {
				e.printStackTrace();
			}
			int mi = MailInfo.createMailInfo(builder, 
					builder.createString(m.getString(Mail.MAIL_ID)), 
					sp, 
					builder.createString(msg), 
					m.getLong(Mail.MAIL_TIME), 
					isRead, 
					m.getInt(Mail.MAIL_TYPE));
			if (m.getInt(Mail.MAIL_TYPE) == 2) {
				if (m.getInt(SystemMail.IS_DELETED) == 0) {
					lm.add(mi);
				}
				if (m.getInt(SystemMail.IS_DELETED) == 1) {
					if (smlo.getObject(entry.getKey()) == null) {
						mlo.removeKey(entry.getKey());
						update = true;
					}
				}
			}
			if (m.getInt(Mail.MAIL_TYPE) == 1) {
				lm.add(mi);
			}
			int[] lmui = ArrayUtils.toPrimitive(lm.toArray(new Integer[lm.size()]));
			int list_mailOffset = CMDGetMailList.createListMailVector(builder, lmui);
			int ml = CMDGetMailList.createCMDGetMailList(builder, uid, list_mailOffset);
			builder.finish(ml);
			rs = CMDGetMailList.getRootAsCMDGetMailList(builder.dataBuffer());
			bKq = ErrorCode.OK;
			List<?> channels = Arrays.asList(params.get(0));
			List<?> outparams = Arrays.asList(params.get(1), params.get(2), params.get(3), bKq, rs);
			Service.sendToClient(this.getClass().getSimpleName(), sTrid, Service.CMDTYPE_REQUEST, channels, outparams);
			if (update) {
				Lib.getDBGame(false).getCBConnection().upsert(JsonDocument.create(Mail.MAIL_TABLENAME + uid, mo));
			}
		}
		return Arrays.asList(bKq, null, null);
	}
}