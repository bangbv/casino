package vn.com.vng.gsmobile.casino.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.google.flatbuffers.FlatBufferBuilder;

import vn.com.vng.gsmobile.casino.flatbuffers.ListUID;
import vn.com.vng.gsmobile.casino.flatbuffers.ListUserInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.UserAccType;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.UserStatus;

public class testListUser {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FlatBufferBuilder builder1 = new FlatBufferBuilder(0);
		int il = ListUID.createListUID(builder1, 
				builder1.createString("1111111"), 
				ListUID.createListVector(builder1, new long[]{123456,654321})
			);
		builder1.finish(il);
		ListUID rq = ListUID.getRootAsListUID(builder1.dataBuffer());
		System.out.println(rq);
		
		ListUserInfo rs = null;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		List<Integer> l = new ArrayList<>();
		for(int i=0; i < rq.listLength(); i++){
			int iu = UserInfo.createUserInfo(builder, 
					rq.list(i), 
					0, 
					0, 
					UserStatus.On_Lobby, 
					UserAccType.Facebook, 
					0, 
					0, 
					0, 
					0
				);
			l.add(iu);
		}
		int iul = ListUserInfo.createListUserInfo(builder, 
				builder.createString(rq.trans()), 
				0, 
				ListUserInfo.createListVector(builder, ArrayUtils.toPrimitive(l.toArray(new Integer[l.size()])))
			);
		builder.finish(iul);
		rs = ListUserInfo.getRootAsListUserInfo(builder.dataBuffer());
		System.out.println(rs);
		
	}

}
