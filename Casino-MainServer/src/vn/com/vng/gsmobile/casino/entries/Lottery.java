package vn.com.vng.gsmobile.casino.entries;

import com.couchbase.client.java.document.json.JsonObject;

public class Lottery {
	public static final String LOTTERY_TABLENAME = "lotteryConfig";
	public static final String LISTITEMS = "listItem";
	public static final String ITEMID = "itemId";
	public static final String ITEMNAME = "itemName";
	public static final String COUNT = "count";
	public static final String COINRATE = "coinRate";
	public static final String STARRATE = "starRate";
	public static final String FREECNT = "freeCnt";
	public static final String COINPRICE = "coinPrice";
	public static final String STARPRICE = "starPrice";
	
	public static int caculateCoinPrice(JsonObject lo, User user) {
		int lotteryCnt = user.getConditionValue(CondType.LotteryCountDaily).intValue();
		int freeCount = lo.getInt(Lottery.FREECNT); 
		int coinPrice = lo.getInt(Lottery.COINPRICE);
		int coinRate = lo.getInt(Lottery.COINRATE);
		int totalCoin = 0;
		if(freeCount - lotteryCnt < 0){
			totalCoin = (lotteryCnt - freeCount) * coinRate + coinPrice;
		}
		return totalCoin;
	}

	public static int caculateStarPrice(JsonObject lo, User user) {
		int lotteryCnt = user.getConditionValue(CondType.LotteryCountDaily).intValue();
		int freeCount = lo.getInt(Lottery.FREECNT); 
		int starPrice = lo.getInt(Lottery.STARPRICE);
		int starRate = lo.getInt(Lottery.STARRATE);
		int totalStar = 0;
		if(freeCount - lotteryCnt < 0){
			totalStar = (lotteryCnt - freeCount) * starRate + starPrice;
		}
		return totalStar;
	}
}
