package org.osgp.shared.dbs;

import static java.util.stream.Collectors.joining;

import java.util.Collection;

import org.osgp.util.dao.cassandra.CassandraField;
import org.osgp.util.dao.cassandra.CassandraTableData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;

public abstract class AbstractCassandraClient {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractCassandraClient.class);

	private Cluster cluster;

	private Session session;

	
	protected abstract String getKeyspaceName();

	protected abstract Collection<CassandraTableData> getCassandraTables();
	
	protected String getStrategy() {
		return "SimpleStrategy";
	}

	protected int getReplicationFactor() {
		return 1;
	}

	public AbstractCassandraClient() {
		super();
	}

	public void initialize(final String node, final Integer port) {
		this.connect(node, port);
		this.createKeyspace();
		this.useKeyspace();
		this.createTables();
	}

	private void connect(final String node, final Integer port) {

		Builder b = Cluster.builder().addContactPoint(node);

		if (port != null) {
			b.withPort(port);
		}
		cluster = b.build();

		Metadata metadata = cluster.getMetadata();
		LOG.info("Cluster name: " + metadata.getClusterName());

		for (Host host : metadata.getAllHosts()) {
			LOG.info(
					"Datacenter: " + host.getDatacenter() + " Host: " + host.getAddress() + " Rack: " + host.getRack());
		}

		session = cluster.connect();
	}

	public Session getSession() {
		return this.session;
	}

	public void close() {
		session.close();
		cluster.close();
	}

	/**
	 * Method used to create any keyspace - schema.
	 * 
	 * @param schemaName
	 *            the name of the schema.
	 * @param replicatioonStrategy
	 *            the replication strategy.
	 * @param numberOfReplicas
	 *            the number of replicas.
	 * 
	 */
	private void createKeyspace() {
		StringBuilder sb = new StringBuilder("CREATE KEYSPACE IF NOT EXISTS ").append(this.getKeyspaceName())
				.append(" WITH replication = {").append("'class':'").append(this.getStrategy())
				.append("','replication_factor':").append(this.getReplicationFactor()).append("};");

		final String query = sb.toString();

		session.execute(query);
	}

	private void useKeyspace() {
		session.execute("USE " + this.getKeyspaceName());
	}

	/**
	 * Method used to delete the specified schema. It results in the immediate,
	 * irreversable removal of the keyspace, including all tables and data
	 * contained in the keyspace.
	 * 
	 * @param schemaName
	 *            the name of the keyspace to delete.
	 */
	public void deleteKeyspace() {
		if (session != null) {
			StringBuilder sb = new StringBuilder("DROP KEYSPACE ").append(this.getKeyspaceName());
			final String query = sb.toString();
			session.execute(query);
		}
	}

	private void createTables() {
		this.getCassandraTables().forEach(td -> createTable(td));
	}

	private void createTable(final CassandraTableData td) {
		final String qry = String.format(
				"CREATE TABLE IF NOT EXISTS %s (%s);", td.getTableName(), tableFields(td));
		this.getSession().execute(qry);
	}
	
	
	private String tableFields(final CassandraTableData td) {
		StringBuffer sb = new StringBuffer();
		for (CassandraField fld : td.getFields()) {
			sb.append(fld.getName() + " " + fld.getType().getSqlType() + ", ");
		}
		sb.append("PRIMARY KEY ");
		sb.append(td.getFields().stream().filter(f -> f.isPk()).map(f -> f.getName()).collect(joining(",","(",")")));
		return sb.toString();
	}
	
}
