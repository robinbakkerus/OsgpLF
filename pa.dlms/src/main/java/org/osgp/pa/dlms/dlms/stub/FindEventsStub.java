package org.osgp.pa.dlms.dlms.stub;

import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.shared.ResponseValuesMsg;
import com.alliander.osgp.shared.ResponseStatus;

@AnnotCommandExecutorStub(action=RequestType.FINDEVENTS)
public class FindEventsStub extends AbstractExecutorStub {

	@Override
	protected ResponseValuesMsg makeResponse(final DlmsActionMsg reqItem) {

		ResponseValuesMsg result = ResponseValuesMsg.newBuilder()
				.setStatus(ResponseStatus.OK)
				.build();
//				todo properties		
//				.setResponse("find events response").build();

		return result;
	}
	
	@Override
	protected void simulate() throws ProtocolAdapterException {
	}
}
