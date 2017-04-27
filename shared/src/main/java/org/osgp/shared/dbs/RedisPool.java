package org.osgp.shared.dbs;

import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {

	private final String host;
	private final BinaryJedis binaryJedis;
	private final JedisPool pool;

	
	public RedisPool(String host) {
		super();
		this.host = host;
		
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setTestOnBorrow(true);
		this.pool = new JedisPool(poolConfig, this.host);
		this.binaryJedis = pool.getResource();
		this.binaryJedis.getClient().setTimeoutInfinite();
	}

	public BinaryJedis jedis() {
		return this.binaryJedis;
	}

	public void close() {
		this.pool.close();
	}

}
