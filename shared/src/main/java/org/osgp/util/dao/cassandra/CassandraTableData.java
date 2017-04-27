package org.osgp.util.dao.cassandra;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.GeneratedMessageV3;

public class CassandraTableData {

	private final Class<? extends GeneratedMessageV3> clazz;
	private final String tableName;
	private final List<CassandraField> fields;
	
	public CassandraTableData(Class<? extends GeneratedMessageV3> clazz, String tableName, List<CassandraField> fields) {
		super();
		this.clazz = clazz;
		this.tableName = tableName;
		this.fields = fields;
	}
	
	public CassandraTableData(Class<? extends GeneratedMessageV3> clazz, String tableName) {
		this(clazz, tableName, new ArrayList<>());
	}
	
	public String getTableName() {
		return tableName;
	}
	public List<CassandraField> getFields() {
		return fields;
	}
	public Class<? extends GeneratedMessageV3> getClazz() {
		return clazz;
	}
	
	public CassandraTableData addField(final CassandraField field) {
		this.fields.add(field);
		return this;
	}
}
