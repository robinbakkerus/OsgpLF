package org.osgp.pa.dlms.service;

import org.osgp.pa.dlms.dlms.cmdexec.BundleExecutorImpl;
import org.osgp.pa.dlms.dlms.stub.BundleExecutorStub;

public class BundleExecutorFact {

	private static BundleExecutor sCommandExecutor;
	private static String useMockSystemProp = null;
	
	public static BundleExecutor get() {
		if (isMakeNeeded()) {
			if (useStub()) {
				sCommandExecutor = new BundleExecutorStub();
			} else {
				sCommandExecutor = new BundleExecutorImpl();
			}
		}
		return sCommandExecutor;
	}

	public static boolean useStub() {
		useMockSystemProp = System.getProperty("mock");
		return (useMockSystemProp != null && useMockSystemProp.toLowerCase().equals("true"));
	}
	
	private static boolean isMakeNeeded() {
		return sCommandExecutor == null || useMockSystemProp != System.getProperty("mock");
	}
}
