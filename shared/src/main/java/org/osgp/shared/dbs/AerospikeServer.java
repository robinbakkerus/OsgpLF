package org.osgp.shared.dbs;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.policy.ClientPolicy;

public class AerospikeServer  {

//	private static final Logger LOGGER = LoggerFactory.getLogger(AerospikeServer.class.getName());

	private final String host;
	private final int port;
	private final AerospikeClient client;
	private final ClientPolicy cPolicy = new ClientPolicy();

	public AerospikeServer(String host, int port) {
		super();
		this.host = host;
		this.port = port;
		client = new AerospikeClient(cPolicy, host, port);
	}

	public String getHost() {
		return host;
	}


	public int getPort() {
		return port;
	}


	public AerospikeClient getClient() {
		return client;
	}

	public void cleanup()  {
		if (this.client != null) {
			this.client.close();
		}
	}
}
