package org.osgp.smint.test.json;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

import com.alliander.osgp.dlms.ProfileGenericDataMsg;
import com.google.protobuf.util.JsonFormat;

public class TestActionsJsonFromTo {

	@Test
	public void testLoadProfileToJson() throws IOException {
		ProfileGenericDataMsg msg = ProfileGenericDataMsg.newBuilder()
				.setDateFrom(now()).setDateTo(now()).setObisCode("1.0.9.0.0.255").build();
		String json = JsonFormat.printer().print(msg);
		System.out.println(json);
		ProfileGenericDataMsg.Builder msg2 = ProfileGenericDataMsg.newBuilder();
		JsonFormat.parser().merge(json, msg2);
		System.out.println(msg2);
		
	}

	
	private long now() {return new Date().getTime();}
}
