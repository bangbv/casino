/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
//The MIT License
//
//Copyright (c) 2009 Carl Bystršm
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package vn.com.vng.gsmobile.casino.client;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import vn.com.vng.gsmobile.casino.entries.Bot;
import vn.com.vng.gsmobile.casino.entries.CMD;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGame3LaShowCard;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGameTLMNSkip;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDGameTLMNUpdate;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDInviteGame;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDJoinGame;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDQuitGame;
import vn.com.vng.gsmobile.casino.flatbuffers.Game3LaGameInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.GameRoomState;
import vn.com.vng.gsmobile.casino.flatbuffers.GameTLMNInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.JoinGameType;
import vn.com.vng.gsmobile.casino.flatbuffers.QuitState;
import vn.com.vng.gsmobile.casino.flatbuffers.RoomResponse;
import vn.com.vng.gsmobile.casino.ulti.GZip;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {
	private Long uid = 0l;
    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    
    public ClientHandler(Long uid, WebSocketClientHandshaker handshaker) {
    	this.uid = uid;
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Lib.getLogger().debug(Arrays.asList(uid, ctx.channel().id() , "disconnected", this.getClass().getSimpleName()+".channelInactive"));
        Bot b = Bot.list.get(uid);
        if(b!=null)
        	b.reconnect();
    }

	@Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            Lib.getLogger().debug(Arrays.asList(uid, ctx.channel().id() ,"connected", this.getClass().getSimpleName()+".channelRead0"));
            handshakeFuture.setSuccess();
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof BinaryWebSocketFrame) {
        	receiptFormServer(ctx, frame);
        } else if (frame instanceof PingWebSocketFrame) {
        	ch.writeAndFlush(new PongWebSocketFrame());
        	Lib.getLogger().trace(Arrays.asList(uid, ctx.channel().id() , "pong:OK", this.getClass().getSimpleName()+".channelRead0"));
        } else if (frame instanceof CloseWebSocketFrame) {
            Lib.getLogger().debug(Arrays.asList(uid, ctx.channel().id() , "received closing", this.getClass().getSimpleName()+".channelRead0"));
            ch.close();
        }  
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	Lib.getLogger().error(Arrays.asList(ctx.channel().id() ,ctx, Lib.getStackTrace(cause), this.getClass().getSimpleName()+".exceptionCaught"));
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
        Bot b = Bot.list.get(uid);
        if(b!=null)
        	b.reconnect();
    }
    @SuppressWarnings({ "rawtypes"})
	private void receiptFormServer(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception{
		 ByteBufInputStream bbi = new ByteBufInputStream(frame.content());
		 byte c1 = bbi.readByte();
		 byte c2 = bbi.readByte();
		 byte c3 = bbi.readByte();
		 byte c4 = bbi.readByte();
		 if(c4==0){
    		int gzip = ((int)c3 & (int)0x80)>>7;
    		int version = (int)c3 & (int)0x7F;
		     String sCMD = c1+"_"+c2+"_"+version;
		     Map mService = (Map) Lib.getServiceConfig(false).get(sCMD);
		     if(mService!=null){
		    	 //1. Giải nén
			    byte[] b = null;
            	if(gzip==1)
            		b = GZip.decompress(IOUtils.toByteArray(bbi));
            	else
            		b = IOUtils.toByteArray(bbi);
            	//2. Giải mã
				byte[] b1 = null;
//				Object oSecKey = mService.get("server_seckey");
//				if("1".equals(Const.SESSION_KEY) && "1".equals(oSecKey)){
//					//Giai ma data request
//					Session s = new Session(sUID);
//					String sk = s.hget(Session.SECKEY);
//					if(sk !=null){
//			    		b1 = XXTEA.decrypt(b, sk.getBytes());
//					}
//				}
		     	ByteBuffer bb = ByteBuffer.wrap(b1==null?b:b1);
		        Object oData = null;
		     	Object oServer = mService.get("server");
		     	if(oServer!=null){
			        Class<?> clsRequest = Class.forName(mService.get("server").toString());
			        try{
			        	oData = Lib.invoke(clsRequest, "getRootAs"+clsRequest.getSimpleName(), new Class<?>[]{ByteBuffer.class}, new Object[]{bb});
			        }catch(Exception e){
			        	oData = new String(b, Charset.forName("UTF-8"));
			        }
		     	}
		        Lib.getLogService().debug(Arrays.asList(uid, ctx.channel().id() , c1, c2, c3, c4, oData.toString(), this.getClass().getSimpleName()+".receiptFormServer"));
	        	Bot bot = Bot.list.get(uid);
	        	FlatBufferBuilder builder = new FlatBufferBuilder(0);
	        	Table data = null;
	        	Integer state = null;
	        	Integer turnIdx = null;
	        	Integer playIdx = null;
	        	Long battleId = null;
	        	Long roomId = null;
	        	CMD cmd = CMD.getCMD(c1, c2, (byte)version);
	        	switch(cmd){
	        	case ROOM_WAIT:
	        		RoomResponse rs = (RoomResponse) oData;
	        		if(rs!=null){
		        		builder.finish(CMDJoinGame.createCMDJoinGame(builder, 
		        				uid, 
		        				rs.room().id(), 
		        				JoinGameType.PLAY, 
		        				builder.createString("trid"), 
		        				0
		        		));
		        		data = CMDJoinGame.getRootAsCMDJoinGame(builder.dataBuffer());
		        		bot.write(CMD.JOIN_ROOM, data);
	        		}
	        		break;
	        	case INVITE_ROOM:
	        		CMDInviteGame iv = (CMDInviteGame) oData;
	        		builder.finish(CMDJoinGame.createCMDJoinGame(builder, 
	        				uid, 
	        				iv.roomId(), 
	        				JoinGameType.PLAY, 
	        				builder.createString("trid"), 
	        				0
	        		));
	        		data = CMDJoinGame.getRootAsCMDJoinGame(builder.dataBuffer());
	        		bot.write(CMD.JOIN_ROOM, data);
	        		break;
	        	case JOIN_ROOM:
	        		RoomResponse rs2 = (RoomResponse) oData;
	        		if(rs2!=null){
		        		bot.setRoomId(rs2.room().id());
	        		}
	        		break;
	        	case QUIT_ROOM:
	        		CMDQuitGame qg = (CMDQuitGame) oData;
	        		if(qg.state()==QuitState.QUIT_ACCEPT||qg.state()==QuitState.QUIT_NOT_ENOUGH_MONEY||qg.state()==QuitState.QUIT_POOR_CONNECTION)
	        			bot.setRoomId(null);
	        		break;
	        	case TLMN_UPDATE_BATTLE_INFO:
	        	case TLMN_BATTLE_INFO:
	        		GameTLMNInfo gif = (GameTLMNInfo) oData;
	        		roomId = gif.roomId();
        			battleId = gif.gameId();
	        		state = gif.state();
	        		turnIdx = gif.turnIdx();
	        		playIdx = gif.playerIdx();
	        		if(state==GameRoomState.Finished){
	        			bot.setPlayerIdx(null);
	        		}
	        		else if(state==GameRoomState.Playing){
	        			for(int j = 0; j < gif.cardListLength(); j++){
	        				if(gif.cardList(j).playerId()==bot.getId()){
	        					bot.setPlayerIdx(j);
	        					break;
	        				}
	        			}
	        		}
	        		
	        		//skip turn here
	        		if(bot.getPlayerIdx() == playIdx && state==GameRoomState.Playing){
		        		builder.finish(CMDGameTLMNSkip.createCMDGameTLMNSkip(builder, 
		        				roomId, 
		        				battleId, 
		        				turnIdx, 
		        				playIdx
		        		));
		        		data = CMDGameTLMNSkip.getRootAsCMDGameTLMNSkip(builder.dataBuffer());
		        		bot.write(CMD.TLMN_SKIP_TURN, data);
	        		}
	        		break;
	        	case TLMN_UPDATE_BATTLE_INFO_TINY:
	        		CMDGameTLMNUpdate gup = (CMDGameTLMNUpdate) oData;
	        		roomId = gup.roomId();
        			battleId = gup.gameId();
	        		state = gup.state();
	        		turnIdx = gup.nextTurnIdx();
	        		playIdx = gup.nextPlayerIdx();
	        		synchronized (bot) {
						bot.wait(2000);
					}
	        		//skip turn here
	        		if(bot.getPlayerIdx() == playIdx && state==GameRoomState.Playing){
		        		builder.finish(CMDGameTLMNSkip.createCMDGameTLMNSkip(builder, 
		        				roomId, 
		        				battleId, 
		        				turnIdx, 
		        				playIdx
		        		));
		        		data = CMDGameTLMNSkip.getRootAsCMDGameTLMNSkip(builder.dataBuffer());
		        		bot.write(CMD.TLMN_SKIP_TURN, data);
	        		}
	        		break;
	        	case CAO_BATTLE_INFO:
	        		Game3LaGameInfo gif3 = (Game3LaGameInfo) oData;
	        		roomId = gif3.roomId();
        			battleId = gif3.gameId();
	        		state = gif3.state();
	        		if(state==GameRoomState.Finished){
	        			bot.setPlayerIdx(null);
	        		}
	        		else if(state==GameRoomState.Playing){
	        			for(int j = 0; j < gif3.cardListLength(); j++){
	        				if(gif3.cardList(j).playerId()==bot.getId()){
	        					bot.setPlayerIdx(j);
	        					break;
	        				}
	        			}
	        		}
	        		if(state==GameRoomState.Playing){
		        		synchronized (bot) {
							bot.wait(2000);
						}
		        		builder.finish(CMDGame3LaShowCard.createCMDGame3LaShowCard(builder, 
		        				roomId, 
		        				battleId, 
		        				bot.getPlayerIdx(),
		        				CMDGame3LaShowCard.createShowedCardsVector(builder, new byte[]{0,1,2})
		        		));
		        		data = CMDGameTLMNSkip.getRootAsCMDGameTLMNSkip(builder.dataBuffer());
		        		bot.write(CMD.CAO_SHOW_CARDS, data);
	        		}
	        		break;
				default:
					break;
	        	}
		     }
		     else{
		    	 Lib.getLogService().debug(Arrays.asList(uid, ctx.channel().id() , c1, c2, c3, c4, Hex.encodeHexString(IOUtils.toString(bbi).getBytes()), this.getClass().getSimpleName()+".receiptFormServer"));
		     }
		 }
		 else
		 	Lib.getLogService().debug(Arrays.asList(uid, ctx.channel().id() , c1, c2, c3, c4, Lib.getErrorMessage(c4), this.getClass().getSimpleName()+".receiptFormServer"));
    }
}
