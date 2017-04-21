package vn.com.vng.gsmobile.casino.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.com.vng.gsmobile.casino.entries.CondType;
import vn.com.vng.gsmobile.casino.entries.CondUpdateType;
import vn.com.vng.gsmobile.casino.entries.User;

public class testUserCondition {
	public static void main(String[] agrs){
		
		User u = new User(73319113887399936l);
		//1. get số lần quay trong ngày
		u.getConditionValue(CondType.LotteryCountDaily);
		//2. tăng số lần quay tổng và trong ngày sau mỗi lượt quay
		List<List<?>> conds = new ArrayList<>();
		conds.add(Arrays.asList(User.COND_LOTTERY_CNT, 1, CondUpdateType.Increase));
		conds.add(Arrays.asList(User.COND_DAILY_LOTTERY_CNT, 1, CondUpdateType.Increase));
		u.setConditionValue(conds);
	}
}
