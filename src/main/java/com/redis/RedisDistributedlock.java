package com.redis;

import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月15日 上午10:54:33
 */
public class RedisDistributedlock {

    private static final String requestId = UUID.randomUUID().toString();
    
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    
    public static void main(String[] args) {
        
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(5);
        config.setMaxWaitMillis(1000L);
        config.setTestOnBorrow(false);
        
        JedisPool jedisPool = new JedisPool(config, "192.168.56.101" ,6379);
        
        Jedis jedis = jedisPool.getResource();
        
        System.out.println("Set requestId : " + requestId);
        jedis.set("redislock", requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, 1000L);
        
        
        String getRequestId = jedis.get("redislock");
        System.out.println("Get requestId : " + getRequestId);
        
        jedisPool.close();
    }

}
