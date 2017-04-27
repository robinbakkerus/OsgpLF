package org.osgp.platform.dbs;

public enum PlatformTable {

	REQ_RESP("RequestResponses"), 
	CORE_UNDELIVERED("UndeliveredCore");
	
	private final String tableName;

	private PlatformTable(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}
	
}
