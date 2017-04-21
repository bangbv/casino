package vn.com.vng.gsmobile.casino.entries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;
import vn.com.vng.gsmobile.casino.flatbuffers.ListUserInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.LobbyType;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfoDetail;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class User {
	public static final String USER_TABLENAME = "1_";
	public static final String USERESOURCE_TABLENAME = "2_";
	public static final String USERCOND_TABLENAME = "3_";
	public static final String USERSOCIAL_TABLENAME = "5_";
	public static final String UID = "uid";
	public static final String NAME = "name";
	public static final String AVATAR = "avatar";
	public static final String ACCTYPE = "accType";
	public static final String COIN = "coin";
	public static final String STAR = "star";	
	public static final String CASH = "cash";
	public static final String LEVEL = "level";
	public static final String EXP = "exp";
	public static final String WIN = "winCount";
	public static final String LOSE = "loseCount";
	public static final String ITEMS = "items";
	public static final String VIP = "vip";
	public static final String VIP_EXPIRE = "vipExpire";
	public static final String LOCATION = "location";
	public static final String LASTLOGIN = "lastLogin";
	public static final String SECKEY = "sessionKey";
	public static final String CREATEDATE = "createDate";
	public static final String STATUS = "status";
	public static final String GIFT_LIST = "giftList";
	public static final String GIFT_ID = "giftId";
	public static final String CONSECUTIVE_DAY = "consecutiveDay";
	public static final String DATE = "date";
	
	//user condition
	//social
	public static final String COND_TOTAL_INVITE = "total_invite";	
	public static final String COND_TOTAL_SHARE = "total_share";	
	//buy
	public static final String COND_TOTAL_BUY_CNT = "total_buy_cnt";	
	public static final String COND_COIN_BUY_CNT = "coin_buy_cnt";
	public static final String COND_CASH_BUY_CNT = "cash_buy_cnt";
	public static final String COND_STAR_BUY_CNT = "star_buy_cnt";
	public static final String COND_ITEM_BUY_CNT = "_buy_cnt";
	//play - total
	public static final String COND_PLAY_TIME = "play_time";
	public static final String COND_CONSECUTIVE_WINS_CNT = "cwins_cnt";
	public static final String COND_CONSECUTIVE_LOSS_CNT = "closs_cnt";
	public static final String COND_COIN_WIN_MAX = "coin_win_max";
	public static final String COND_COIN_WIN_TOTAL = "coin_win";
	public static final String COND_COIN_LOSE_TOTAL = "coin_lose";
	public static final String COND_CHALLENGER = "challenger";
	public static final String COND_LOTTERY_CNT = "lott_cnt";
	//play - a game
	public static final String COND_GAME_PLAY_TIME = "_play_time";
	public static final String COND_GAME_WIN_CNT = "_win_cnt";
	public static final String COND_GAME_LOSE_CNT = "_lose_cnt";
	public static final String COND_GAME_CONSECUTIVE_WINS_CNT = "_cwins_cnt";
	public static final String COND_GAME_CONSECUTIVE_LOSS_CNT = "_closs_cnt";
	public static final String COND_GAME_LOBBY_UNLOCK = "_lobby_type";
	public static final String COND_GAME_LOBBY_SUGGEST = "_lobby_suggest";
	//play - daily
	public static final String TODAY = "today";
	public static final String COND_DAILY_INVITE = "invite_daily";	
	public static final String COND_DAILY_SHARE = "share_daily";
	public static final String COND_DAILY_COIN_WIN = "coin_win_daily";//Tiền thắng và các loại quà tặng
	public static final String COND_DAILY_COIN_LOSE = "coin_lose_daily";//Tiền thua và các loại tiêu
	public static final String COND_DAILY_COIN_BALANCE = "coin_balance_daily";//Tổng thắng - thua trong ngày
	public static final String COND_DAILY_CHALLENGER = "challenger_daily";
	public static final String COND_DAILY_PLAY_TIME = "play_time_daily";
	public static final String COND_DAILY_WIN_CNT = "win_cnt_daily";
	public static final String COND_DAILY_LOSE_CNT = "lose_cnt_daily";
	public static final String COND_DAILY_CONSECUTIVE_WINS_CNT = "cwins_cnt_daily";
	public static final String COND_DAILY_CONSECUTIVE_LOSS_CNT = "closs_cnt_daily";
	public static final String COND_DAILY_GAME_PLAY_TIME = "_play_time_daily";
	public static final String COND_DAILY_GAME_WIN_CNT = "_win_cnt_daily";
	public static final String COND_DAILY_GAME_LOSE_CNT = "_lose_cnt_daily";
	public static final String COND_DAILY_GAME_CONSECUTIVE_WINS_CNT = "_cwins_cnt_daily";
	public static final String COND_DAILY_GAME_CONSECUTIVE_LOSS_CNT = "_closs_cnt_daily";
	public static final String COND_DAILY_LOTTERY_CNT = "lott_cnt_daily";
	//pattern condition
	public static final String PATTERN_COND = "%s%s";
	
	private Long uid = null;
	public User(Long uid){
		this.uid = uid;
	}
	public UserInfo toUserInfo(){
		UserInfo rs = null;
		try{
			FlatBufferBuilder builder = new FlatBufferBuilder(0);
			int ui = 0;
			JsonObject j = (JsonObject) LocalCache.get(User.USER_TABLENAME + uid);
			JsonObject j2 = (JsonObject) LocalCache.get(User.USERESOURCE_TABLENAME + uid);
			if((j != null) && (j2 != null)){
				Long vip_expired = j.getLong(User.VIP_EXPIRE);
				ui = UserInfo.createUserInfo(builder, 
						uid, 
						builder.createString(j.getString(User.NAME)), 
						builder.createString(j.getString(User.AVATAR)), 
						j.getInt(User.STATUS), 
						j.getInt(User.ACCTYPE), 
						j.getInt(User.VIP), 
						j2.getLong(User.COIN),
						j2.getInt(User.LEVEL), 
						UserInfoDetail.createUserInfoDetail(builder, 
							j2.getInt(User.EXP), 
							j2.getInt(User.WIN),
							j2.getInt(User.LOSE),
							j2.getInt(User.STAR),
							j2.getInt(User.CASH),
							getVipDaysRemaining(vip_expired)
						)
					);
				builder.finish(ui);
				rs = UserInfo.getRootAsUserInfo(builder.dataBuffer());
			}
		}catch(Exception e){
			Lib.getLogger().error(Arrays.asList(uid, Lib.getStackTrace(e), this.getClass().getSimpleName()+".toUserInfo"));
		}
		return rs;
	}
	public static ListUserInfo toListUserInfo(List<Long> userList){
		ListUserInfo rs = null;
		try{
			FlatBufferBuilder builder = new FlatBufferBuilder(0);
			List<Integer> l = new ArrayList<>();
			Iterator<Long> it = userList.iterator();
			while(it.hasNext()){
				Long uid = it.next();
				try{
					if(uid!=null && uid > 0){
						JsonObject j = (JsonObject) LocalCache.get(User.USER_TABLENAME + uid);
						JsonObject j2 = (JsonObject) LocalCache.get(User.USERESOURCE_TABLENAME + uid);
						if ((j != null) && (j2 != null)) {
							Long vip_expired = j.getLong(User.VIP_EXPIRE);
							int uiBuffer = UserInfo.createUserInfo(builder, 
									uid, 
									builder.createString(j.getString(User.NAME)), 
									builder.createString(j.getString(User.AVATAR)), 
									j.getInt(User.STATUS), 
									j.getInt(User.ACCTYPE),
									j.getInt(User.VIP), 
									j2.getLong(User.COIN), 
									j2.getInt(User.LEVEL), 
									UserInfoDetail.createUserInfoDetail(builder, 
											j2.getInt(User.EXP), 
											j2.getInt(User.WIN),
											j2.getInt(User.LOSE),
											j2.getInt(User.STAR),
											j2.getInt(User.CASH),
											getVipDaysRemaining(vip_expired)
									));
							l.add(uiBuffer);
						}
					}
				}catch(Exception e){
					Lib.getLogger().error(Arrays.asList(uid, Lib.getStackTrace(e), User.class.getSimpleName()+".toListUserInfo"));
				}
				
			}
			int iul = ListUserInfo.createListUserInfo(builder, builder.createString(""), 0,
					ListUserInfo.createListVector(builder, ArrayUtils.toPrimitive(l.toArray(new Integer[l.size()]))));
			builder.finish(iul);
			rs = ListUserInfo.getRootAsListUserInfo(builder.dataBuffer());
		}catch(Exception e){
			Lib.getLogger().error(Arrays.asList(Lib.getStackTrace(e), User.class.getSimpleName()+".toListUserInfo"));
		}
		return rs;
	}

	public Number getConditionValue(int cond_type, Object...objects) {
		// TODO Auto-generated method stub
		Number kq = 0;
		JsonObject jo = null;
		switch(cond_type){
		case CondType.CurrentTime:
			kq = System.currentTimeMillis();
			break;
		case CondType.BuyCount:
			if(objects.length>0){
				kq = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_ITEM_BUY_CNT));
			}
			break;			
		case CondType.TotalBuyCount:
			kq = getConditionValue(uid, COND_TOTAL_BUY_CNT);
			break;
		case CondType.PlayCount:
			if(objects.length>0){
				Number win = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_GAME_WIN_CNT));
				Number loss = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_GAME_LOSE_CNT));
				kq = win.intValue() + loss.intValue();
			}
			else{
				jo = (JsonObject) LocalCache.get(USERESOURCE_TABLENAME+uid);
				kq = jo.getLong(WIN) + jo.getLong(LOSE);	
			}
			break;			
		case CondType.LoseCount:
			if(objects.length>0){
				kq = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_GAME_LOSE_CNT));
			}
			else{
				jo = (JsonObject) LocalCache.get(USERESOURCE_TABLENAME+uid);
				kq = jo.getLong(LOSE);
			}
			break;			
		case CondType.WinCount:
			if(objects.length>0){
				kq = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_GAME_WIN_CNT));
			}
			else{
				jo = (JsonObject) LocalCache.get(USERESOURCE_TABLENAME+uid);
				kq = jo.getLong(WIN);
			}
			break;			
		case CondType.Level:
			jo = (JsonObject) LocalCache.get(USERESOURCE_TABLENAME+uid);
			kq = jo.getLong(LEVEL);
			break;			
		case CondType.Coin:
			jo = (JsonObject) LocalCache.get(USERESOURCE_TABLENAME+uid);
			kq = jo.getLong(COIN);
			break;			
		case CondType.Star:
			jo = (JsonObject) LocalCache.get(USERESOURCE_TABLENAME+uid);
			kq = jo.getLong(STAR);
			break;
		case CondType.Exp:
			jo = (JsonObject) LocalCache.get(USERESOURCE_TABLENAME+uid);
			kq = jo.getLong(EXP);
			break;		
		case CondType.CWinsCount:
			if(objects.length>0)
				kq = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_GAME_CONSECUTIVE_WINS_CNT));
			else
				kq = getConditionValue(uid, COND_CONSECUTIVE_WINS_CNT);
			break;
		case CondType.CLossCount:
			if(objects.length>0)
				kq = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_GAME_CONSECUTIVE_LOSS_CNT));
			else
				kq = getConditionValue(uid, COND_CONSECUTIVE_LOSS_CNT);
			break;
		case CondType.CoinWin:
			kq = getConditionValue(uid, COND_COIN_WIN_TOTAL);
			break;
		case CondType.CoinLose:
			kq = getConditionValue(uid, COND_COIN_LOSE_TOTAL);
			break;
		case CondType.CoinWinDaily:
			kq = getConditionValue(uid, COND_DAILY_COIN_WIN);
			break;
		case CondType.CoinLoseDaily:
			kq = getConditionValue(uid, COND_DAILY_COIN_LOSE);
			break;
		case CondType.CoinBalanceDaily:
			kq = getConditionValue(uid, COND_DAILY_COIN_BALANCE);
			break;
		case CondType.LobbyUnlock:
			if(objects.length>0){
				kq = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_GAME_LOBBY_UNLOCK), LobbyType.Lobby_1);
			}
			break;
		case CondType.LobbySuggest:
			if(objects.length>0){
				kq = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_GAME_LOBBY_SUGGEST), LobbyType.Lobby_2);
			}
			break;			
		case CondType.Location:
			jo = LocalCache.get(USER_TABLENAME+uid);
			kq = LocationType.value(jo.getString(LOCATION));
			break;
		case CondType.PlayTime:
			if(objects.length>0)
				kq = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_GAME_PLAY_TIME));
			else
				kq = getConditionValue(uid, COND_PLAY_TIME);
			break;
		case CondType.PlayTimeDaily:
			if(objects.length>0)
				kq = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_DAILY_GAME_PLAY_TIME));
			else
				kq = getConditionValue(uid, COND_DAILY_PLAY_TIME);
			break;	
		case CondType.CoinWinMax:
			kq = getConditionValue(uid, COND_COIN_WIN_MAX);
			break;		
		case CondType.CWinsCountDaily:
			if(objects.length>0)
				kq = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_DAILY_GAME_CONSECUTIVE_WINS_CNT));
			else
				kq = getConditionValue(uid, COND_DAILY_CONSECUTIVE_WINS_CNT);
			break;	
		case CondType.CLossCountDaily:
			if(objects.length>0)
				kq = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_DAILY_GAME_CONSECUTIVE_LOSS_CNT));
			else
				kq = getConditionValue(uid, COND_DAILY_CONSECUTIVE_LOSS_CNT);
			break;		
		case CondType.WinCountDaily:
			if(objects.length>0)
				kq = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_DAILY_GAME_WIN_CNT));
			else
				kq = getConditionValue(uid, COND_DAILY_WIN_CNT);
			break;	
		case CondType.LoseCountDaily:
			if(objects.length>0)
				kq = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_DAILY_GAME_LOSE_CNT));
			else
				kq = getConditionValue(uid, COND_DAILY_LOSE_CNT);
			break;
		case CondType.PlayCountDaily:
			if(objects.length>0){
				Number win = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_DAILY_GAME_WIN_CNT));
				Number loss = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_DAILY_GAME_LOSE_CNT));
				kq = win.intValue() + loss.intValue();
			}
			else{
				Number win = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_DAILY_WIN_CNT));
				Number loss = getConditionValue(uid, String.format(PATTERN_COND, objects[0].toString(), COND_DAILY_LOSE_CNT));
				kq = win.intValue() + loss.intValue();
			}
			break;				
		case CondType.Challenger:
			kq = getConditionValue(uid, COND_CHALLENGER);
			break;
		case CondType.ChallengerDaily:
			kq = getConditionValue(uid, COND_DAILY_CHALLENGER);
			break;		
		case CondType.Invite:
			kq = getConditionValue(uid, COND_TOTAL_INVITE);
			break;
		case CondType.Share:
			kq = getConditionValue(uid, COND_TOTAL_SHARE);
			break;		
		case CondType.InviteDaily:
			kq = getConditionValue(uid, COND_DAILY_INVITE);
			break;
		case CondType.ShareDaily:
			kq = getConditionValue(uid, COND_DAILY_SHARE);
			break;	
		case CondType.LotteryCount:
			kq = getConditionValue(uid, COND_LOTTERY_CNT);
			break;
		case CondType.LotteryCountDaily:
			kq = getConditionValue(uid, COND_DAILY_LOTTERY_CNT);
			break;				
		}
		return kq;
	}
	public void setConditionValue(List<List<?>> conds){
		if(conds==null||conds.size()==0) return;
		String tbKey = String.format("%s%d", USERCOND_TABLENAME, uid); 
		JsonObject jo = Lib.getCB(tbKey);
		if(jo!=null){
			boolean isUpdate = false;
			for(List<?> cond : conds){
				String condField = (String) cond.get(0);
				Number value = (Number) cond.get(1);
				Number uptype = cond.size()>2?(Number) cond.get(2):CondUpdateType.Upsert;
				Number upvalue = 0;
				if(value!=null && condField!=null){
					switch(condField){
					case COND_DAILY_INVITE:
					case COND_DAILY_SHARE:
					case COND_DAILY_COIN_WIN:
					case COND_DAILY_COIN_BALANCE:
					case COND_DAILY_COIN_LOSE:
					case COND_DAILY_CHALLENGER:
					case COND_DAILY_PLAY_TIME:
					case COND_DAILY_WIN_CNT:
					case COND_DAILY_LOSE_CNT:
					case COND_DAILY_CONSECUTIVE_LOSS_CNT:
					case COND_DAILY_CONSECUTIVE_WINS_CNT:
					case COND_DAILY_GAME_PLAY_TIME:
					case COND_DAILY_GAME_LOSE_CNT:
					case COND_DAILY_GAME_WIN_CNT:
					case COND_DAILY_GAME_CONSECUTIVE_LOSS_CNT:
					case COND_DAILY_GAME_CONSECUTIVE_WINS_CNT:
					case COND_DAILY_LOTTERY_CNT:
						Long today = Lib.ConvertDateToLong(new Date(), 0);
						Long lastaccessday = null;
						try{
							lastaccessday = jo.getLong(TODAY);
						}catch (Exception e) {
							// TODO: handle exception
							lastaccessday = null;
						}
						if(lastaccessday==null || lastaccessday==0 || lastaccessday < today){
							jo = Lib.getCB(tbKey);
							jo.put(TODAY, today);
							jo.put(COND_DAILY_INVITE, 0);
							jo.put(COND_DAILY_SHARE, 0);
							jo.put(COND_DAILY_COIN_WIN, 0);
							jo.put(COND_DAILY_COIN_BALANCE, 0);
							jo.put(COND_DAILY_COIN_LOSE, 0);
							jo.put(COND_DAILY_CHALLENGER, 0);
							jo.put(COND_DAILY_PLAY_TIME, 0);
							jo.put(COND_DAILY_WIN_CNT, 0);
							jo.put(COND_DAILY_LOSE_CNT, 0);
							jo.put(COND_DAILY_CONSECUTIVE_LOSS_CNT, 0);
							jo.put(COND_DAILY_CONSECUTIVE_WINS_CNT, 0);
							jo.put(COND_DAILY_GAME_PLAY_TIME, 0);
							jo.put(COND_DAILY_GAME_WIN_CNT, 0);
							jo.put(COND_DAILY_GAME_LOSE_CNT, 0);
							jo.put(COND_DAILY_GAME_CONSECUTIVE_LOSS_CNT, 0);
							jo.put(COND_DAILY_GAME_CONSECUTIVE_WINS_CNT, 0);
							jo.put(COND_DAILY_LOTTERY_CNT, 0);
						}
						break;
					}
					
					switch(uptype.byteValue()){
					case CondUpdateType.Increase:
						Number now_value = 0;
						try{
							now_value = jo.getLong(condField);
						}catch(Exception e){
							now_value = 0;
						}
						
						upvalue = value.longValue() + (now_value!=null?now_value.longValue():0);
						break;
					case CondUpdateType.Upsert:
						upvalue = value;
						break;
					}
					jo.put(condField, upvalue.longValue());
					isUpdate = true;
				}
			}
			if(isUpdate){
				Lib.getDBGame(false).getCBConnection().upsert(JsonDocument.create(tbKey, jo));
				LocalCache.put(tbKey, jo);
			}
		}
	}
	public static int getVipDaysRemaining(Long vip_expired){
		if(vip_expired== null || vip_expired == 0)
			return 0;
		int days = Lib.ConvertLongToDay(new Date(), vip_expired);
		if(days<0) days=0;
		return days;
	}
	@SuppressWarnings("unchecked")
	private static <Any> Any getConditionValue(Long uid, String condField, Object... defalutValue){
		Object kq = 0;
		String tbKey = String.format("%s%d", USERCOND_TABLENAME, uid); 
		JsonObject jo = (JsonObject) LocalCache.get(tbKey);
		JsonObject jo2 = null;
		if(jo!=null){
			//reset daily fields
			switch(condField){
			case COND_DAILY_INVITE:
			case COND_DAILY_SHARE:
			case COND_DAILY_COIN_WIN:
			case COND_DAILY_COIN_BALANCE:
			case COND_DAILY_COIN_LOSE:
			case COND_DAILY_CHALLENGER:
			case COND_DAILY_PLAY_TIME:
			case COND_DAILY_WIN_CNT:
			case COND_DAILY_LOSE_CNT:
			case COND_DAILY_CONSECUTIVE_LOSS_CNT:
			case COND_DAILY_CONSECUTIVE_WINS_CNT:
			case COND_DAILY_GAME_PLAY_TIME:
			case COND_DAILY_GAME_LOSE_CNT:
			case COND_DAILY_GAME_WIN_CNT:
			case COND_DAILY_GAME_CONSECUTIVE_LOSS_CNT:
			case COND_DAILY_GAME_CONSECUTIVE_WINS_CNT:
			case COND_DAILY_LOTTERY_CNT:
				Long today = Lib.ConvertDateToLong(new Date(), 0);
				Long lastaccessday = null;
				try{
					lastaccessday = jo.getLong(TODAY);
				}catch (Exception e) {
					// TODO: handle exception
					lastaccessday = null;
				}
				if(lastaccessday==null || lastaccessday==0 || lastaccessday < today){
					jo2 = Lib.getCB(tbKey);
					jo2.put(TODAY, today);
					jo2.put(COND_DAILY_INVITE, 0);
					jo2.put(COND_DAILY_SHARE, 0);
					jo2.put(COND_DAILY_COIN_WIN, 0);
					jo2.put(COND_DAILY_COIN_BALANCE, 0);
					jo2.put(COND_DAILY_COIN_LOSE, 0);
					jo2.put(COND_DAILY_CHALLENGER, 0);
					jo2.put(COND_DAILY_PLAY_TIME, 0);
					jo2.put(COND_DAILY_WIN_CNT, 0);
					jo2.put(COND_DAILY_LOSE_CNT, 0);
					jo2.put(COND_DAILY_CONSECUTIVE_LOSS_CNT, 0);
					jo2.put(COND_DAILY_CONSECUTIVE_WINS_CNT, 0);
					jo2.put(COND_DAILY_GAME_PLAY_TIME, 0);
					jo2.put(COND_DAILY_GAME_WIN_CNT, 0);
					jo2.put(COND_DAILY_GAME_LOSE_CNT, 0);
					jo2.put(COND_DAILY_GAME_CONSECUTIVE_LOSS_CNT, 0);
					jo2.put(COND_DAILY_GAME_CONSECUTIVE_WINS_CNT, 0);
					jo2.put(COND_DAILY_LOTTERY_CNT, 0);
				}
				break;
			}
			//get value
			try{
				kq = jo.getLong(condField);
			}catch (Exception e) {
				// TODO: handle exception
				kq = null;
			}
			//create value if not exists
			if(kq==null){
				if(defalutValue.length>0)
					kq = NumberUtils.toLong(defalutValue[0].toString());
				else
					kq = 0;
				if(jo2==null)
					jo2 = Lib.getCB(tbKey);
				jo2.put(condField, kq);
			}
		}
		//update db if have new field or reset daily fields
		if(jo2!=null){
			Lib.getDBGame(false).getCBConnection().upsert(JsonDocument.create(tbKey, jo2));
			LocalCache.put(tbKey, jo2);
		}
		return (Any) kq;
	}
}
