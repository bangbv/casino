package vn.com.vng.gsmobile.casino.ulti;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

import vn.com.vng.gsmobile.casino.entries.Mail;
import vn.com.vng.gsmobile.casino.entries.SystemMail;
import vn.com.vng.gsmobile.casino.flatbuffers.MailType;

public class SendMaillAllUser {

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 1; i++) {
			String msg = "Tuổi hồng thơ ngây dưới mái trường Tuổi thơ đã đi qua rồi Để lại trong tôi một nỗi buồn Nói lên tiếng yêu lặng thầm Anh dành cho em Ah hah háh hah hah hàh... Xưa chúng ta chung trường Cùng nhau kết hoa ước hẹn Mà sao bỗng dưng em lại Bỏ quên hoa,quên tình tôi Em vội ra đi trong ly biệt Tháng năm vẫn trôi qua dần Anh chờ tin em ..... Kỷ niệm trong tôi đã phai mờ Giờ em bước đi theo chồng Bỏ lại trong tôi một bóng hình Nói lên tiếng yêu lặng thầm Anh dành cho em Khi biết tin em rồi Lòng anh bỗng se thắt lại Và khi tiếng chuông giáo đường Chợt ngân xe hoa dừng lại Em là cô dâu, khoác áo hồng Sánh vai bước đi bên chồng Tình anh đơn côi Hãy hát khúc nhạc buồn Cùng chung tiếng ca cung đàn Còn đâu dáng em những chiều Nhè nhẹ đưa bước chân phù du Êm đềm trôi qua, khi em theo chồng Nhớ sao những khi tan trường Mình anh bơ vơ";
			JsonObject mo = Lib.getCB(SystemMail.SYSTEM_TABLENAME);
			JsonObject mlo = null;
			if (mo != null) {
				mlo = mo.getObject(Mail.MAIL_LIST);
			} else {
				// create new document
				mo = JsonObject.create();
				mo.put(Mail.TYPE, 27);
				mlo = JsonObject.create();
				mo.put(Mail.MAIL_LIST, mlo);
			}

			Long mid = System.currentTimeMillis();
			JsonObject nm = JsonObject.create();
			JsonObject f = JsonObject.create();
			f.put(Mail.FROM_UID, 47921862302351360l);
			f.put(Mail.FROM_NAME, "Mail System");
			f.put(Mail.FROM_AVATAR, "https://graph.facebook.com/v2.8/1627942547221425/picture?width=540&height=540");
			nm.put(Mail.MAIL_ID, String.valueOf(mid));
			nm.put(Mail.MAIL_TYPE, MailType.MailSystem);
			nm.put(Mail.TYPE, 27);
			nm.put(SystemMail.IS_DELETED, 0);
			nm.put(Mail.IS_READ, 0);
			nm.put(Mail.MAIL_TIME, mid);
			nm.put(Mail.MAIL_MSG, msg);
			nm.put(Mail.MAIL_FROM, f);
			mlo.put(String.valueOf(mid), nm);

			System.out.println(mo);
			JsonDocument nmd = JsonDocument.create(Mail.MAIL_STB, mo);
			Lib.getDBGame(false).getCBConnection().upsert(nmd);
		}
		System.out.println("finish !");
	}
}
