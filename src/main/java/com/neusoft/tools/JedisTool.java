package com.neusoft.tools;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisTool {
	//redis的ip地址
	private static String ADDR="10.25.39.145";
	//redis的端口号
	private static int PORT = 6379;
	//密码
	private static String AUTH ="1997";
	//最大连接数
	private static int MAX_ACTIVE = 1024;
	//最大空闲数，数据库连接的最大空闲时间。超过空闲时间，数据库连接将被标记为不可用，然后被释放。设为0表示无限制。
	private static int MAX_IDLE=200;
	//最大等待数目
	private static long MAX_WAIT = 10000;
	//断开连接时间
	private static int TIMEOUT = 10000;
	
	private static boolean TEST_ON_BORROW = true;
	
	private static JedisPool jedisPool = null;
	
	public static Jedis getConnection(){
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(MAX_ACTIVE);
		//setMaxActive(MAX_ACTIVE);
		config.setMaxIdle(MAX_IDLE);
		config.setMaxWaitMillis(MAX_WAIT);
		config.setTestOnBorrow(TEST_ON_BORROW);
		jedisPool = new JedisPool(config,ADDR,PORT,TIMEOUT,AUTH);
		Jedis resource = jedisPool.getResource();
		return resource;
	}
}
