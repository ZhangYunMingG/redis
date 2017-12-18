package com.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.SortingParams;

/**
 * value为List的一些操作
 * @author haiswang
 *
 */
public class RedisJedisClientForListValue {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(5);
		config.setMaxWaitMillis(1000L);
		config.setTestOnBorrow(false);
		
		JedisPool jedisPool = new JedisPool(config, "10.249.72.184" ,6379);
		Jedis jedis = jedisPool.getResource();
		
		System.out.println("清空库中所有的数据:" + jedis.flushDB());
		
		jedis.lpush("listname1", "listvalue1");
		jedis.lpush("listname1", "listvalue2");
		jedis.lpush("listname1", "listvalue3");
		jedis.lpush("listname1", "listvalue4");
		jedis.lpush("listname1", "listvalue5");
		jedis.lpush("listname1", "listvalue3");
		jedis.lpush("listname1", "listvalue4");
		jedis.lpush("listname1", "listvalue5");
		
		jedis.lpush("listname2", "lv1");
		jedis.lpush("listname2", "lv2");
		jedis.lpush("listname2", "lv3");
		jedis.lpush("listname2", "lv4");
		jedis.lpush("listname2", "lv5");
		jedis.lpush("listname2", "lv3");
		jedis.lpush("listname2", "lv4");
		jedis.lpush("listname2", "lv5");
		
		/**
		 * end为-1的时候表示全部的数据
		 */
		System.out.println("所有元素-listname1:" + jedis.lrange("listname1", 0, -1));
		System.out.println("所有元素-listname2:" + jedis.lrange("listname2", 0, -1));
		
		System.out.println("===============删除===============");
		/**
		 * 删除列表指定的值,第二个参数为删除元素的个数(有重复时),后add进去的值先被删除,类似于出栈
		 */
		System.out.println("成功删除指定元素的个数-listname1:" + jedis.lrem("listname1", 1, "listvalue4"));
		System.out.println("删除指定元素之后-listname1:" + jedis.lrange("listname1", 0, -1));
		
		/**
		 * 删除区间之外的数据
		 */
		System.out.println("删除下标0-3区间之外的元素:" + jedis.ltrim("listname1", 0, 3));
		System.out.println("删除区间之外元素之后-listname1:" + jedis.lrange("listname1", 0, -1));
		
		/**
		 * 列表元素出栈,redis的list类似于栈,后进先出
		 */
		System.out.println("出栈元素:" + jedis.lpop("listname1"));
		System.out.println("元素出栈以后-listname1:" + jedis.lrange("listname1", 0, -1));
		
		System.out.println("===============修改===============");
		/**
		 * 这边的下标是按照add的相反顺序
		 */
		System.out.println("修改下标为0的元素:" + jedis.lset("listname1", 0, "listvalue0"));
		System.out.println("元素修改以后-listname1:" + jedis.lrange("listname1", 0, -1));
		
		System.out.println("===============查询===============");
		/**
		 * 链表的长度
		 */
		System.out.println("长度-listname1:" + jedis.llen("listname1"));
		System.out.println("长度-listname2:" + jedis.llen("listname2"));
		
		System.out.println("===============排序===============");
		
		//list中存字符串时必须指定参数为alpha，如果不使用SortingParams，而是直接使用sort("list"),会出现"ERR One or more scores can't be converted into double"
		SortingParams sortingParams = new SortingParams();
		sortingParams.alpha();
		sortingParams.limit(0, 3);
		
		jedis.lpush("sort1", "2");
		jedis.lpush("sort1", "1");
		jedis.lpush("sort1", "5");
		jedis.lpush("sort1", "3");
		jedis.lpush("sort1", "4");
		jedis.lpush("sort1", "9");
		jedis.lpush("sort1", "0");
		
		System.out.println("返回排序后的结果-sort1:" + jedis.sort("sort1"));
		
		/**
		 * 
		 */
		System.out.println("子串排序后的结果-sort1:" + jedis.lrange("sort1", 1, -1));
		
		/**
		 * 获取指定下标的值
		 */
		System.out.println("获取下标为2的元素listname1:" + jedis.lindex("listname1", 2));
		
	}

}
