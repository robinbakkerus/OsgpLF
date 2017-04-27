package org.osgp.pa.dlms.dlms.stub;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.dlms.CommandExecutor;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.shared.ResponseValuesMsg;

public abstract class AbstractExecutorStub implements CommandExecutor  {

	private static final int SIMULATE_WAIT = 0;
	private static String waitSystemProp = null;
	private static String failSystemProp = null;
	
	
	@Override
	public ResponseValuesMsg execute(DlmsConnection conn, DlmsDevice device, DlmsActionMsg reqItem)
			throws ProtocolAdapterException {
		simulate();
		simulateWait();
		if (getFaalKans() == 0.0d || getFaalKans() <  Math.random()) {
			return makeResponse(reqItem);
		} else {
			throw new ProtocolAdapterException("FOUT");
		}
	}
	
	protected abstract ResponseValuesMsg makeResponse(final DlmsActionMsg reqItem);

	protected abstract void simulate() throws ProtocolAdapterException;
	
	protected void simulateWait() throws ProtocolAdapterException {
		try {
			Thread.sleep(pause());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected int pause() {
		int wait = 0;
		if (System.getProperty("wait") != waitSystemProp) {
			if (System.getProperty("wait")!=null) {
				wait = Integer.parseInt(System.getProperty("wait"));
			} else {
				wait = SIMULATE_WAIT;
			}
//			System.out.println("simulating wait of " + wait + " msecs");
		}
		return wait;
	}
	
	Double faalKans = 0.0d;
	
	protected double getFaalKans() {
		if (System.getProperty("fail") != failSystemProp) {
			if (System.getProperty("fail") != null) {
				faalKans = Double.parseDouble(System.getProperty("fail"));
			} else {
				faalKans = 0.0d;
			}
//			System.out.println("simulating fail with " + faalKans + " changes");
		}
		return faalKans;
	}
}
