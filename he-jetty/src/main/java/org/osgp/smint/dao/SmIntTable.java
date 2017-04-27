package org.osgp.smint.dao;

public enum SmIntTable {
	
	JOB("Job"), 
	RECIPE("Recipe"), 
	DEV_GROUP("GroupDevice"), 
	RR_NEW("RequestResponsesNew"), 
	RR_SEND("RequestResponsesSend"), 
	RR_DONE("RequestResponsesDone");
	
	private final String tableName;

	private SmIntTable(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}
	
	
}
