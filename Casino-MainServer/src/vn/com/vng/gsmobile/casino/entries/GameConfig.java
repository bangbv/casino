package vn.com.vng.gsmobile.casino.entries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import com.couchbase.client.java.document.json.JsonObject;
import com.google.gson.Gson;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class GameConfig {
	public static String GAMELIST = "GAMELIST";	
	public static String GAMENAME = "GAMENAME";	
	public static String GAMESERVICE = "GAMESERVICE";	
	public static String TIME_INITBATTLE = "TIME_INITBATTLE";	
	public static String TIME_TURNPLAY = "TIME_TURNPLAY";	
	public static String TIME_FINISHBATTLE = "TIME_FINISHBATTLE";	
	public static String REQUIRECOIN_RATE = "REQUIRECOIN_RATE";
	
	public static String ROOMLIST = "ROOMLIST";	
	public static String GAMETYPE = "GAMETYPE";
	public static String LOBBYTYPE = "LOBBYTYPE";
	public static String BET = "BET";
	public static String MAXSIT = "MAXSIT";
	public static String REQUIRECOIN = "REQUIRECOIN";
	public static String REQUIRELEVEL = "REQUIRELEVEL";
	public static String INITROOM = "INITROOM";
	public static String MAXROOM = "MAXROOM";	
	public static String INITFAKEROOM = "INITFAKEROOM";
	public static String MAXFAKEROOM = "MAXFAKEROOM";	
	public static String TIP = "TIP";
	public static String PREFIX = "20170210";
	public static String GAME_TABLENAME = "20_";
	
	static JsonObject data = null;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized static JsonObject load(boolean isReload) {
		new File(Const.CONFIG_PATH).mkdir();
		String sFN = Const.CONFIG_PATH+"game.cfg";
		if(data==null || isReload)
		try {
			Lib.getLogger().info(Lib.class.getName()+".load["+sFN+"]:...");
			Gson gson = new Gson();
			Map map = (Map) gson.fromJson(new BufferedReader(new FileReader(sFN)), Object.class);
			data = JsonObject.from(map);
			Lib.getLogger().info(Lib.class.getName()+".load["+sFN+"]: OK");
		} catch (Exception e) {
			e.printStackTrace();
			Lib.getLogger().error(Lib.class.getName()+".load["+sFN+"]:"+Lib.getStackTrace(e));
			data = null;
		}
		return data;
	}

}
