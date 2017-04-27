package org.osgp.pa.dlms.dlms.stub;

import java.util.ArrayList;
import java.util.List;

import org.osgp.dlms.DC;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.util.MsgMapper;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.shared.ResponseValuesMsg;
import com.alliander.osgp.shared.ResponseValuesMsg;
import com.alliander.osgp.shared.PropMsg;
import com.alliander.osgp.shared.ResponseStatus;

@AnnotCommandExecutorStub(action=RequestType.GET_CONFIGURATION)
public class GetConfigurationStub extends AbstractExecutorStub {

	@Override
	protected ResponseValuesMsg makeResponse(final DlmsActionMsg reqItem) {
		return makeTheResponse(reqItem);
	}

	@Override
	protected void simulate() throws ProtocolAdapterException {
		if (System.getProperty("simulateError") != null) {
			throw new ProtocolAdapterException("simulating communication error");
		}
	}
	
	private ResponseValuesMsg makeTheResponse(final DlmsActionMsg reqItem) {
		return MsgMapper.makeResponseValues(makeProps(reqItem), ResponseStatus.OK, "GetConfiguration");
	}
	
	private List<PropMsg> makeProps(final DlmsActionMsg reqItem) {
		List<PropMsg> result = new ArrayList<>();
		result.add(PropMsg.newBuilder()
				.setValue("TODO")
				.build());
		return result;
	}

}
