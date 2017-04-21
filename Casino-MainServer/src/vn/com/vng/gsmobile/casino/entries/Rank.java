package vn.com.vng.gsmobile.casino.entries;

import vn.com.vng.gsmobile.casino.flatbuffers.RankType;
import vn.com.vng.gsmobile.casino.ulti.Lib;

public class Rank {
	public static final String PREFIX = "RANK";
	
	public static void add(String uid, double value,byte ranktype){
		String keyRank = Rank.PREFIX+":"+RankType.name(ranktype);
		Lib.getRedisGame(false).getRedisConnection().zadd(keyRank, (double) value, uid);		
	}
	
}
