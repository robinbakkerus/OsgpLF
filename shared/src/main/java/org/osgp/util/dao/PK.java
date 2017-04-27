package org.osgp.util.dao;

import java.io.Serializable;
import java.util.UUID;

import com.aerospike.client.Key;

public class PK implements Serializable {

	private static final long serialVersionUID = -3533745321816713818L;
	
	private Object key;
	private String table;
	
	public PK(Object key) {
		this(key, null);
	}

	public PK(Object key, String table) {
		super();
		this.key = key;
		this.table = table;
	}

	public Object getKey() {
		return key;
	}
	
	public Key aeroKey() {
		return (Key) this.key;
 	}

	public byte[] redisKey() {
		return (byte[]) this.key;
 	}
	
	public String perstKey() {
		return this.key.toString();
	}

//	public Object cassandraKey() {
//		return this.key.toString();
//	}
	
	public UUID uuid() {
		if (this.key instanceof UUID) {
			return (UUID) this.key;
		} else if (this.key instanceof String) {
			return UUID.fromString(this.key.toString());
		} else {
			throw new RuntimeException("PK can not return valid UUID");
		}
	}
	
	public String getTable() {
		return table;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PK other = (PK) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} 
		
		if (key instanceof byte[]) {
			String s1 = new String(this.redisKey());
			String s2 = new String(other.redisKey());
			return s1.equals(s2);
		} else {
			return this.key.equals(other.key);
		}
	}
	
	
}
