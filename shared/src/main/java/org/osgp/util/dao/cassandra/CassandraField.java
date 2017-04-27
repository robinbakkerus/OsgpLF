package org.osgp.util.dao.cassandra;

public class CassandraField {

	final String name;
	final CassandraFieldType type;
	final boolean isPk;
	
	
	public CassandraField(String name, CassandraFieldType type) {
		this(name, type, false);
	}

	public CassandraField(String name, CassandraFieldType type, boolean isPk) {
		this.name = name;
		this.type = type;
		this.isPk = isPk;
	}

	public String getName() {
		return name;
	}

	public CassandraFieldType getType() {
		return type;
	}

	public boolean isPk() {
		return isPk;
	}
}
