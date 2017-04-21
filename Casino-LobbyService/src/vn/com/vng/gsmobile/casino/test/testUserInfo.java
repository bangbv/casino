package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.business.UserInfoService;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfo;

public class testUserInfo {

	public static void main(String[] args) throws Exception {
		UserInfoService service = new UserInfoService();
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		
		int offsetUid = builder.createString("f1129364050428316");
		int nameOffset = builder.createString("");
		int avatarOffset = builder.createString("");
		int status = builder.createString("");
		int acc_type = builder.createString("");
		int vip = builder.createString("");
		int coin = builder.createString("");
		int level = builder.createString("");
		int detailOffset = builder.createString("");
		
		int ui = UserInfo.createUserInfo(builder, offsetUid, nameOffset, avatarOffset, status, acc_type, vip, coin, level, detailOffset);
		builder.finish(ui);
		
		UserInfo obj = UserInfo.getRootAsUserInfo(builder.dataBuffer());
		service.execute("",  Arrays.asList("","","","",obj));
		service.execute("",  Arrays.asList("","","","",obj));
	}
}
