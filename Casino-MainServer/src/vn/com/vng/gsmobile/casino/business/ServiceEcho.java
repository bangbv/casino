package vn.com.vng.gsmobile.casino.business;

import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class ServiceEcho{
	public void execute(List<?> params) throws Exception {
		// TODO Auto-generated method stub
		ChannelHandlerContext ctx = (ChannelHandlerContext) params.get(0);
		BinaryWebSocketFrame frame = (BinaryWebSocketFrame) params.get(1);
    	ctx.writeAndFlush(frame.retain());
	}

}
