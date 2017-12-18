package com.redis;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class RedisClient {
	
	/**
	 * 单实例客户端
	 */
	private Jedis jedis;
	private JedisPool jedisPool;
	
	/**
	 * 基于Hash一致性算法实现的分布式Redis集群客户端
	 */
	private ShardedJedis shardedJedis;
	private ShardedJedisPool shardedJedisPool;
	
	public RedisClient()
	{
		initialPool();
		initialShardedPool();
		jedis = jedisPool.getResource();
		shardedJedis = shardedJedisPool.getResource();
	}
	
	private void initialPool()
	{
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(5);
		config.setMaxWaitMillis(1000L);
		config.setTestOnBorrow(false);
		
		jedisPool = new JedisPool(config, "10.249.72.184" ,6379);
	}
	
	private void initialShardedPool()
	{
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(5);
		config.setMaxWaitMillis(1000L);
		config.setTestOnBorrow(false);
		
		List<JedisShardInfo> shareds = new ArrayList<JedisShardInfo>();
		shareds.add(new JedisShardInfo("10.249.72.184", 6379, "master"));
		
		shardedJedisPool = new ShardedJedisPool(config ,shareds);
	}

}
