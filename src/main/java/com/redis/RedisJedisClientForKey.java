package com.redis;

import java.util.Iterator;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 对Key的一些操作
 * @author haiswang
 *
 */
public class RedisJedisClientForKey {
	
	public static void main(String[] args) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(5);
		config.setMaxWaitMillis(1000L);
		config.setTestOnBorrow(false);
		
		JedisPool jedisPool = new JedisPool(config, "192.168.56.101" ,6379);
		Jedis jedis = jedisPool.getResource();
		
		System.out.println("清空库中所有的数据:" + jedis.flushDB());
		System.out.println("新增name,haiswang键值对:" + jedis.set("name", "haiswang"));
		System.out.println("判断key:name是否存在:" + jedis.exists("name"));
		System.out.println("新增age,27键值对:" + jedis.set("age", "27"));
		System.out.println("系统中所有的键值如下:");
		
		Set<String> keys = jedis.keys("*");
		Iterator<String> iter = keys.iterator();
		while(iter.hasNext())
		{
			String key = iter.next();
			System.out.println(key);
		}
		
		System.out.println("系统中删除key:age:" + jedis.del("age"));
		System.out.println("判断key:age是否还存在:" + jedis.exists("age"));
		System.out.println("设置key:name的过期时间是5s:" + jedis.expire("name", 5));
		
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		/**
		 * 如果永久存储或者不存在都返回-1
		 */
		System.out.println("查看key:name的剩余生存时间:" + jedis.ttl("name"));
		System.out.println("移除key:name的剩余生存时间:" + jedis.persist("name"));
		System.out.println("查看key:name的剩余生存时间:" + jedis.ttl("name"));
		System.out.println("查看key所存储的值的类型:" + jedis.type("name"));
		
		//VIP添加
		
		
		/**
		 * 关闭连接池
		 */
		jedisPool.close();
	}
	
}
