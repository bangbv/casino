package vn.com.vng.gsmobile.casino.business;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;
import org.xxtea.XXTEA;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import vn.com.vng.gsmobile.casino.entries.ChannelType;
import vn.com.vng.gsmobile.casino.entries.Handshake;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.GZip;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class Service {
	public static byte CMDTYPE_RESPONSE = 0;
	public static byte CMDTYPE_REQUEST = 1;
	IService iService = null;
	String sTrid = "";
	public Service(IService iService){
		this.iService = iService;
	}
	public byte process(String sTrid, Channel c, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: cmd, 1: subcmd, 2: version, 3:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;//Khong xac dinh
		this.sTrid = sTrid;
		boolean isSendToClient = false;
		try{
			Lib.getLogService().debug(Arrays.asList(this.sTrid, params, c, this.getClass().getSimpleName()+".process.start"));
			List<?> lKq = Lib.invoke(this.iService, "execute", new Class<?>[]{String.class, List.class}, new Object[]{this.sTrid, Arrays.asList(c, params.get(0),params.get(1),params.get(2),params.get(3))});
			bKq = (byte) lKq.get(0);//error code
			List<?> channels = (List<?>) lKq.get(1); //ds client nhan phan hoi
			List<?> outparams = (List<?>) lKq.get(2); //outparams=[0: cmd, 1: sub_cmd, 2: version, 3: errorcode, 4:data FlatBuffer]
			isSendToClient = Service.sendToClient(this.getClass().getName(), this.sTrid, Service.CMDTYPE_RESPONSE, channels, outparams);
			Lib.getLogService().debug(Arrays.asList(this.sTrid, outparams, channels!=null && channels.size()>2?channels.size():channels, Lib.getErrorMessage(bKq), this.getClass().getSimpleName()+".process.finish"));
		}catch(Exception e){
			bKq = ErrorCode.UNKNOWN;
			if(isSendToClient){//neu chua phan hoi cho client, thi thong bao loi tai day
				List<?> channels = Arrays.asList(c);
				List<?> outparams = Arrays.asList(params.get(0),params.get(1),params.get(2),bKq,params.get(3)); //noi dung phan hoi
				Service.sendToClient(this.getClass().getSimpleName(), this.sTrid, Service.CMDTYPE_RESPONSE, channels, outparams);
			}
			Lib.getLogService().error(Arrays.asList(this.sTrid, params, c, Lib.getErrorMessage(bKq), Lib.getStackTrace(e), this.getClass().getSimpleName()+".process.catch"));
	    	throw e;//co nen ngat ket noi khi xay ra loi nghiep vu khong? mac dinh: ngat ket noi
		}
		return bKq;
	}
	@SuppressWarnings({ "resource", "rawtypes", "unused" })
	public static boolean sendToClient(String className, String sTrid, byte cmdtype, List<?> channels, List<?> params){
		// TODO Auto-generated method stub
		if(channels == null || channels.size()==0) return true;
		//params=[0: cmd, 1: sub_cmd, 2: version, 3: errorcode, 4:data FlatBuffer]
		byte bKq = (byte) params.get(3);
		try{
			if(cmdtype==CMDTYPE_REQUEST)
				Lib.getLogService().debug(Arrays.asList(sTrid, params, channels!=null && channels.size()>2?channels.size():channels, className+".sendToClient.start"));
			byte[] b2 = null;
			if(cmdtype==CMDTYPE_RESPONSE){
				if(bKq==ErrorCode.OK || (params.size()>4 && params.get(4)!=null)){
					Object oData = params.get(4);
					if(oData!=null){
				    	ByteBuffer bb = Lib.invoke(oData, 1, "getByteBuffer", new Class<?>[]{}, new Object[]{});
				    	b2 = Arrays.copyOfRange(bb.array(),bb.position(), bb.remaining()+bb.position());
					}
				}
				else 
					b2 = Lib.getErrorMessage(bKq).getBytes(Charset.forName("UTF-8"));
			}
			else{
				Object oData = params.get(4);
				if(oData!=null){
			    	ByteBuffer bb = Lib.invoke(oData, 1, "getByteBuffer", new Class<?>[]{}, new Object[]{});
			    	b2 = Arrays.copyOfRange(bb.array(),bb.position(), bb.remaining()+bb.position());
				}
			}
			//0. get CMD
    		Number version = (byte)params.get(2);
    		version = version.intValue() & (int)0x7F;
        	String sCMD = String.format("%d_%d_%d", params.get(0), params.get(1), version.byteValue());
        	Map mService = (Map) Lib.getServiceConfig(false).get(sCMD);
			boolean mustEncrypt = false;
			boolean mustGzip = false;
			if(mService!=null){
				//1. Mã hóa
				if("1".equals(Const.SESSION_KEY)){
					if("1".equals(mService.get("server_seckey"))){
						mustEncrypt = true;
		        	}
				}
				//2. Nén dữ liệu
				if("1".equals(mService.get("server_gzip"))){
					mustGzip = true;
					version = version.intValue() | (int)0x80;
	        	}
			}
			byte[] b1 = new byte[]{(byte) params.get(0), (byte) params.get(1), version.byteValue(), bKq};
			if(mustEncrypt){
				for(Object o : channels){
					if(o!=null){
						Channel c = (Channel) o;
						Long sUID = Handshake.getUser(c, ChannelType.Any);
	            		String sk = null;//s.hget(Session.SECKEY);
	            		if(sk !=null){
	                		//Ma hoa data response theo tung user
		            		byte[] b3 = XXTEA.encrypt(b2, sk.getBytes());
		    		    	byte[] bzip = mustGzip&&b3!=null?GZip.compress(b3):b3;
		    				//Gui phan hoi cho tung user
		            		byte[] b = ArrayUtils.addAll(b1, bzip);
		    				Lib.getLogService().trace(Arrays.asList(sTrid+"-"+c.id(), Hex.encodeHexString(b), c, className+".sendToClient.start"));
		    				BinaryWebSocketFrame frame = new BinaryWebSocketFrame();
		    		    	ByteBufOutputStream bbo = new ByteBufOutputStream(frame.content());
		    		    	bbo.write(b);
		    				c.writeAndFlush(frame);
		    		    	Lib.getLogService().trace(Arrays.asList(sTrid+"-"+c.id(), "OK", (bzip!=null?bzip.length:0), "draw="+(b3!=null?b3.length:0)+"b", className+".sendToClient.finish"));
	            		}
	            		else
	            			throw new Exception(Arrays.asList(c.id().toString(), sUID, "SESSION_KEY IS NULL").toString());
					}
				}
			}
			else{
		    	byte[] bzip = mustGzip&&b2!=null?GZip.compress(b2):b2;
		    	byte[] b = ArrayUtils.addAll(b1, bzip);
				Lib.getLogService().trace(Arrays.asList(sTrid, Hex.encodeHexString(b), channels, className+".sendToClient.start"));
				BinaryWebSocketFrame frame = new BinaryWebSocketFrame();
		    	ByteBufOutputStream bbo = new ByteBufOutputStream(frame.content());
		    	bbo.write(b);
				ChannelGroup cg = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
				for(Object c : channels){
					if(c!=null)
						cg.add((Channel) c);
				}
				cg.writeAndFlush(frame);
		    	Lib.getLogService().trace(Arrays.asList(sTrid, "OK", (bzip!=null?bzip.length:0)+"b", "draw="+(b2!=null?b2.length:0)+"b",className+".sendToClient.finish"));
			}
			if(cmdtype==CMDTYPE_REQUEST)
				Lib.getLogService().debug(Arrays.asList(sTrid, "OK", className+".sendToClient.finish"));
		}catch(Exception e){
			Lib.getLogService().error(Arrays.asList(sTrid, params, channels, Lib.getStackTrace(e), className+".sendToClient.catch"));
		}
		return true;
	}
}
