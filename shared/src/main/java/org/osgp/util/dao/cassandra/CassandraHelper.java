package org.osgp.util.dao.cassandra;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.osgp.util.dao.PK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.Bytes;
import com.google.common.collect.Lists;
import com.google.protobuf.GeneratedMessageV3;

public class CassandraHelper<M extends GeneratedMessageV3> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CassandraHelper.class);
	
	private final CassandraTableData tableData;
	
	
	public CassandraHelper(final CassandraTableData tableData) {
		super();
		this.tableData = tableData;
	}

	@SuppressWarnings("unchecked")
	public List<M> selectAll(final Session session) {
		List<M> r = new ArrayList<>();
		String qry = String.format("SELECT * FROM %s", this.tableData.getTableName());
		final ResultSet rset = session.execute(qry);
		for (final Row row : rset) {
			try {
				byte[] bytes = row.getBytes("msg").array();
				Method m = clazz().getDeclaredMethod("parseFrom", byte[].class);
				r.add((M) m.invoke(null, bytes));
			} catch (Exception e) {
				LOGGER.error("error parsing record for class" + clazz().getName() + " :" + e);
			}
		}
		return r;
	}
	
	
	@SuppressWarnings("unchecked")
	public M select(final Session session, final Object value) {
		final String qry = String.format("SELECT * FROM %s WHERE %s = ?", this.tableName(), this.fldName(0));
		final ResultSet rset = session.execute(qry, getSelectValue(value));
		final Row row = rset.one();
		if (row != null) {
			try {
				byte[] bytes = row.getBytes("msg").array();
				Method m = clazz().getDeclaredMethod("parseFrom", byte[].class);
				return (M) m.invoke(null, bytes);
			} catch (Exception e) {
				LOGGER.error("error parsing record for RequestResponseMsg", e);
			}
		}
		return null;
	}
		
	private Object getSelectValue(final Object value) {
		if (value instanceof String) {
			if (isUuid(value)) {
				return UUID.fromString(value.toString());
			}
		} 
		return value;
	}
	
	private boolean isUuid(final Object value) {
		if (value instanceof String) {
			final String str = value.toString();
			return str.length() == 36 && str.indexOf('-') > 1;
		} else {
			return false;
		}
	}
	
	public void delete(final Session session, final PK pk) {
		session.execute(deleteQuery(pk));
	}

	private String deleteQuery(final PK pk) {
		return String.format("DELETE FROM %s WHERE %s = %s; ", this.tableName(), this.fldName(0), this.formatValue(pk.getKey(), 0));
	}
	
	public void deleteList(final Session session, final List<PK> pkList) {
		List<List<PK>> partPkList = Lists.partition(pkList, 250);
		for (List<PK> pks : partPkList) {
			StringBuilder sb = new StringBuilder("BEGIN BATCH ");
			pks.forEach(pk -> sb.append(deleteQuery(pk)));
			sb.append(" APPLY BATCH");
			session.execute(sb.toString());
		}
	}
	
	public void save(final Session session, final Object... values) {
		session.execute(formatQuery(values));
	}
	
	public void saveList(final Session session, final List<List<Object>> valueList) {
		List<List<List<Object>>> partLists = Lists.partition(valueList, 100);
		for (List<List<Object>> partValuesList : partLists) {
			StringBuilder sb = new StringBuilder("BEGIN BATCH ");
			for (List<Object> objects : partValuesList) {
				sb.append(formatQuery(objects));
			}
			sb.append(" APPLY BATCH");
			session.execute(sb.toString());
		}

	}

	private String formatQuery(final List<Object> values) {
		return String.format("INSERT INTO %s (%s) VALUES (%s)", this.tableName(), fields(), formatValues(values));
	}

	private String formatQuery(final Object... values) {
		return String.format("INSERT INTO %s (%s) VALUES (%s)", this.tableName(), fields(), formatValues(values));
	}

	private String fields() {
		List<String> fldnames= this.tableData.getFields().stream().map(f -> f.getName()).collect(Collectors.toList());
		return  fldnames.stream().collect(Collectors.joining(","));
	}
	
	private String formatValues(List<Object> values) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<values.size(); i++) {
			sb.append(formatValue(values.get(i), i));
			if (i < values.size() - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
	
	private String formatValues(Object... values) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<values.length; i++) {
			sb.append(formatValue(values[i], i));
			if (i < values.length - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
	
	private String formatValue(final Object value, final int fldIndex) {
		final CassandraField fld = this.tableData.getFields().get(fldIndex);
		if (CassandraFieldType.STRING == fld.getType() ) {
			return "'" + value.toString() + "'";
		} else {
			return value.toString();
		}
	}
	
	
	public static String blob(final com.google.protobuf.GeneratedMessageV3 msg) {
		return Bytes.toHexString(msg.toByteArray());
	}

	public static UUID uuid(final String str) {
		return UUID.fromString(str);
	}

	private Class<? extends GeneratedMessageV3> clazz() {
		return this.tableData.getClazz();
	}
	
	private String tableName() {
		return this.tableData.getTableName();
	}
	
	private String fldName(final int index) {
		return this.tableData.getFields().get(index).getName();
	}

}
