package vn.com.vng.gsmobile.casino.test;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.flatbuffers.UserInfo;

public class testFlatBuffer2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		builder.finish(UserInfo.createUserInfo(builder, 
				builder.createString("123456"), 
				builder.createString("Long"), 
				builder.createString("avatar"), 
				0, 
				0, 
				0, 
				0, 
				0, 
				0
			));
		UserInfo u = UserInfo.getRootAsUserInfo(builder.dataBuffer());
		System.out.println(u);
		u.mutateCoin(1000l);
		System.out.println(u);
		UserInfo.addCoin(builder, 1000l);
		System.out.println(u);
		u = UserInfo.getRootAsUserInfo(builder.dataBuffer());
		System.out.println(u);
	}

}
