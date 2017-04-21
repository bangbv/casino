package vn.com.vng.gsmobile.casino.test;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

public class testRedisConnector {

	public static void main(String[] args) {
//		RedisConnection conn = Lib.getRedisGame(false).getRedisConnection();
//		System.out.println(conn.zrangeWithScores("KeyA",0,2));
		//System.out.println(conn.zcard("KeyA"));		
	      //Connecting to Redis server on localhost
	     // Jedis jedis = new Jedis("localhost");
		Jedis jedis = new Jedis("127.0.0.1", 6379, 60000);
	      //System.out.println("Connection to server sucessfully");
	      //set the data in redis string
	     // jedis.set("tutorial-name", "Redis tutorial");
	     // Get the stored data and print it

	    System.out.println("result:"+jedis.zadd("RANK:RankGlobalLevel",(double)101,"f123456"));
	    
	    Set<Tuple> rs = jedis.zrevrangeWithScores("RANK:RankGlobalLevel",0 ,2);	     
	     //jedis.zrevrangeWithScores(key, start, end)
	     for (Tuple tuple : rs) {
			System.out.println(tuple.getElement()+":"+tuple.getScore());
		}	    
	   //  System.out.println("Stored string in redis:: "+ jedis.get("KeyA"));
	}

}
