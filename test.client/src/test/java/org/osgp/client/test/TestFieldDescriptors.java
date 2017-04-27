package org.osgp.client.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.osgp.core.request.RequestHandlerFact;
import org.osgp.pa.dlms.util.DlmsDeviceMsgBuildHelper;
import org.osgp.shared.CC;
import org.osgp.util.RequestHandler;

import com.alliander.osgp.shared.ActionMsg;
import com.alliander.osgp.shared.AddDeviceActionMsg;
import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.DeviceMsg;
import com.alliander.osgp.shared.ProtocolSpecificMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;

import junit.framework.Assert;

public class TestFieldDescriptors {

//	@Test
	public void test1() {
		RequestResponseMsg reqRespMsg = makeReqRespMsg();
		
		FieldDescriptor fd = getFieldDescr(ActionMsg.getDescriptor(), AddDeviceActionMsg.class);
		System.out.println(fd.getName());
		Assert.assertTrue(reqRespMsg.getAction().hasField(fd));
	}
	
	
	@Test 
	public void test2() {
		RequestHandlerFact.initialize();
		
		List<RequestResponseMsg> allMsg = new ArrayList<>();
		allMsg.add(makeReqRespMsg());
		allMsg.add(makeReqRespMsg());
		
		for (FieldDescriptor fd : ActionMsg.getDescriptor().getFields()) {
			System.out.println(fd.getName());
			List<RequestResponseMsg> filterMsgs = allMsg.stream().filter(m -> m.getAction().hasField(fd))
					.collect(Collectors.toList());	
			if ("addDevice".equals(fd.getName())) {
				Assert.assertTrue(filterMsgs.size() > 0);
			}
			
			RequestHandler handler = RequestHandlerFact.get(fd.getMessageType().getFullName());
			Assert.assertNotNull(handler);
		}
	}
	
	private FieldDescriptor getFieldDescr(Descriptor desc, Class<?> clazz) {
		return desc.getFields().stream().filter(f -> f.getMessageType().getFullName()
				.endsWith(clazz.getSimpleName()))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

	
	private RequestResponseMsg makeReqRespMsg() {
		final String devid = "NEW00001";
		DeviceMsg deviceMsg = DlmsDeviceMsgBuildHelper.makeDeviceMsg(devid);

		return RequestResponseMsg.newBuilder()
				.setCommon(makeCommon(devid))
				.setDevice(deviceMsg)
				.setAction(makeActionMsg(devid))
				.build();	
	}
	
	private ActionMsg makeActionMsg(final String devid) {
		return ActionMsg.newBuilder().setAddDevice(makeAddDeviceActionMsg(devid)).build();
	}

	private AddDeviceActionMsg makeAddDeviceActionMsg(final String devid) {
		return AddDeviceActionMsg.newBuilder().setProtocolSpecific(makeAddDeviceSpecific(devid)).build();
	}

	private ProtocolSpecificMsg makeAddDeviceSpecific(final String devid) {
		return ProtocolSpecificMsg.newBuilder().setRaw(DlmsDeviceMsgBuildHelper.makeDlmsDeviceMsg(devid).toByteString()).build();
	}

	private CommonMsg makeCommon(final String devid) {
		return CommonMsg.newBuilder().setApplicationName("Appname").setDeviceId(devid).setUserName("robinb")
				.setOrganisation(CC.INFOSTROOM).build();
	}

}
