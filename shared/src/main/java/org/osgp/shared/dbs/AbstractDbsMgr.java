package org.osgp.shared.dbs;

import org.osgp.shared.dbs.perst.AbstractPerstStorage;
import org.osgp.util.ConfigHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Session;

public abstract class AbstractDbsMgr<T> {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractDbsMgr.class);

	private final String host;
	private final int aeroSpikePort;
	protected final String perstDbname;
	private AerospikeServer aerospike;
	private RedisPool redisPool;
	protected AbstractPerstStorage<T> perstStorage;
	private AbstractCassandraClient cassandraClient;

	public AbstractDbsMgr(String host, int aeroSpikePort, String perstDbname) {
		super();
		this.host = host;
		this.aeroSpikePort = aeroSpikePort;
		this.perstDbname = perstDbname;

		setupDatabase();
	}

	
	private void setupDatabase() {
		final Database dbs = ConfigHelper.getDatabaseImpl();
		if (Database.Redis == dbs) {
			startRedis();
		} else if (Database.PERST == dbs) {
			startPerst();
		} else if (Database.Aerospike == dbs) {
			startAerospike();
		} else {
			startCassandra();
		}
	}

	private void startAerospike() {
		if (aerospike == null) {
			aerospike = new AerospikeServer(this.host, this.aeroSpikePort);
			LOGGER.warn("Aerospike database started ");
		}
	}

	protected void startPerst() {
		if (perstStorage == null) {
			perstStorage = makePerstRoot();
			LOGGER.warn("Perst database started " + perstDbname);
		}
	}
	
	protected abstract AbstractPerstStorage<T> makePerstRoot();
	
	protected abstract AbstractCassandraClient getCassandraClient();


	public T getPerstRoot() {
		if (this.perstStorage == null) {
			return null;
		} else {
			return (T) this.perstStorage.getMsgRoot();
		}
	}

	private void startRedis() {
		if (redisPool == null) {
			redisPool = new RedisPool(this.host);
			LOGGER.warn("Redis Database started ");
		}
	}
	
	private void startCassandra() {
		if (this.getCassandraClient() != null) {
			this.cassandraClient = this.getCassandraClient();
			this.cassandraClient.initialize("localhost", 9042);
		} else {
			throw new RuntimeException("cassandra client is null");
		}
	}
	
	public Session getCassandraSession() {
		return cassandraClient.getSession();
	}
	
	public AerospikeServer getAeroServer() {
		return aerospike;
	}

	public RedisPool getRedisPool() {
		return redisPool;
	}
	
	public void closeDatabase() {
		closeAerospike();
		closeRedis();
		closePerst();
		closeCassandra();
	}

	private void closeRedis() {
		if (redisPool != null) {
			redisPool.close();
			redisPool = null;
		}
	}

	private void closeAerospike() {
		if (aerospike != null) {
			aerospike.cleanup();
			aerospike = null;
		}
	}

	private void closePerst() {
		if (perstStorage != null) {
			perstStorage.shutdown();
			perstStorage = null;
		}
	}

	
	private void closeCassandra() {
		if (cassandraClient != null) {
			cassandraClient.close();
			cassandraClient = null;
		}
	}
}
