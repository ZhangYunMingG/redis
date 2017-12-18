package com.redis;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisJedisClientForSetValue {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(5);
		config.setMaxWaitMillis(1000L);
		config.setTestOnBorrow(false);
		
		JedisPool jedisPool = new JedisPool(config, "10.249.73.145" ,6379);
		Jedis jedis = jedisPool.getResource();
		
		jedis.sadd("111", "1", "2");
		jedis.sadd("222", "1", "2" ,"3");
		
		Set<String> set = jedis.sdiff("222" ,"111");
		for (String str : set) {
			System.out.println(str);
		}
		
		System.out.println("over...");
	}

}
