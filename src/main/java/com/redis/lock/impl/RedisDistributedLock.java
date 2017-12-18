package com.redis.lock.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.redis.lock.intf.IDistributedLock;
import redis.clients.jedis.Jedis;

/**
 * 
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月15日 下午4:38:34
 */
public class RedisDistributedLock implements IDistributedLock {
    
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_SUCCESS = 1L;
    private static final Long DEFAULT_TIMEOUT = 10000L;
    
    private String key;
    private String clientId;
    private long timeout;
    private Jedis jedis;
    
    ExecutorService es = Executors.newFixedThreadPool(1);
    
    
    public RedisDistributedLock(String keyArg, String clientIdArg, Jedis jedisArg) {
        this(keyArg, clientIdArg, DEFAULT_TIMEOUT, jedisArg);
    }
    
    public RedisDistributedLock(String keyArg, String clientIdArg, long timeoutArg, Jedis jedisArg) {
        this.key = keyArg;
        this.clientId = clientIdArg;
        this.timeout = timeoutArg;
        this.jedis = jedisArg;
    }



    @Override
    public void lock() {
        GetLockThread getLockThread = new GetLockThread(false);
        Future<?> future = es.submit(getLockThread);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("lock() : " + e);
        }
    }



    @Override
    public void lockInterruptibly() {
        GetLockThread getLockThread = new GetLockThread(true);
        Future<?> future = es.submit(getLockThread);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e);
            future.cancel(true);
        }
    }



    @Override
    public boolean tryLock() {
        String response = jedis.set(key, clientId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, timeout);
        return LOCK_SUCCESS.equals(response);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        GetLockThread getLockThread = new GetLockThread(true);
        Future<?> future = es.submit(getLockThread);
        
        try {
            future.get(time, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.out.println(e);
            future.cancel(true);
            return false;
        }
        
        return true;
    }
    
    /**
     * 这边解锁的时候,使用一个lua
     */
    @Override
    public boolean unlock() {
        List<String> keys = new ArrayList<>();
        List<String> clientIds = new ArrayList<>();
        keys.add(key);
        clientIds.add(clientId);
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, keys, clientIds);
        
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 
     * <p>Description:</p>
     * @author hansen.wang
     * @date 2017年12月18日 上午10:59:34
     */
    class GetLockThread implements Runnable {
        
        //是否响应中断
        private boolean responseInterrupt;
        
        public GetLockThread(boolean responseInterruptArg) {
            this.responseInterrupt = responseInterruptArg;
        }
        
        @Override
        public void run() {
            while(true) {
                try {
                    String response = jedis.set(key, clientId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, timeout);
                    if(LOCK_SUCCESS.equals(response)) {
                        break;
                    }
                } catch(Exception e) {
                    System.out.println(e);
                }
                
                try {
                    TimeUnit.MILLISECONDS.sleep(100L);
                } catch (InterruptedException e) {
                    if(responseInterrupt) {
                        break;
                    }
                }
            }
        }
    }
}


