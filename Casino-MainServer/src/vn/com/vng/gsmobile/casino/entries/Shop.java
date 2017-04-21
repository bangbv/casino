package vn.com.vng.gsmobile.casino.entries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.business.Service;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDShopList;
import vn.com.vng.gsmobile.casino.flatbuffers.EventType;
import vn.com.vng.gsmobile.casino.flatbuffers.ItemCostType;
import vn.com.vng.gsmobile.casino.flatbuffers.ItemType;
import vn.com.vng.gsmobile.casino.flatbuffers.RankType;
import vn.com.vng.gsmobile.casino.flatbuffers.ShopItem;
import vn.com.vng.gsmobile.casino.flatbuffers.UserInfo;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.Lib;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class Shop {
	public static final String SHOP_TABLENAME = "21_";
	public static final String SHOP_LIST = "shop_list";
	public static final String SHOP_ID = "shop_id";
	public static final String ITEM_VALUE_BASE = "item_value_base";
	public static final String ITEM_VALUE = "item_value";
	public static final String ITEM_TYPE = "item_type";
	public static final String COST_TYPE = "cost_type";
	public static final String COST_VALUE_BASE = "cost_value_base";
	public static final String COST_VALUE = "cost_value";
	public static final String COST_SHOW = "cost_show";
	public static final String PROMO = "promo";
	public static final String DISCOUNT = "discount";
	public static final String EVENT_ID = "event_id";
	public static final String EVENT_DESCIPTION = "event_description";	
	public static final String STAR = "star";	
	public static final String STATE = "state";	
	public static final String PAYMENT_SKU = "payment_sku";	
	public static final String GOOGLE_SKU = "google_sku";	
	public static final String APPLE_SKU = "apple_sku";
	public static final String COND_LIST = "cond_list";
	
	private static JsonObject database = null;
	public synchronized static JsonObject getShopBase(){
		if(database==null){
			database = (JsonObject) LocalCache.get(Const.SHOPLIST_ID);
		}
		return database;
	}
	
	@SuppressWarnings("unchecked")
	public static <Any> Any getPolicyShop(String... strings){
		Object o = null;
		String key = null;
		String uid = null;
		String itemid = null;
		if(strings!=null){
			if(strings.length>0){
				uid = strings[0];
				key = "PolicyShop:"+uid;
			}
			if(strings.length>1)
				itemid = strings[1];
		}
		Map<String, TreeMap<Integer, Map<String, Object>>> mPolicyShop = (Map<String, TreeMap<Integer, Map<String, Object>>>) LocalCache.get(key);
		if(mPolicyShop==null){
			o = buildPolicyShop(strings);
		}else{
			if(itemid==null)
				o = mPolicyShop;
			else
				o = mPolicyShop.get(itemid);
		}
		return (Any) o;
	}
	
	@SuppressWarnings("unchecked")
	public static <Any> Any buildPolicyShop(String... strings){
		Object o = null;
		String key = null;
		String uid = null;
		String itemid = null;
		if(strings!=null){
			if(strings.length>0){
				uid = strings[0];
				key = "PolicyShop:"+uid;
			}
			if(strings.length>1)
				itemid = strings[1];
		}
		Map<String, TreeMap<Integer, Map<String, Object>>> mPolicyShop = new HashMap<>();
		if(uid!=null){
			//1. lấy danh sách promo event của user
			JsonObject joEventList = Event.getEventBase();
			//2. kiểm tra điều kiện hưởng promo của user
			if(joEventList!=null){
				Iterator<Object> itEventList = joEventList.getObject(Event.EVENT_LIST).toMap().values().iterator();
				while(itEventList.hasNext()){
					Map<String, Object> event = (Map<String, Object>) itEventList.next();
					Number type = (Number) event.get(Event.EVENT_TYPE);
					Number start_time = (Number) event.get(Event.START_TIME);
					Number end_time = (Number) event.get(Event.END_TIME);
					long current_time = System.currentTimeMillis();
					if(type.byteValue() == EventType.Event_Buy && current_time >= start_time.longValue() && current_time <= end_time.longValue()){
						Map<String, Object> policy = (Map<String, Object>)  event.get(Event.POLICY);
						if(policy != null){
							List<Map<String, Object>> cond_list = (List<Map<String, Object>>) policy.get(Event.POLICY_COND_LIST);
							if(Condition.valid(Long.parseLong(uid), cond_list)){
								Number order = (Number) event.get(Event.ORDER);
								Map<String, Map<String, Object>> policy_shop = (Map<String, Map<String, Object>>)  policy.get(Event.POLICY_SHOP);
								if(policy_shop!=null){
									Iterator<Entry<String, Map<String, Object>>> itPolicyShop = policy_shop.entrySet().iterator();
									while(itPolicyShop.hasNext()){
										Entry<String, Map<String, Object>> item = itPolicyShop.next();
										//kiểm tra điều kiện từng item
										List<Map<String, Object>> item_cond_list = (List<Map<String, Object>>) item.getValue().get(Event.POLICY_COND_LIST);
										if(Condition.valid(Long.parseLong(uid), item_cond_list)){
											if(!mPolicyShop.containsKey(item.getKey())){
												mPolicyShop.put(item.getKey(), new TreeMap<Integer, Map<String, Object>>());
											}
											TreeMap<Integer, Map<String, Object>> mPolicy = mPolicyShop.get(item.getKey());
											Map<String, Object> item_value = item.getValue();
											item_value.put(Event.EVENT_ID, event.get(Event.EVENT_ID));
											item_value.put(Event.NAME, event.get(Event.NAME));
											mPolicy.put(order.intValue(), item_value);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		LocalCache.put(key, mPolicyShop);
		if(itemid==null)
			o = mPolicyShop;
		else
			o = mPolicyShop.get(itemid);
		return (Any) o;
	}
	@SuppressWarnings("unchecked")
	public static CMDShopList buildShopList(String... userId){
		String uid = userId!=null && userId.length>0?userId[0]:null;
		Long luid = userId!=null && userId.length>0?NumberUtils.createLong(userId[0]):0l;
		String key = Const.SHOPLIST_ID+":"+(uid!=null?uid:"BASE");
		CMDShopList value = null;
		Map<String, TreeMap<Integer, Map<String, Object>>> mPolicyShop = buildPolicyShop(uid);
		JsonObject joShop = getShopBase();
		if(joShop!=null){
			FlatBufferBuilder builder  = new FlatBufferBuilder(0);
			List<Integer> l = new ArrayList<>();
			Iterator<Object> it = joShop.getObject(Shop.SHOP_LIST).toMap().values().iterator();
			while(it.hasNext()){
				Map<String, Object> item = (Map<String, Object>) it.next();
				Number shop_id = (Number) item.get(Shop.SHOP_ID);
				Number item_type = (Number) item.get(Shop.ITEM_TYPE);
				Number item_value_base = (Number) item.get(Shop.ITEM_VALUE_BASE);
				Number cost_type = (Number) item.get(Shop.COST_TYPE);
				Number cost_value_base = (Number) item.get(Shop.COST_VALUE_BASE);
				Number cost_show = (Number) item.get(Shop.COST_SHOW);
				Number star = (Number) item.get(Shop.STAR);
				Number state = (Number) item.get(Shop.STATE);	
				String payment_sku = (String) item.get(Shop.PAYMENT_SKU);
				String goole_sku = (String) item.get(Shop.GOOGLE_SKU);
				String apple_sku = (String) item.get(Shop.APPLE_SKU);
				List<Map<String, Object>> cond_list = (List<Map<String, Object>>) item.get(COND_LIST);
				if(Condition.valid(luid, cond_list)){
					Number event_id = 0;
					Number event_promo = 0;
					Number event_discount = 0;
					String event_description = null;
					TreeMap<Integer, Map<String, Object>> mPolicy = mPolicyShop.get(shop_id.toString());
					if(mPolicy!=null){
						Entry<Integer, Map<String, Object>> e = mPolicy.firstEntry();
						if(e !=null){
							Map<String, Object> policy = e.getValue();
							event_id = (Number) policy.get(Event.EVENT_ID);
							event_promo = (Number) policy.get(Event.POLICY_SHOP_PROMO);
							event_discount = (Number) policy.get(Event.POLICY_SHOP_DISCOUNT);
							event_description = (String) policy.get(Event.NAME);
							event_description = (event_description==null?"":event_description)
												+ (event_promo.intValue()>0?" +"+event_promo.intValue()+"%":"")
												+ (event_discount.intValue()>0?" -"+event_discount.intValue()+"%":"");
						}
					}
					l.add(ShopItem.createShopItem(builder, 
							shop_id.longValue(), 
							item_value_base.longValue(), 
							item_value_base.longValue()+(event_promo.intValue()*item_value_base.longValue()/100), 
							item_type.intValue(), 
							cost_type.intValue(), 
							cost_value_base.longValue(), 
							cost_value_base.longValue()-(event_discount.intValue()*cost_value_base.longValue()/100), 
							cost_show.longValue()-(event_discount.intValue()*cost_show.longValue()/100),
							0, 
							event_promo.intValue(), 
							event_discount.intValue(), 
							event_id.longValue(), 
							event_description==null?0:builder.createString(event_description), 
							star.intValue(), 
							state.intValue(),
							payment_sku==null?0:builder.createString(payment_sku), 
							goole_sku==null?0:builder.createString(goole_sku), 
							apple_sku==null?0:builder.createString(apple_sku)
						)
					);
				}
			}
			int isl = CMDShopList.createCMDShopList(builder, 
					uid==null?0:luid,
					0, 
					CMDShopList.createListItemVector(builder, ArrayUtils.toPrimitive(l.toArray(new Integer[l.size()])))
				);
			builder.finish(isl);
			value = CMDShopList.getRootAsCMDShopList(builder.dataBuffer());
			LocalCache.put(key, value);
		}
		return value;
	}
	public static CMDShopList getShopList(String... userId){
		String uid = userId!=null&&userId.length>0?userId[0]:null;
		String key = Const.SHOPLIST_ID+":"+(uid!=null?uid:"BASE");
		CMDShopList value = (CMDShopList) LocalCache.get(key);
		if(value == null)
			value = buildShopList(userId);
		return value;
	}
	public static void pushToClient(){
		byte bKq = ErrorCode.UNKNOWN;
		CMDShopList value = getShopList();
		if(value != null){
			bKq = ErrorCode.OK;
			List<Channel> lc = new ArrayList<>();
			lc.addAll(Handshake.gameWebsocket.getChannels().values());
			lc.addAll(Handshake.mainWebsocket.getChannels().values());
			Service.sendToClient(
				Shop.class.getSimpleName(), 
				Const.SERVER_HOST+"_"+System.currentTimeMillis(), Service.CMDTYPE_REQUEST, 
				lc, 
				Arrays.asList(CMD.SHOP_LIST.cmd,CMD.SHOP_LIST.subcmd,CMD.SHOP_LIST.version,bKq,value)
			);
		}
		else
			bKq = ErrorCode.NOTEXISTS;
	}
	
	public static byte buy(long userId, Number itemId, String payId, String sTrid, Channel channel){
		byte bKq = ErrorCode.UNKNOWN;
		if(channel==null) channel = Handshake.getChannel(userId);
		Number cost_type = null;
		if(userId > 0 && itemId.intValue()!=0){
			String uid = String.format("%d", userId);
			//1. Lấy thông tin user và item and promo
			JsonObject user = null;
			List<?> l = Lib.getDBGame(false).getCBConnection().get(User.USERESOURCE_TABLENAME+userId);
			if((boolean) l.get(0) && l.get(1)!=null){
				JsonObject rsc = ((JsonDocument)l.get(1)).content();
				JsonObject item = Shop.getShopBase().getObject(Shop.SHOP_LIST).getObject(itemId.toString());
				TreeMap<Integer, Map<String, Object>> policy_list = Shop.getPolicyShop(uid, itemId.toString());
				Map<String, Object> policy = null;
				if(policy_list!=null){
					Entry<Integer, Map<String, Object>> e = policy_list.firstEntry();
					if(e!=null)
						policy = e.getValue();
				}
				//2 Kiểm tra đủ tiền 
				Number discount = 0;
				if(policy!=null){
					discount = (Number) policy.get(Shop.DISCOUNT);
				}
				long buy_value = item.getLong(Shop.COST_VALUE) - discount.longValue()*item.getLong(Shop.COST_VALUE)/100;
				if(buy_value<0) buy_value = 0;
				long have_value = 0;
				cost_type = item.getInt(Shop.COST_TYPE);
				switch(cost_type.byteValue()){
				case ItemCostType.Cost_Cash_Gateway:
				case ItemCostType.Cost_Cash_SMS:
				case ItemCostType.Cost_Cash_Card:
				case ItemCostType.Cost_Cash_IAP:		
					have_value = rsc.getLong(User.CASH);
					break;
				case ItemCostType.Cost_Coin:
					have_value = rsc.getLong(User.COIN);
					break;
				case ItemCostType.Cost_Star:
					have_value = rsc.getLong(User.STAR);
					break;
				case ItemCostType.Cost_Free:
					have_value = 0;
					buy_value = 0;
					break;
				}
				//3. Trừ tiền, nhận hàng và build lại shoplist cho user
				User u = new User(userId);
				List<List<?>> conds = new ArrayList<>();
				if(have_value>=buy_value){
					//trừ tiền
					switch(cost_type.byteValue()){
					case ItemCostType.Cost_Cash_Gateway:
					case ItemCostType.Cost_Cash_SMS:
					case ItemCostType.Cost_Cash_Card:
					case ItemCostType.Cost_Cash_IAP:		
						rsc.put(User.CASH, have_value-buy_value);
						break;
					case ItemCostType.Cost_Coin:
						rsc.put(User.COIN, have_value-buy_value);
						break;
					case ItemCostType.Cost_Star:
						rsc.put(User.STAR, have_value-buy_value);
						break;
					}
					//nhận hàng
					JsonObject joVip = null;
					int item_type = item.getInt(Shop.ITEM_TYPE);
					switch(item_type){
					case ItemType.Item_Coin:
						Number event_promo = 0;
						if(policy!=null){
							event_promo = (Number) policy.get(Shop.PROMO);
						}
						Number vip_promo = 0;
						user = (JsonObject) LocalCache.get(User.USER_TABLENAME+userId);
						if(user!=null){
							Number vip_type = user.getInt(User.VIP);
							Number vip_expired = user.getInt(User.VIP_EXPIRE);
							if(vip_type!=null && vip_type.intValue() > 0 
							&& vip_expired!=null && System.currentTimeMillis() < vip_expired.longValue()){
								joVip = Vip.getVipBase().getObject(Vip.VIP_LIST).getObject(vip_type.toString());
								if(joVip!=null)
									vip_promo = joVip.getInt(Vip.BUY_COIN_PROMO);
							}
						}
						long item_value_base =  item.getLong(Shop.ITEM_VALUE_BASE);
						long item_value = item_value_base + (event_promo.intValue()+vip_promo.intValue())*item_value_base/100;
						Long have_coin = rsc.getLong(User.COIN);
						have_coin = have_coin + item_value;
						rsc.put(User.COIN, have_coin);
						Integer item_star = item.getInt(Shop.STAR);
						if(item_star!=null && item_star > 0){
							int have_star = rsc.getInt(User.STAR);
							rsc.put(User.STAR, have_star+item_star);
						}
						Lib.getDBGame(false).getCBConnection().upsert(JsonDocument.create(User.USERESOURCE_TABLENAME+userId, rsc));
						LocalCache.put(User.USERESOURCE_TABLENAME+userId, rsc);
						//cap nhat rank dai gia tai day
						Rank.add(uid,have_coin.doubleValue(),RankType.RankGlobalMoney);
						//add condition
						conds.add(Arrays.asList(User.COND_COIN_WIN_TOTAL, item_value, CondUpdateType.Increase));
						conds.add(Arrays.asList(User.COND_DAILY_COIN_WIN, item_value, CondUpdateType.Increase));
						conds.add(Arrays.asList(User.COND_DAILY_COIN_BALANCE, item_value, CondUpdateType.Increase));
						break;
					case ItemType.Item_VIP:
						List<?> us = Lib.getDBGame(false).getCBConnection().get(User.USER_TABLENAME+userId);
						if((boolean)us.get(0) && us.get(1)!=null){
							user = ((JsonDocument) us.get(1)).content();
							Number buy_vip_type =  item.getInt(Shop.ITEM_VALUE_BASE);
							joVip = Vip.getVipBase().getObject(Vip.VIP_LIST).getObject(buy_vip_type.toString());
							Long coin = joVip.getLong(Vip.VALUE_IMME);
							Long coin0 = rsc.getLong(User.COIN);
							if(coin0!=null){
								rsc.put(User.COIN, coin0 + coin);
							}
							Number days =  joVip.getInt(Vip.DAYS);
							Long vip_expire = Lib.ConvertDateToLong(new Date(), days==null?0:days.intValue());
							user.put(User.VIP, buy_vip_type.intValue());
							user.put(User.VIP_EXPIRE, vip_expire);
							Lib.getDBGame(false).getCBConnection().upsert(JsonDocument.create(User.USER_TABLENAME+userId, user));
							LocalCache.put(User.USER_TABLENAME+userId, user);
							Lib.getDBGame(false).getCBConnection().upsert(JsonDocument.create(User.USERESOURCE_TABLENAME+userId, rsc));
							LocalCache.put(User.USERESOURCE_TABLENAME+userId, rsc);
							if(coin0!=null){
								//cap nhat rank dai gia tai day
								double d = coin0 + coin;
								Rank.add(uid,d,RankType.RankGlobalMoney);
								//add condition
								conds.add(Arrays.asList(User.COND_COIN_WIN_TOTAL, coin, CondUpdateType.Increase));
								conds.add(Arrays.asList(User.COND_DAILY_COIN_WIN, coin, CondUpdateType.Increase));
								conds.add(Arrays.asList(User.COND_DAILY_COIN_BALANCE, coin, CondUpdateType.Increase));
							}
						}
						break;						
					}
					//Cập nhật thông tin cho user
					UserInfo ui = new User(userId).toUserInfo();
					if(ui!=null){
						Service.sendToClient(
								Shop.class.getSimpleName()+".buy", 
								sTrid, Service.CMDTYPE_REQUEST, 
								Arrays.asList(channel),
								Arrays.asList(CMD.MY_INFO.cmd,CMD.MY_INFO.subcmd,CMD.MY_INFO.version,(byte)0,ui)							
							);
					}
					//Cập nhật bảng userCondition
					switch(item_type){
					case ItemType.Item_Coin:
						conds.add(Arrays.asList(User.COND_CASH_BUY_CNT, 1, CondUpdateType.Increase));
						break;
					case ItemType.Item_VIP:
						conds.add(Arrays.asList(User.COND_STAR_BUY_CNT, 1, CondUpdateType.Increase));
						break;
					}
					conds.add(Arrays.asList(User.COND_TOTAL_BUY_CNT, 1, CondUpdateType.Increase));
					conds.add(Arrays.asList(String.format(User.PATTERN_COND, itemId.toString(), User.COND_ITEM_BUY_CNT), 1, CondUpdateType.Increase));
					u.setConditionValue(conds);
					//build lại shoplist
					CMDShopList sl = Shop.buildShopList(uid);
					if(sl!=null){
						Service.sendToClient(
								Shop.class.getSimpleName()+".buy",  
								sTrid, Service.CMDTYPE_REQUEST, 
								Arrays.asList(channel),
								Arrays.asList(CMD.SHOP_LIST.cmd,CMD.SHOP_LIST.subcmd,CMD.SHOP_LIST.version,(byte)0,sl)							
							);
					}
					bKq = ErrorCode.OK;
				}
				else
					bKq = ErrorCode.MONEY_NOTENOUGH;
			}
			else
				bKq = ErrorCode.NOTEXISTS;
		}
		else
			bKq = ErrorCode.NOTEXISTS;
		if(payId!=null && cost_type!=null && cost_type.byteValue()>=ItemCostType.Cost_Cash_Gateway){
			JsonDocument doc = new BuyPending(userId, itemId.longValue(), payId).toJsonDocument();
			List<?> l = Lib.getDBGame(false).getCBConnection().exists(doc.id());
			boolean pendingExists = false;
			if((boolean)l.get(0) && !(boolean)l.get(1)){
				pendingExists = true;
			}
			//Nếu chưa nhận được tiền nạp (và chưa đánh dấu) thì đánh dấu pending
			if(bKq == ErrorCode.MONEY_NOTENOUGH && !pendingExists)
			{
				Lib.getDBGame(false).getCBConnection().insert(doc);
				BuyPending.add(payId);
			}
			else if(pendingExists){//đánh dấu kết quả xử lý pending
				if(bKq == ErrorCode.MONEY_NOTENOUGH)
					doc.content().put(BuyPending.STATUS, BuyStatusType.NotEnoughCash);
				else if(bKq == ErrorCode.OK)
					doc.content().put(BuyPending.STATUS, BuyStatusType.Finish);
				Lib.getDBGame(false).getCBConnection().upsert(doc);
			}
		}
		return bKq;
	}
}
