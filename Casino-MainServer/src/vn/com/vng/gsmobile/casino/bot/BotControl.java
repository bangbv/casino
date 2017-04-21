package vn.com.vng.gsmobile.casino.bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class BotControl implements Runnable{
	Socket s = null;
	public BotControl(Socket s){
		this.s = s;
	}
	@SuppressWarnings({ "unused" })
	@Override
	public void run() {
		// TODO Auto-generated method stub
		PrintWriter out1 = null;
		try {
			out1 = new PrintWriter(s.getOutputStream(), true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		final PrintWriter out = out1;
		BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String sCMD = "";
            String sPARAMS = "";
    		FlatBufferBuilder builder;
    		ArrayList<Channel> lc = new ArrayList<Channel>();
            while ((sCMD = in.readLine()) != null) {
            	sCMD = sCMD.replace(" ", "").replace("\t", "").toUpperCase();
            	String[] lCMD = sCMD.split(":");
            	sCMD = lCMD[0];
            	if(lCMD.length>1)
            		sPARAMS = lCMD[1];
            	
            	if (sCMD.equalsIgnoreCase("EXIT")){
            		out.println("bye bye!");
                    break;
            	}
            	else
                switch(sCMD){
	            	case "LOGTRACE":
	            		Lib.setLoggerLevel("TRACE");
	            		out.println("done!");
	            		break;
                	case "LOGDEBUG":
                	case "DEBUGON":
                		Lib.setLoggerLevel("DEBUG");
                		out.println("done!");
                		break;
                	case "LOGINFO":                		
                	case "DEBUGOFF":
                		Lib.setLoggerLevel("INFO");
                		out.println("done!");
                		break;
                	case "CONFIGRELOAD": 
                	case "RELOADCONFIG":                		
                	case "LOADCONFIG":
                		if(!Const.IS_STOPPING){
                			Lib.loadConfig(true, BotClass.APPCFG);
	                		out.println("done!");
                		}
                		else
                			out.println("reject because server is stopping!");
                		break; 
                	case "VIEW":
                	case "INFO":
                	case "SHOWINFO":
                		out.println("done!");
                		break;     
                	case "SHUTDOWN":
                		out.println("shutting down...");
                		System.exit(0);
                	default:
                		out.println(sCMD + " don't support");
                }
            }
        } catch (Exception e) {
			// TODO Auto-generated catch block
            if(out!=null) out.println(e.getMessage());
			Lib.getLogger().error(BotControl.class.getName()+".run:"+Lib.getStackTrace(e));
		} finally {
        	try {
				this.s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(out!=null) out.println(e.getMessage());
				Lib.getLogger().error(BotControl.class.getName()+".finally:"+Lib.getStackTrace(e));
			}
        }
	}
	
}