package vn.com.vng.gsmobile.casino.entries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.channel.Channel;
import vn.com.vng.gsmobile.casino.business.Service;
import vn.com.vng.gsmobile.casino.flatbuffers.EventInfo;
import vn.com.vng.gsmobile.casino.flatbuffers.EventList;
import vn.com.vng.gsmobile.casino.flatbuffers.EventType;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;
import vn.com.vng.gsmobile.casino.ulti.LocalCache;

public class Event {
	public static final String EVENT_TABLENAME = "22_";
	public static final String EVENT_LIST = "event_list";
	public static final String EVENT_ID = "event_id";
	public static final String EVENT_TYPE = "event_type";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String ORDER = "order";
	public static final String URL_FANPAGE = "url_fanpage";
	public static final String URL_BANNER = "url_banner";
	public static final String URL_BANNER_TINY = "url_banner_tiny";
	public static final String BANNER_START_TIME = "banner_start_time";
	public static final String BANNER_END_TIME = "banner_end_time";
	public static final String START_TIME = "start_time";
	public static final String END_TIME = "end_time";
	public static final String POLICY = "policy";	
	public static final String POLICY_SHOP = "shop";	
	public static final String POLICY_SHOP_PROMO = "promo";	
	public static final String POLICY_SHOP_DISCOUNT = "discount";	
	public static final String POLICY_RANK = "rank";	
	public static final String POLICY_COND_LIST = "cond_list";	
	public static final String CREATE_DATE = "create_date";
	public static final String CREATE_BY = "create_by";
	
	private static JsonObject database = null;
	public synchronized static JsonObject getEventBase(){
		if(database==null){
			database = (JsonObject) LocalCache.get(Const.EVENTLIST_ID);
		}
		return database;
	}
	
	@SuppressWarnings("unchecked")
	public static EventList getEventList(){
		String key = "fbs_"+Const.EVENTLIST_ID;
		EventList value = (EventList) LocalCache.get(key);
		if(value == null){
			JsonObject jo = getEventBase();
			if(jo != null){
				FlatBufferBuilder builder  = new FlatBufferBuilder(0);
				List<Integer> l = new ArrayList<>();
				Iterator<Object> it = jo.getObject(Event.EVENT_LIST).toMap().values().iterator();
				while(it.hasNext()){
					Map<String, Object> event = (Map<String, Object>) it.next();
					Number type = (Number) event.get(Event.EVENT_TYPE);
					Number id = (Number) event.get(Event.EVENT_ID);
					Number starttime = (Number) event.get(Event.START_TIME);
					Number endtime = (Number) event.get(Event.END_TIME);
					Number bannerstarttime = (Number) event.get(Event.BANNER_START_TIME);
					Number bannerendtime = (Number) event.get(Event.BANNER_END_TIME);
					String name = (String) event.get(Event.NAME);
					String desc = (String) event.get(Event.DESCRIPTION);
					String fanpage = (String) event.get(Event.URL_FANPAGE);
					String banner = (String) event.get(Event.URL_BANNER);
					String bannertiny = (String) event.get(Event.URL_BANNER_TINY);
					List<Byte> rank = new ArrayList<>();
					if(type.byteValue()==EventType.Event_Rank){
						List<Number> r = (List<Number>) ((Map<String, Object>)event.get(Event.POLICY)).get(Event.POLICY_RANK);
						for(Number n : r){
							rank.add(n.byteValue());
						}
					}
					l.add(EventInfo.createEventInfo(builder, 
							id.longValue(), 
							type.intValue(), 
							name==null?0:builder.createString(name), 
							starttime.longValue(), 
							endtime.longValue(), 
							bannerstarttime.longValue(), 
							bannerendtime.longValue(), 							
							0, 
							0, 
							desc==null?0:builder.createString(desc), 
							fanpage==null?0:builder.createString(fanpage), 
							banner==null?0:builder.createString(banner),
							bannertiny==null?0:builder.createString(bannertiny),
							rank==null?0:EventInfo.createRankVector(builder, ArrayUtils.toPrimitive(rank.toArray(new Byte[rank.size()])))
						));
				}
				int iel = EventList.createEventList(builder, 
						0, 
						EventList.createEventsVector(builder, ArrayUtils.toPrimitive(l.toArray(new Integer[l.size()])))
					);
				builder.finish(iel);
				value = EventList.getRootAsEventList(builder.dataBuffer());
				LocalCache.put(key, value);			}
		}
		return value;
	}

	public static void pushToClient(){
		byte bKq = ErrorCode.UNKNOWN;
		EventList value = getEventList();
		if(value != null){
			bKq = ErrorCode.OK;
			List<Channel> lc = new ArrayList<>();
			lc.addAll(Handshake.gameWebsocket.getChannels().values());
			lc.addAll(Handshake.mainWebsocket.getChannels().values());
			Service.sendToClient(
				Event.class.getSimpleName(), 
				Const.SERVER_HOST+"_"+System.currentTimeMillis(), Service.CMDTYPE_REQUEST, 
				lc, 
				Arrays.asList(CMD.EVENT_LIST.cmd,CMD.EVENT_LIST.subcmd,CMD.EVENT_LIST.version,bKq,value)
			);
		}
		else
			bKq = ErrorCode.NOTEXISTS;
	}
}
