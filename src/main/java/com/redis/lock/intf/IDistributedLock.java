package com.redis.lock.intf;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁的接口
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月15日 下午4:12:50
 */
public interface IDistributedLock {

    /**
     * 获取锁
     * 在成功以前一直堵塞
     */
    public void lock();
    
    /**
     * 获取锁
     * 在成功以前一直堵塞(响应Interrupt)
     */
    public void lockInterruptibly() ;
    
    /**
     * 获取锁
     * @return true:成功 false:失败
     */
    public boolean tryLock();
    
    /**
     * 指定时间内尝试获取锁
     * @param time
     * @param unit
     * @return
     */
    public boolean tryLock(long time, TimeUnit unit);
    
    /**
     * 释放锁,当锁是自己保持的情况下
     * @return
     */
    public boolean unlock();
}
