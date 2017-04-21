package vn.com.vng.gsmobile.casino.entries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.collections4.MapUtils;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class LevelTotalExp {
	public static final String LEVELEXP_TABLENAME = "26_";
	public static final String LEVEL_EXP = "level_exp";
	public static final String LEVEL = "level";
	public static final String EXP = "exp";
	
	private static TreeMap<Integer, Integer> database = null;
	private synchronized static TreeMap<Integer, Integer> buildLevelBase(){
		JsonObject jo = Lib.getCB(Const.LEVELEXP_ID);
		try{
			if(jo!=null){
				database = new TreeMap<>();
				JsonArray ja = jo.getArray(LEVEL_EXP);
				Iterator<Object> it = ja.iterator();
				while(it.hasNext()){
					JsonObject le = (JsonObject) it.next();
					database.put(le.getInt(EXP), le.getInt(LEVEL));
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			database = null;
		}
		return database;
	}
	public static TreeMap<Integer, Integer> getLevelBase(){
		if(database==null)
			buildLevelBase();
		return database;
	}
	
	public static Integer getLevel(Integer exp){
		Integer kq = 0;
		if(database==null)
			buildLevelBase();
		if(database!=null){
			SortedMap<Integer, Integer> sort = database.tailMap(exp);
			if(sort!=null && sort.size()>0){
				Integer exp_tmp = sort.firstKey();
				Integer level_tmp = sort.get(sort.firstKey());
				if(exp>=exp_tmp)
					kq = level_tmp;
				else
					kq = level_tmp - 1;
			}
		}
		return kq;
	}
	
	private static Map<String, Integer> mExpScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{String.format("%d", ExpType.None), 0},
				{String.format("%d", ExpType.Normal), 1},
				{String.format("%d", ExpType.Winner), 6},
				{String.format("%d", ExpType.Loser), 3},
				{String.format("%d", ExpType.Bonus), 2},
				{String.format("%d", ExpType.Penance), 1},
				{String.format("%d", ExpType.Special), 12},
		});
	public static Integer getExp(Byte expType){
		Integer kq = mExpScore.get(expType.toString());
		if(kq==null)
			kq = 0;
		return kq;
	}
}
