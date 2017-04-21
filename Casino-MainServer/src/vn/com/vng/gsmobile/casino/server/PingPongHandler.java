package vn.com.vng.gsmobile.casino.server;

import java.util.Arrays;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import vn.com.vng.gsmobile.casino.entries.Checker;
import vn.com.vng.gsmobile.casino.entries.Handshake;

public class PingPongHandler extends MessageToMessageDecoder<WebSocketFrame> {

	@Override
	protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		if (frame instanceof PingWebSocketFrame) {
            frame.content().retain();
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()));
        }
		else if (frame instanceof PongWebSocketFrame) {
			System.out.println(Arrays.asList("client","pong", ctx.channel().id(), frame.toString()));
			Checker checker = Handshake.pingList.get(ctx.channel().id().toString());
	    	if(checker!=null){
	    		synchronized (checker) {
	    			checker.setResult(true);
	    			checker.notifyAll();
				}
	    	}
        }
		else
			out.add(frame.retain());
	}
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
