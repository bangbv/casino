package vn.com.vng.gsmobile.casino.entries;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import com.google.flatbuffers.FlatBufferBuilder;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGameNotice;

public class Notice {
	public static String WINNER_NOTICE = "Chúc mừng [color=#FF0000]%s[/color] vừa giành chiến thắng";
	public static String WELCOME_NOTICE = "Ai là người giỏi nhất";
	
	public static CMDGameNotice toGameNotice(Byte game_type, List<String> l){
		CMDGameNotice rs = null;
		FlatBufferBuilder builder = new FlatBufferBuilder(0);
		List<Integer> nl = new ArrayList<>();
		for(String s : l){
			nl.add(builder.createString(s));
		}

		builder.finish(CMDGameNotice.createCMDGameNotice(builder, 
				game_type.intValue(), 
				CMDGameNotice.createNoticeListVector(builder, ArrayUtils.toPrimitive(nl.toArray(new Integer[nl.size()])))
		));
		rs = CMDGameNotice.getRootAsCMDGameNotice(builder.dataBuffer());
		return rs;
	}
}
