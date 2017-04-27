package org.osgp.pa.dlms.dlms.stub;

import java.util.ArrayList;
import java.util.List;

import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.util.MsgMapper;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.shared.PropMsg;
import com.alliander.osgp.shared.ResponseStatus;
import com.alliander.osgp.shared.ResponseValuesMsg;

@AnnotCommandExecutorStub(action=RequestType.GET_SPECIFIC_OBJECT)
public class GetSpecificObjectStub extends AbstractExecutorStub {
	
	@Override
	protected ResponseValuesMsg makeResponse(DlmsActionMsg action) {
		return makeTheResponse(action);
	}


//	private static final String STR = 
//		"get-specific response for class %d, attr %d, obiscode %s,%s,%s,%s,%s,%s";
		
//	private String makeResponseString(final DlmsActionMsg action) {
//		GetSpecificObjectMsg getSpecObj = action.getGetSpecificObjectMsg();
//		int classid = getSpecObj.getClassid();
//		int attr = getSpecObj.getAttrribute();
//		int a = getSpecObj.getObiscode().getA();
//		int b = getSpecObj.getObiscode().getB();
//		int c = getSpecObj.getObiscode().getC();
//		int d = getSpecObj.getObiscode().getD();
//		int e = getSpecObj.getObiscode().getE();
//		int f = getSpecObj.getObiscode().getF();
//	
//		return String.format(STR, classid, attr, a,b,c,d,e,f);
//	}
	
	@Override
	protected void simulate() throws ProtocolAdapterException {
	}
	
		
	private ResponseValuesMsg makeTheResponse(final DlmsActionMsg reqItem) {
		return MsgMapper.makeResponseValues(makeProps(reqItem), ResponseStatus.OK, "GetSpecificObject");
	}
	
	private List<PropMsg> makeProps(final DlmsActionMsg reqItem) {
		List<PropMsg> result = new ArrayList<>();
		result.add(PropMsg.newBuilder()
				.setValue("TODO")
				.build());
		return result;
	}	

}
