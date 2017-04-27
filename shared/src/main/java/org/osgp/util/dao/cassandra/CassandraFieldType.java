package org.osgp.util.dao.cassandra;

public enum CassandraFieldType {

	UUID("UUID"), BLOB("BLOB"), LONG("BIGINT"), STRING("VARCHAR");

	private final String sqlType;

	private CassandraFieldType(String sqlType) {
		this.sqlType = sqlType;
	}

	public String getSqlType() {
		return sqlType;
	}
	
	
}
