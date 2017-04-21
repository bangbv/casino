package com.vng.gsmobile.casino.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import com.google.gson.Gson;

public class GameConfig {

	@SuppressWarnings("rawtypes")
	static Map gameConfig = null;

	@SuppressWarnings("rawtypes")
	public synchronized static Map getGameConfig(boolean isReload) {
		new File(Const.CONFIG_PATH).mkdir();
		String sFN = Const.CONFIG_PATH + "game_config.cfg";
		if (gameConfig == null || isReload)
			try {
				// Lib.getLogger().info(Lib.class.getName()+".getDBConfig["+sFN+"]:...");
				Gson gson = new Gson();
				gameConfig = (Map) gson.fromJson(new BufferedReader(new FileReader(sFN)), Object.class);
				// Lib.getLogger().info(Lib.class.getName()+".getDBConfig["+sFN+"]:
				// OK");
			} catch (Exception e) {
				e.printStackTrace();
				// Lib.getLogger().error(Lib.class.getName()+".getDBConfig["+sFN+"]:"+Lib.getStackTrace(e));
				gameConfig = null;
			}
		return gameConfig;
	}
}
