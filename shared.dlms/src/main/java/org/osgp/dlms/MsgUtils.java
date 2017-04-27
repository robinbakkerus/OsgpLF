package org.osgp.dlms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.osgp.shared.exceptionhandling.ComponentType;
import org.osgp.shared.exceptionhandling.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.DlmsSpecificMsg;
import com.alliander.osgp.shared.ActionMsg;
import com.alliander.osgp.shared.ProtocolSpecificMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseValuesMsg;
import com.google.protobuf.InvalidProtocolBufferException;

public class MsgUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MsgUtils.class);

	public static DlmsSpecificMsg getDlmsSpecific(final RequestResponseMsg reqRespMsg) throws TechnicalException {
		try {
			return DlmsSpecificMsg.parseFrom(reqRespMsg.getAction().getProtocolSpecific().getRaw());
		} catch (InvalidProtocolBufferException e) {
			final String s = "error parsing specific action " + e;
			LOGGER.error(s);
			throw new TechnicalException(ComponentType.PROTOCOL_DLMS, s);
		}
	}
	
	public static boolean hasProtocolSpecificAction(final RequestResponseMsg reqRespMsg) {
		return reqRespMsg != null && reqRespMsg.getAction() != null 
				&& reqRespMsg.getAction().getProtocolSpecific() != null 
				&& reqRespMsg.getAction().getProtocolSpecific().toString().length() > 0;
	}	

	public static boolean hasAddDeviceAction(final RequestResponseMsg reqRespMsg) {
		return reqRespMsg != null && reqRespMsg.getAction() != null 
				&& reqRespMsg.getAction().getAddDevice() != null 
				&& reqRespMsg.getAction().getAddDevice().toString().length() > 0;
	}	

	public static boolean hasUpdateFirmwareAction(final RequestResponseMsg reqRespMsg) {
		return reqRespMsg != null && reqRespMsg.getAction() != null 
				&& reqRespMsg.getAction().getUpdateFirmware() != null 
				&& reqRespMsg.getAction().getUpdateFirmware().toString().length() > 0;
	}	

	public static List<DlmsActionMsg> getDlmsActions(final RequestResponseMsg reqRespMsg) throws TechnicalException {
		if (hasProtocolSpecificAction(reqRespMsg)) {
			DlmsSpecificMsg specific = getDlmsSpecific(reqRespMsg);
			return specific.getActionsList();
		} else {
			return new ArrayList<>();
		}
	}

	public static List<ResponseValuesMsg> getResponseValues(final RequestResponseMsg reqRespMsg) throws TechnicalException {
		//TODO ook de niet specifiek responses!
		if (hasProtocolSpecificAction(reqRespMsg)) {
			DlmsSpecificMsg sm = getDlmsSpecific(reqRespMsg);
			for (DlmsActionMsg a : sm.getActionsList()) {
				ResponseValuesMsg vals =  a.getResponse();
				System.out.println(vals);
			}
			List<ResponseValuesMsg> r = getDlmsActions(reqRespMsg).stream().map(f -> f.getResponse()).collect(Collectors.toList());
			return r;
		} else {
			return new ArrayList<>();
		}
	}

	public static RequestResponseMsg updateActions(final RequestResponseMsg reqRespMsg, List<DlmsActionMsg> updatedActions) {
		DlmsSpecificMsg specificMsg = DlmsSpecificMsg.newBuilder()
				.addAllActions(updatedActions)
				.build();
		
		return RequestResponseMsg.newBuilder(reqRespMsg)
				.setAction(makeDlmsAction(specificMsg))
				.build();
				
	}
	
	public static ActionMsg makeDlmsAction(final DlmsSpecificMsg specificMsg) {
		ProtocolSpecificMsg protocolSpecific = ProtocolSpecificMsg.newBuilder()
				.setRaw(specificMsg.toByteString())
				.build();
		
		return ActionMsg.newBuilder()
				.setProtocolSpecific(protocolSpecific)
				.build();
	}

}
