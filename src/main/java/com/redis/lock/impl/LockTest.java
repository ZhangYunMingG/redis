package com.redis.lock.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class LockTest {

    public static void main(String[] args) {
        
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(30);
        config.setMaxIdle(30);
        config.setMinIdle(20);
        config.setMaxWaitMillis(10000L);
        config.setTestOnBorrow(false);
        
        JedisPool jedisPool = new JedisPool(config, "192.168.56.101" ,6379);
        String key = "REDIS_DISTRIBUTED_LOCK";
        List<Thread> allThread = new ArrayList<>();
        for(int i=0; i<20; i++) {
            Jedis jedis = jedisPool.getResource();
            GetReleaseLockThead getReleaseLockThead = new GetReleaseLockThead(jedis, key);
            Thread thread = new Thread(getReleaseLockThead, "thread-" + i);
            allThread.add(thread);
        }
        
        for (Thread thread : allThread) {
            thread.start();
        }
        
        try {
            for (Thread thread : allThread) {
                thread.join();
            }
        } catch (InterruptedException e) {
        }
        
    }

}

/**
 * 
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月18日 上午11:23:58
 */
class GetReleaseLockThead implements Runnable {
    
    private Jedis jedis;
    private String key;
    private String clientId;
    
    public GetReleaseLockThead(Jedis jedisArg, String keyArg) {
        this.jedis = jedisArg;
        this.key = keyArg;
        this.clientId = UUID.randomUUID().toString();
    }
    
    @Override
    public void run() {
        RedisDistributedLock redisDistributedLock = new RedisDistributedLock(key, clientId, jedis);
        while(true) {
            redisDistributedLock.lock();
            System.out.println("Thread name : " + Thread.currentThread().getName() + " get lock");
            redisDistributedLock.unlock();
            System.out.println("Thread name : " + Thread.currentThread().getName() + " release lock");
        }
    }
}
