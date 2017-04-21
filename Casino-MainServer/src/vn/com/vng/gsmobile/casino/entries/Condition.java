package vn.com.vng.gsmobile.casino.entries;

import java.util.List;
import java.util.Map;

public class Condition {
	public static final String COND_TYPE = "cond_type";	
	public static final String COND_DETAIL = "cond_detail";	
	public static final String COND_MIN = "cond_value_min";	
	public static final String COND_MAX = "cond_value_max";	
	
	public static boolean valid(Long uid, List<Map<String, Object>> cond_list){
		boolean isEligible = true;
		if(uid > 0 && cond_list!=null){
			User u = new User(uid);
			for(Map<String, Object> cond : cond_list){
				Number cond_type = (Number) cond.get(COND_TYPE);
				Object cond_detail = cond.get(COND_DETAIL);
				Number cond_value_min = (Number) cond.get(COND_MIN);
				Number cond_value_max = (Number) cond.get(COND_MAX);
				Number cond_value = null;
				if(cond_detail==null)
					cond_value = u.getConditionValue(cond_type.intValue());
				else
					cond_value = u.getConditionValue(cond_type.intValue(), cond_detail);
				
				if(cond_value==null)
					isEligible = false;
				else{
					if(cond_value_min!=null && cond_value.longValue() < cond_value_min.longValue())
						isEligible = false;
					if(cond_value_max!=null && cond_value.longValue() > cond_value_max.longValue())
						isEligible = false;
				}
				if(!isEligible)
					break;
			}
		}
		return isEligible;
	}
}
