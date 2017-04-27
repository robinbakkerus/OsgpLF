package org.osgp.at.test;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TestJedis {

	@Test
	public void test() {
		@SuppressWarnings("resource")
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
		Jedis jedis=null;
			jedis = pool.getResource();
			  /// ... do stuff here ... for example
			  jedis.set("foo", "bar");
			  @SuppressWarnings("unused")
			String foobar = jedis.get("foo");
			  jedis.zadd("sose", 0, "car"); jedis.zadd("sose", 0, "bike"); 
			  @SuppressWarnings("unused")
			Set<String> sose = jedis.zrange("sose", 0, -1);
			/// ... when closing your application:
			pool.destroy();
	}
}
