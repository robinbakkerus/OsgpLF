package org.osgp.smint.service;

import java.io.IOException;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

public abstract class AbstractService {

	protected StringBuilder startJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"data\":");
		return sb;
	}

	protected String endJson(StringBuilder sb) {
		sb.append("}");
		return sb.toString();
	}
	
	protected StringBuilder startJsonArray() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"data\": [");
		return sb;
	}

	protected String endJsonArray(StringBuilder sb) {
		sb.append("]}");
		return sb.toString();
	}


	protected void addJson(MessageOrBuilder obj, Appendable sb) {
		try {
			JsonFormat.printer().appendTo(obj, sb);
		} catch (IOException e) {
			e.printStackTrace();
			//TODO sb.append ??
		}
	}

	protected void failIfInvalid(String name, String email) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' cannot be empty");
		}
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'email' cannot be empty");
		}
	}
	
}
