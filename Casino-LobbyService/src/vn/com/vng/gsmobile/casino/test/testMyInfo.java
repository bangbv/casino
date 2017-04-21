package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;
import java.util.List;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.MyInfoService;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfo;

public class testMyInfo {

	public static void main(String[] args) throws Exception {
		MyInfoService service = new MyInfoService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		
		long offsetUid = 43586947498737664l;
		int nameOffset = builder.createString("Quan Nguyen");
		int avatarOffset = builder.createString("https://s240.avatar.talk.zdn.vn/b/6/3/8/11/240/da5bcddc87d6da49279d717c98ac8a3f.jpg");
		int status = builder.createString("");
		int acc_type = builder.createString("");
		int vip = builder.createString("");
		int coin = builder.createString("");
		int level = builder.createString("");
		int detailOffset = builder.createString("");
		
		int ui = UserInfo.createUserInfo(builder, offsetUid, nameOffset, avatarOffset, status, acc_type, vip, coin, level, detailOffset);
		builder.finish(ui);
		
		UserInfo obj = UserInfo.getRootAsUserInfo(builder.dataBuffer());
		List<?> rs = service.execute("",  Arrays.asList("","","","",obj));
		System.out.println(rs.get(2));
	}
}
