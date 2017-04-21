package vn.com.vng.gsmobile.casino.entries;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.collections4.MapUtils;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import vn.com.vng.gsmobile.casino.ulti.Const;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class LevelExp {
	public static final String LEVELEXP_TABLENAME = "26_";
	public static final String LEVEL_EXP = "level_exp";
	public static final String LEVEL = "l";
	public static final String EXP = "e";
	
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
					database.put(le.getInt(LEVEL), le.getInt(EXP));
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
	
	public static List<Integer> getNewLevelExp(Integer level, Integer exp){
		Integer new_level = level;
		Integer new_exp = exp;
		if(database==null)
			buildLevelBase();
		if(database!=null){
			Integer exp_of_level = null;
			do{
				exp_of_level = database.get(new_level);
				if(exp_of_level==null) exp_of_level = Integer.MAX_VALUE;
				if(new_exp >= exp_of_level){
					new_level += 1;
					new_exp -= exp_of_level;
				}
				else
					break;
			}while(true);
		}
		return Arrays.asList(new_level, new_exp);
	}
	private static Integer LEVEL_EXP_SPLIT = 100000;
	public static Integer getSort(List<Integer> new_level_exp) {
		// TODO Auto-generated method stub
		return new_level_exp.get(0)*LEVEL_EXP_SPLIT+new_level_exp.get(1);
	}
	public static List<Integer> getLevelExp(Integer sort_value) {
		// TODO Auto-generated method stub
		Integer level = sort_value/LEVEL_EXP_SPLIT;
		Integer exp = sort_value%LEVEL_EXP_SPLIT;
		return Arrays.asList(level, exp);
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
