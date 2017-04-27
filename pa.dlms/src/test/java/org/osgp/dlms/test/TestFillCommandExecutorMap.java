package org.osgp.dlms.test;

import org.junit.Assert;
import org.junit.Test;
import org.osgp.pa.dlms.dlms.CommandExecutor;
import org.osgp.pa.dlms.dlms.cmdexec.BundleExecutorImpl;
import org.osgp.pa.dlms.dlms.cmdexec.FindEventsCmdExec;
import org.osgp.pa.dlms.dlms.cmdexec.GetSpecificObject;
import org.osgp.pa.dlms.service.BundleExecutor;
import org.osgp.pa.dlms.service.ExecutorNotFoundException;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.RequestType;

public class TestFillCommandExecutorMap {

	@Test
	public void test() {
		try {
			CommandExecutor cmdExec = bundleExec().getExecutor(makeAction(RequestType.FINDEVENTS));
			Assert.assertTrue(cmdExec.getClass().getName().equals(FindEventsCmdExec.class.getName()));
			cmdExec = bundleExec().getExecutor(makeAction(RequestType.GET_SPECIFIC_OBJECT));
			Assert.assertTrue(cmdExec.getClass().getName().equals(GetSpecificObject.class.getName()));
		} catch (ExecutorNotFoundException e) {
			Assert.fail(e.getMessage());
		}
	}

	private BundleExecutor bundleExec() {
		return new BundleExecutorImpl();
	}
	
	private DlmsActionMsg makeAction(RequestType type) {
		return DlmsActionMsg.newBuilder()
				.setRequestType(type)
				.build();
	}
}
