package vn.com.vng.gsmobile.casino.server;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;
import org.xxtea.XXTEA;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import vn.com.vng.gsmobile.casino.business.IService;
import vn.com.vng.gsmobile.casino.business.Service;
import vn.com.vng.gsmobile.casino.entries.CMD;
import vn.com.vng.gsmobile.casino.entries.ChannelType;
import vn.com.vng.gsmobile.casino.entries.Handshake;
import vn.com.vng.gsmobile.casino.entries.RoomManager;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.GZip;
import vn.com.vng.gsmobile.casino.ulti.IOUtils;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final String WEBSOCKET_PATH = "/websocket";
	private WebSocketServerHandshaker handshaker;

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
		Lib.getLogger().debug(Arrays.asList(ctx.channel().id() ,ctx, this.getClass().getSimpleName()+".handlerAdded"));
	}
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
		if(!Const.IS_STOPPING){
			byte bChannelType = Handshake.getChannelType(ctx.channel());
			Long sUID = null;
			switch(bChannelType){
			case ChannelType.Game:
				sUID = Handshake.gameWebsocket.remove(ctx.channel());
				RoomManager.updateUserLobbyMapping(sUID, System.currentTimeMillis());
				break;
			case ChannelType.Main:
				sUID = Handshake.mainWebsocket.remove(ctx.channel());
				break;
			}
			Lib.getLogger().debug(Arrays.asList("uid="+sUID ,ctx.channel().id(),"ChannelType="+bChannelType,ctx,"mainWebsocket.size="+Handshake.mainWebsocket.getChannels().size(),"gameWebsocket.size="+Handshake.gameWebsocket.getChannels().size(), this.getClass().getSimpleName()+".handlerRemoved"));
		}
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
        if (msg instanceof FullHttpRequest) {
            handshake(ctx, (FullHttpRequest) msg);
        } 
		else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        } 
	}
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
     	Lib.getLogger().error(Arrays.asList(ctx.channel().id() ,ctx, Lib.getStackTrace(cause), this.getClass().getSimpleName()+".exceptionCaught"));
        ctx.close();
    }
	@SuppressWarnings("unused")
	private void handshake(ChannelHandlerContext ctx, FullHttpRequest req) {
		Lib.getLogger().debug(Arrays.asList(ctx.channel().id() ,ctx, req, this.getClass().getSimpleName()+".handshake.request"));
        if (Const.IS_STOPPING) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_GATEWAY));
            return;
        }
        // Handle a bad request.
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }
        // Allow only GET methods. Or Send an error page otherwise.
        if (req.method() != GET || !req.uri().startsWith(WEBSOCKET_PATH)) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }
        // Verify
        String sProtocol = req.headers().get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
        Map<String, String> p = parseProtocol(sProtocol);
        String sSIG = p.get("sig");
        Long sUID = null;
        try{
        	sUID = Long.parseLong(p.get("uid"));
        }catch (Exception e) {
			// TODO: handle exception
        	sUID = null;
		}
        String sGID = p.get("game");
        byte bChannelType = (sGID==null)?ChannelType.Main:ChannelType.Game;
        if(sUID==null || sUID == 0){
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }
        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
        		"ws://"+req.headers().get(HttpHeaderNames.HOST) + WEBSOCKET_PATH, 
        		sProtocol, true, 1024 * 1024
        );
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
    		Lib.getLogger().error(Arrays.asList(ctx.channel().id() ,"sendUnsupportedVersionResponse", this.getClass().getSimpleName()+".handshake.response"));
        } else {
            handshaker.handshake(ctx.channel(), req);
        	switch(bChannelType){
        	case ChannelType.Game:
        		Handshake.gameWebsocket.add(sUID, ctx.channel());
        		RoomManager.updateUserLobbyMapping(sUID, Long.MAX_VALUE);
        		break;
        	case ChannelType.Main:
        		Handshake.mainWebsocket.add(sUID, ctx.channel());
        		break;
        	}
            Lib.getLogger().debug(Arrays.asList("uid="+sUID ,ctx.channel().id(), "ChannelType="+bChannelType,"OK", "mainWebsocket.size="+Handshake.mainWebsocket.getChannels().size(),"gameWebsocket.size="+Handshake.gameWebsocket.getChannels().size(), this.getClass().getSimpleName()+".handshake.response"));
        }
    }
    private static void sendHttpResponse(
        ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }

        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
        Lib.getLogger().error(Arrays.asList(ctx.channel().id() ,ctx, req, res, ServerHandler.class.getSimpleName()+".handshake.response"));
    }
	@SuppressWarnings({ "rawtypes", "unused" })
	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            Lib.getLogger().debug(Arrays.asList(ctx.channel().id() ,"CloseWebSocketFrame", this.getClass().getSimpleName()+".handleWebSocketFrame.close"));
            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
    		Long sUID = Handshake.getUser(ctx.channel());
        	String sTrid = sUID+"_"+ctx.channel().id() + "_" + System.currentTimeMillis();
        	ByteBufInputStream bbi = new ByteBufInputStream(frame.content());
        	byte[] b = IOUtils.readFully(bbi, -1, true);
        	byte bKq = ErrorCode.UNKNOWN;
        	try{
	        	Lib.getLogService().trace(Arrays.asList(sTrid, Hex.encodeHexString(b), b.length+"b", ctx, this.getClass().getSimpleName()+".receiptFormClient.start"));
	        	Map mService = null;
        		int gzip = 0;
        		int version = 0;
	        	if(b.length >= 4){
	        		gzip = ((int)b[2] & (int)0x80)>>7;
	        		version = (int)b[2] & (int)0x7F;
		        	String sCMD = String.format("%d_%d_%d", b[0],b[1],version);
		            mService = (Map) Lib.getServiceConfig(false).get(sCMD);
	        	}
	            if(mService!=null){
	            	Object oData = null;
	            	Object oDataClass = mService.get("client");
	            	if(oDataClass!=null){
	            		//1.Giải nén
		            	byte[] bunzip = null;
		            	if(gzip==1)
		            		bunzip = GZip.decompress(ArrayUtils.subarray(b, 4, b.length));
		            	else
		            		bunzip = ArrayUtils.subarray(b, 4, b.length);
		            	//2. Giải mã
		            	Object oSecKey = mService.get("client_seckey");
		            	ByteBuffer bb = null;
		            	if("1".equals(Const.SESSION_KEY) && "1".equals(oSecKey)){
		            		//Giai ma data request
		            		String sk = null;//s.hget(Session.SECKEY);
		            		if(sk !=null){
			            		byte[] b1 = XXTEA.decrypt(bunzip, sk.getBytes());
			            		if(b1!=null)
			            			bb = ByteBuffer.wrap(b1,0, b1.length);
			            		else{
			            			bKq = ErrorCode.UNDECRYPT;
									Service.sendToClient(
											ServerHandler.class.getName(), 
											sTrid, Service.CMDTYPE_REQUEST, 
											Arrays.asList(ctx.channel()), 
											Arrays.asList(CMD.SESSION_ERROR.cmd,CMD.SESSION_ERROR.subcmd,CMD.SESSION_ERROR.version,bKq,null)
									);
			            		}
		            		}
		            		else
		            			throw new Exception(Arrays.asList(ctx.channel().id().toString(), sUID, "SESSION_KEY IS NULL").toString());
		            	}
		            	else
		            		bb = ByteBuffer.wrap(bunzip, 0, bunzip.length);//b,4, b.length-4);
		                Class<?> clsRequest = Class.forName(oDataClass.toString());
		                oData = Lib.invoke(clsRequest, "getRootAs"+clsRequest.getSimpleName(), new Class<?>[]{ByteBuffer.class}, new Object[]{bb});
	            	}
	                Class<?> clsService = Class.forName(mService.get("service").toString());
	                Service pvpService = new Service((IService) clsService.newInstance());
	                bKq = pvpService.process(sTrid, ctx.channel(), Arrays.asList(b[0], b[1], b[2], oData));
	            }
	            else
	            	bKq = ErrorCode.UNDEFINE;//dich vu chua dinh nghia
	            Lib.getLogService().trace(Arrays.asList(sTrid, Lib.getErrorMessage(bKq), this.getClass().getSimpleName()+".receiptFormClient.finish"));
        	}catch(Exception e){
        		Lib.getLogService().error(Arrays.asList(sTrid, Hex.encodeHexString(b), b.length, ctx, Lib.getErrorMessage(bKq), Lib.getStackTrace(e), this.getClass().getSimpleName()+".receiptFormClient.catch"));
    	    	throw e;
        	}
            return;
        }
    }
	private Map<String, String> parseProtocol(String sProtocol){
		Map<String, String> m = new HashMap<String, String>();
        String[] protocol = sProtocol.split(",");
        for(String p : protocol){
        	String[] tmp = p.split("-");
        	if(tmp.length>1){
        		m.put(tmp[0].trim(), tmp[1].trim());
        	}
        }
		return m;
	}
}
