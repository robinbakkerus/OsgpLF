package org.osgp.pa.dlms.dlms.cmdexec;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.pa.dlms.util.ObisCodeHelper;
import org.osgp.util.MsgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.GetSpecificObjectMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;
import com.alliander.osgp.shared.PropMsg;
import com.alliander.osgp.shared.ResponseStatus;
import com.alliander.osgp.shared.ResponseValuesMsg;

@AnnotCommandExecutor(action = RequestType.GET_SPECIFIC_OBJECT)
public class GetSpecificObject extends AbstractCommandExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetSpecificObject.class);

	@Override
	public ResponseValuesMsg execute(DlmsConnection conn, DlmsDevice device, DlmsActionMsg reqItem)
			throws ProtocolAdapterException {
		
		final String resultStr = execute(conn, device, makeRequestData(reqItem));
		return MsgMapper.makeResponseValues(makeProps(resultStr), ResponseStatus.OK, "GetSpecificObject");
	}
	
	final List<PropMsg> makeProps(final String response) {
		List<PropMsg> r = new ArrayList<>();
		r.add(MsgMapper.prop("object", response));
		return r;
	}

	private SpecificAttributeValueRequestDto makeRequestData(final DlmsActionMsg reqItem) throws ProtocolAdapterException {
		final GetSpecificObjectMsg msg = reqItem.getGetSpecificObjectMsg();
		int classId = msg.getClassid();
		int attribute = msg.getAttrribute();
		ObisCodeValuesDto obisCodeValues = ObisCodeHelper.makeObisCodeValuesDto(msg.getObisCode());
		return new SpecificAttributeValueRequestDto(classId, attribute, obisCodeValues);
	}
	
	// -------------------
	private DlmsHelperService dlmsHelper;

	private String execute(final DlmsConnection conn, final DlmsDevice device,
			final SpecificAttributeValueRequestDto requestData) throws ProtocolAdapterException {

		final ObisCodeValuesDto obisCodeValues = requestData.getObisCode();
		final byte[] obisCodeBytes = { obisCodeValues.getA(), obisCodeValues.getB(), obisCodeValues.getC(),
				obisCodeValues.getD(), obisCodeValues.getE(), obisCodeValues.getF() };
		final ObisCode obisCode = new ObisCode(obisCodeBytes);

		LOGGER.debug("Get specific attribute value, class id: {}, obis code: {}, attribute id: {}",
				requestData.getClassId(), obisCode, requestData.getAttribute());

		final AttributeAddress attributeAddress = new AttributeAddress(requestData.getClassId(), obisCode,
				requestData.getAttribute());

		// conn.getDlmsMessageListener().setDescription("GetSpecificAttributeValue,
		// retrieve attribute: "
		// + JdlmsObjectToStringUtil.describeAttributes(attributeAddress));

		final DataObject attributeValue = this.dlmsHelper.getAttributeValue(conn, attributeAddress);
		return this.dlmsHelper.getDebugInfo(attributeValue);
	}

}
