package vn.com.vng.gsmobile.casino.client;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import com.google.flatbuffers.Table;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import vn.com.vng.gsmobile.casino.entries.CMD;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public final class Client{
	Channel channel = null;
	EventLoopGroup group = null;
	Bootstrap bootstrap = null;
	String url = null;
	Long uid = null;
	Byte gameid = null;
	boolean isConnecting = false;
	boolean isConnected = false;

	public Client(Long uid) {
		// TODO Auto-generated constructor stub
		this.uid = uid;
	}
	public Client(String url, Long uid, Byte... gameid) {
		// TODO Auto-generated constructor stub
		connect(url, uid, gameid);
	}
	public Channel getChannel(){
		return channel;
	}
	public synchronized boolean connect(String url, Long uid, Byte... gameid){
		this.url = url;
		this.uid = uid;
		if(gameid!=null && gameid.length>0 && gameid[0]!=null)
			this.gameid = gameid[0];
		else
			this.gameid = null;
		return connect();
	}
	private synchronized boolean connect(){
		if(this.url==null) return false;
		if(isConnecting) return false;
		isConnecting = true;
		try {
	        URI uri = new URI(this.url);
        	String sSig = "sig-";
        	String sProtocol = sSig+", uid-"+this.uid;
        	if(this.gameid!=null)
        		sProtocol += ", game-"+this.gameid;
        	final ClientHandler handler = new ClientHandler(
            	uid,
	            WebSocketClientHandshakerFactory.newHandshaker(
        			uri, 
        			WebSocketVersion.V13, 
        			sProtocol, 
        			false, 
        			new DefaultHttpHeaders()
	            )
            );
    		group = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(
                    		new HttpClientCodec(),
                            new HttpObjectAggregator(8192),
                            WebSocketClientCompressionHandler.INSTANCE,
                            handler
                    );
                 }
             });
            channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
            handler.handshakeFuture().sync();
            isConnecting = false;
            isConnected = true;
    		return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Lib.getLogger().error(Arrays.asList(Lib.getStackTrace(e), this.getClass().getSimpleName()+".connect.catch"));
			group.shutdownGracefully();
			if(channel!=null) channel.disconnect();
			bootstrap = null;
			channel = null;
			group = null;
			isConnecting = false;
			return false;
		}
	}
	private synchronized boolean reconnect(){
		disconnect();
		return connect();
	}
	private synchronized boolean disconnect(){
		isConnected = false;
		try {
			if(group!=null) group.shutdownGracefully();
			if(channel!=null) channel.disconnect();
			bootstrap = null;
			channel = null;
			group = null;
			return true;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Lib.getLogger().error(Arrays.asList(Lib.getStackTrace(e1), this.getClass().getSimpleName()+".disconnect.catch"));
			if(group!=null) group.shutdownGracefully();
			if(channel!=null) channel.disconnect();
			bootstrap = null;
			channel = null;
			group = null;
			return false;
		}
	}
	public boolean isActive(){
		return isConnected && group!= null && channel!=null && channel.isActive();
	}
	public synchronized boolean close(){
		return disconnect();
	}
	public ChannelFuture write(CMD cmd){
		return write(cmd, null);
	}
	@SuppressWarnings({ "rawtypes", "resource" })
	public ChannelFuture write(CMD cmd, Table data){
		if(!isActive()) reconnect();
		if(isActive())
		try{
			Map mService = (Map) Lib.getServiceConfig(false).get(cmd.toString());
			if(mService!=null){
				byte[] b = null;
				byte[] b1 = new byte[]{cmd.cmd, cmd.subcmd, cmd.version, 0};
				byte[] b2 = null;
				if(data!=null){
			    	ByteBuffer bb = data.getByteBuffer();
			    	b2 = Arrays.copyOfRange(bb.array(),bb.position(), bb.remaining()+bb.position());
				}
				b = ArrayUtils.addAll(b1, b2);
				BinaryWebSocketFrame frame = new BinaryWebSocketFrame();
		    	ByteBufOutputStream bbo = new ByteBufOutputStream(frame.content());
		    	bbo.write(b);
				ChannelFuture cf = channel.writeAndFlush(frame);
				Lib.getLogService().debug(Arrays.asList(uid, channel.id() , b1[0], b1[1], b1[2], b1[3], data, this.getClass().getSimpleName()+".write"));
				return cf;
			}
		}catch(Exception e){
			Lib.getLogService().error(Arrays.asList(Lib.getStackTrace(e), this.getClass().getSimpleName()+".write.catch"));
			reconnect();
		}
		return null;
	}
}
