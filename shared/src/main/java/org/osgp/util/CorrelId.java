package org.osgp.util;

import java.util.UUID;

import com.alliander.osgp.shared.CorrelIdMsg;

public class CorrelId {

	public static String generate() {
		return UUID.randomUUID().toString();
	}
	
	public static CorrelIdMsg generateMsg() {
		return CorrelIdMsg.newBuilder().setCorrelid(generate()).build();
	}
}
