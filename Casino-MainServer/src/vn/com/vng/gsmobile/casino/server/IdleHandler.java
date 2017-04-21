package vn.com.vng.gsmobile.casino.server;

import java.util.Arrays;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import vn.com.vng.gsmobile.casino.entries.ChannelType;
import vn.com.vng.gsmobile.casino.entries.Handshake;

public class IdleHandler extends ChannelDuplexHandler {
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
            	if(Handshake.getChannelType(ctx.channel())==ChannelType.Game){
	            	PingWebSocketFrame msg = new PingWebSocketFrame();
	                ctx.channel().writeAndFlush(msg);
	    	    	System.out.println(Arrays.asList("server keep alive", "ping", ctx.channel().id(), msg.toString()));
            	}
            }
        }
    }
}
