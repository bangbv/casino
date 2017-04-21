package vn.com.vng.gsmobile.casino.server;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import vn.com.vng.gsmobile.casino.entries.ServerType;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class Server {
	private int iPort = 8080;
	EventLoopGroup eBossGroup = null;
	EventLoopGroup eWorkerGroup = null;
    EventExecutorGroup eExecutorGroup = null;
	private int iStatus = -1;//init
	public Server(int iPort, EventLoopGroup boss, EventLoopGroup worker) {
		// TODO Auto-generated constructor stub
		this.iPort = iPort;
		this.eBossGroup=boss;
		this.eWorkerGroup=worker;
	}
	public Server(int iPort, EventLoopGroup boss, EventLoopGroup worker, EventExecutorGroup exe) {
		// TODO Auto-generated constructor stub
		this.iPort = iPort;
		this.eBossGroup=boss;
		this.eWorkerGroup=worker;
		this.eExecutorGroup=exe;
	}
	public void start(){
    	new Thread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
		        try {
				// TODO Auto-generated method stub
		        	iStatus = 1;//on
		        	WriteBufferWaterMark wb = new WriteBufferWaterMark(20*1024*1024, 40*1024*1024);
		            ServerBootstrap b = new ServerBootstrap();
		            b.group(eBossGroup, eWorkerGroup)
		             .channel(NioServerSocketChannel.class)
		             .childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel arg0) throws Exception {
							// TODO Auto-generated method stub
							ChannelPipeline p = arg0.pipeline();
							p.addLast(new ReadTimeoutHandler(Const.READ_TIMEOUT, TimeUnit.MILLISECONDS));
							if(Const.SERVER_TYPE == ServerType.AllInOne || Const.SERVER_TYPE == ServerType.Game){
								p.addLast(new IdleStateHandler(Const.READ_IDLE/1000, 0, 0));
								p.addLast(new IdleHandler());
							}
					        p.addLast(new HttpServerCodec());
					        p.addLast(new HttpObjectAggregator(65536));
					        p.addLast(new PingPongHandler());
					        if(eExecutorGroup!=null)
					        	p.addLast(eExecutorGroup, "handler", new ServerHandler());
					        else
					        	p.addLast("handler", new ServerHandler());
						}
					})
		             .option(ChannelOption.SO_BACKLOG, 10240)
		             .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Const.CONNECT_TIMEOUT)
		             .option(ChannelOption.SO_SNDBUF, 20*1024*1024)
		             .option(ChannelOption.SO_RCVBUF, 20*1024*1024)
		             .option(ChannelOption.WRITE_BUFFER_WATER_MARK,wb)
		             .option(ChannelOption.SO_KEEPALIVE, true)
					 .option(ChannelOption.TCP_NODELAY, true)
		             .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, Const.CONNECT_TIMEOUT)
		             .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,wb)
		             .childOption(ChannelOption.MAX_MESSAGES_PER_READ,Integer.MAX_VALUE)
		             .childOption(ChannelOption.SO_KEEPALIVE, true)
					 .childOption(ChannelOption.TCP_NODELAY, true);
		            Channel ch = b.bind(iPort).sync().channel();
		            ChannelConfig cg = ch.config();
		            Lib.getLogger().info("ChannelConfig:"+cg.getOptions());
		            ch.closeFuture().sync();
		        } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Lib.getLogger().error(e.getMessage());
				} finally {
		        	iStatus = 0;//off
		            eBossGroup.shutdownGracefully();
		            eWorkerGroup.shutdownGracefully();
		            if(eExecutorGroup!=null)
		            	eExecutorGroup.shutdownGracefully();
		        }
			}
		}).start();
	}
	public int getStatus(){
		return iStatus;
	}
}
