package vn.com.vng.gsmobile.casino.ulti;

import vn.com.vng.gsmobile.casino.entries.ServerType;

public class Const {

	public static String SPLIT_CHAR = "|";
	public static String LOG_PATH = "C:/casino/log/";
	public static String APP_NAME = "casino";
	public static String CONFIG_PATH = "C:/casino/config/main/";
	public static String SERVICE_PATH = "C:/casino/service/";
	
	public static String SERVER_HOST = "127.0.0.1";
	public static int SERVER_PORT = 8080;
	public static int CONTROL_PORT = 6886;
	public static int BOT_CONTROL_PORT = 6887;
	public static int READ_TIMEOUT = 60000;//60s khong co giao dich thi dong ket noi client
	public static int READ_IDLE = 50000;//50s client khong gui lenh gi thi ping
	public static int PING_TIMEOUT = 3000;//3s 
	public static int CONNECT_TIMEOUT = 10000;//max 10s ket noi client-server
	public static boolean IS_BUSINESS_SLOWLY = true;
	public static int MAX_THREADS_EXECUTE_MESSAGE = 1000;
	
	public static byte SERVER_TYPE = ServerType.AllInOne;

	public static String SESSION_KEY = "1"; //1: yêu cầu mã hóa một số lệnh pvp giao tiếp với server, 0: không yêu cầu mã hóa
	public static String ENCRYPT_KEY = "6a5edd7eaa2def360b3fe712ced84981";
	public static int MAX_ROOM = 1000;
	public static int INIT_SIT_IN_LOBBY = 8*MAX_ROOM;


	public static long NOTICE_SCHEDULE = 30000;
	public static long USERPUSH_SCHEDULE = 1000;
	public static long ROOMPUSH_SCHEDULE = 100;
	public static long ROOMSTATE_SCHEDULE = 30000;
	public static long ROOMALLOC_SCHEDULE = 1*60*1000;
	public static long ROOMSTATE_IDLE = 1*60*1000; 
	public static long ROOMSTATE_DESTROYED = 30*1000;
	public static long ROOMSTATE_WAITING_PLAYER = 1*60*1000;
	public static long ROOMSTATE_WAITING_GAME = 30*1000;
	public static long LOBBYUSER_IDLE = 24*60*60*1000;
	public static long LOBBYCLEAN_SCHEDULE = 60*60*1000;
	public static long BUYPENDING_SCHEDULE = 30*1000;
	public static long GC_SCHEDULE = 30000;
	public static long MONITOR_SCHEDULE = 30000; 
	public static long GAMEDATA_SCHEDULE = 1000; 
	public static long GAMEINIT_SCHEDULE = 1000; 
	
	public static long BOT_CONNECT_SCHEDULE = 30000;
	public static long BOT_AUTOJOIN_SCHEDULE = 5000;

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
	
	public static String VIP_ID = "25_20170210";
	public static String EVENTLIST_ID = "22_20170210";
	public static String SHOPLIST_ID = "21_20170210";
	public static String LEVELEXP_ID = "26_20170301";
	public static String UNLOCK_LOBBY_ID = "30_20170210";
	
	public static boolean IS_STOPPING = false;
	public static boolean IS_STARTING = true;
	public static String SHUTDOWN_MSG = "Hệ thống bắt đầu bảo trì sau ít phút, vui lòng thoát game và nghỉ ngơi chút nhé các bạn.";
	
}
