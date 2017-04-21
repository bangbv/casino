package com.vng.gsmobile.casino.util;

public class Const {

	public static final String ENCODE = "UTF-8";
	public static String SPLIT_CHAR = "|";
	public static String LOG_PATH = "D:/workspace/casinoServer/casino/log/";
	public static String LOG_NAME = "casino";
	//public static String CONFIG_PATH = "/home/longnn2/casino/server/common/commonserver/src/main/resources/";
	public static String CONFIG_PATH = "D:/workspace/casinoServer/Casino-CommonServer/src/main/resources/";
	public static String SERVICE_PATH = "D:/workspace/casinoServer/casino/service/";
	
	public static String SERVER_HOST = "127.0.0.1";
	public static int SERVER_PORT = 8989;
	public static int CONTROL_PORT = 6886;
	public static int READ_TIMEOUT = 35000;//35s khong co giao dich thi dong ket noi client
	public static int CONNECT_TIMEOUT = 10000;//max 10s ket noi client-server
	public static boolean IS_BUSINESS_SLOWLY = true;
	public static int MAX_THREADS_EXECUTE_MESSAGE = 1000;
	
	public static String SERVER_TYPE = "0"; //0: all in one, 1: lobby server lobby, 2: battle server, 3: chat server
	public static String ALLINONE_SERVER = "0";
	public static String LOBBY_SERVER = "1";
	public static String BATTLE_SERVER = "2";
	public static String CHAT_SERVER = "3";

	public static String SESSION_KEY = "1"; //1: yêu cầu mã hóa một số lệnh pvp giao tiếp với server, 0: không yêu cầu mã hóa
	public static String ENCRYPT_KEY = "2bd58608733c632e400cae98714eaec9";
	public static String WALLET_KEY = "76JfaKN6NxXMKFx76nKi6DJNMXf6YfXUSKNMXNnT";
	public static int MAX_BATTLE = 0;


	public static long NOTICE_SCHEDULE = 30000;
	public static long USERPUSH_SCHEDULE = 1000;
	public static long RSCPUSH_SCHEDULE = 2000;
	public static long GC_SCHEDULE = 30000; 
	public static long MONITOR_SCHEDULE = 30000; 
	public static long GAMEDATA_SCHEDULE = 1000; 
	public static long GAMEINIT_SCHEDULE = 1000; 

	public static String EMAIL_HOST = "10.30.76.11";
	public static String EMAIL_PORT = "25";
	public static String EMAIL_ACCOUNT = "casino@vng.com.vn";
	public static String EMAIL_PASSWORD = "";
	public static String EMAIL_ALERT = "longnn2@vng.com.vn";
	
	public static long PVPRANK_TOP = 100;
	public static String PVPRANK_NAME = "PVPRANK";
	public static Integer PVPRANK_LIMIT = 1000;
	
	public static String PVPBET_NAME = "PVPBET";
	public static int BET_TYPE1 = 1;
	public static int BET_VALUE1 = 200;
	public static int BET_TYPE2 = 2;
	public static int BET_VALUE2 = 2;
	public static int BET_TYPE3 = 2;
	public static int BET_VALUE3 = 10;

	public static int RESOURCE_GAIN = 1;
	public static int RESOURCE_USE = 2;
	public static int REWARD_PVP = 41;
	public static int STAGE_TYPE = 7;

	public static int REDIS_DB_PVP_MAIN = 0; //main pvp redis database
	public static int REDIS_DB_PVP_USER = 10; //main pvp redis database
	public static int REDIS_DB_PVP_SESSION = 10; //main pvp redis database
	public static int REDIS_DB_PVP_HANDSHAKE = 10; //main pvp redis database
	public static int REDIS_DB_PVP_INVITE = 10; //main pvp redis database
	public static int REDIS_DB_PVP_FRIEND = 10; //main pvp redis database
	public static int REDIS_DB_PVP_BATTLEINIT = 10; //battleinit pvp redis database	
	public static int REDIS_DB_PVP_BOT = 10; //bot
	public static int REDIS_DB_PVP_RANK = 11; //rank pvp redis database
	public static int REDIS_DB_PVP_CONFIG = 11; //config
	public static int REDIS_DB_PVP_NOTICE = 11; //notice pvp redis database
	public static int REDIS_DB_PVP_USERPUSH = 12; //push user status and rank redis database
	public static int REDIS_DB_PVP_BATTLEDATAINIT = 13; //battledatainit pvp redis database
	public static int REDIS_DB_PVP_LOG = 14; //log pvp redis database
	public static int REDIS_DB_PVP_RSCPUSH = 15; //push user resource (coin, ruby, sora, hkey, item)
	
	public static int REDIS_EXPIRE_USER = 24*60*60; //1 ngay
	public static int REDIS_EXPIRE_SESSION = 60*60; //1 gio
	public static int REDIS_EXPIRE_FRIEND = 2*24*60*60; //1 tuan
	
	public static long REGEN_SORA_TIME = 1800000; //30 phut
	public static int TOP_REFRESH_INTERVAL = 18000;//giây
	public static int FRIEND_REFRESH_INTERVAL = 18000;//giây	
	public static int LIMIT_PACKGAGE_SIZE = 50;//user
	
	public static boolean IS_STOPPING = false;
	public static boolean IS_STARTING = true;
	public static String SHUTDOWN_MSG = "Hệ thống bắt đầu bảo trì sau ít phút, vui lòng thoát game và nghỉ ngơi chút nhé các thuyền trưởng.";
	
	public static String ALGORITHM = "SHA-256";
	public static String DATA_EMPTY = "DATA EMPTY";
	public static String SUCCESS = "SUCCESS";
	public static String GAME_DEFINE = "gameDefine";
	
}
