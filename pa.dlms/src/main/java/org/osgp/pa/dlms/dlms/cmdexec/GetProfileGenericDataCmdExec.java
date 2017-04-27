/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.pa.dlms.dlms.cmdexec;

import static org.osgp.util.MsgMapper.prop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.pa.dlms.util.ObisCodeHelper;
import org.osgp.util.MsgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.ProfileGenericDataMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.dto.valueobjects.smartmetering.CaptureObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileEntryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataResponseDto;
import com.alliander.osgp.shared.PropMsg;
import com.alliander.osgp.shared.ResponseStatus;
import com.alliander.osgp.shared.ResponseValuesMsg;

@AnnotCommandExecutor(action=RequestType.PROFILE_GENERIC_DATA)
public class GetProfileGenericDataCmdExec extends AbstractCommandExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetProfileGenericDataCmdExec.class);

	private static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;

	private static final Map<Integer, Integer> SCALER_UNITS_MAP = new HashMap<>();
	static {
		SCALER_UNITS_MAP.put(CosemInterfaceClass.REGISTER.id(), 3);
		SCALER_UNITS_MAP.put(CosemInterfaceClass.EXTENDED_REGISTER.id(), 3);
		SCALER_UNITS_MAP.put(CosemInterfaceClass.DEMAND_REGISTER.id(), 4);
	}

	@Override
	public ResponseValuesMsg execute(DlmsConnection conn, DlmsDevice device, DlmsActionMsg reqItem)
			throws ProtocolAdapterException {

		final ProfileGenericDataMsg msg = reqItem.getProfileGenericDataMsg();
		final DateTime beginDateTime = dateTime(msg.getDateFrom());
		final DateTime endDateTime = dateTime(msg.getDateTo());
		final ObisCode obisCode = ObisCodeHelper.makeObisCode(msg.getObisCode());
		final ObisCodeValuesDto inputObisCodes = ObisCodeHelper.makeObisCodeValuesDto(msg.getObisCode());

		List<GetResult> captureObjects = this.retrieveCaptureObjects(conn, device, obisCode);
		List<GetResult> bufferList = this.retrieveBuffer(conn, device, beginDateTime, endDateTime, obisCode);
		List<ScalerUnitInfo> scalerUnitInfos = this.retrieveScalerUnits(conn, device, captureObjects);
		ProfileGenericDataResponseDto dtoResponse = this.processData(inputObisCodes, captureObjects, bufferList,
				scalerUnitInfos);

		return formatResponse(dtoResponse);
	}


//	private ObisCode makeObisCode(final ObisCodeMgs obisCodeValues) {
//		final byte[] obisCodeBytes = { (byte) obisCodeValues.getA(), (byte) obisCodeValues.getB(),
//				(byte) obisCodeValues.getC(), (byte) obisCodeValues.getD(), (byte) obisCodeValues.getE(),
//				(byte) obisCodeValues.getF() };
//		return new ObisCode(obisCodeBytes);
//	}

	private ResponseValuesMsg formatResponse(final ProfileGenericDataResponseDto dto) {
		List<PropMsg> props = new ArrayList<>();
		props.addAll(formatCaptureObjects(dto.getCaptureObjects()));
		props.addAll(formatProfileEntries(dto.getProfileEntries()));
		
		return MsgMapper.makeResponseValues(props, ResponseStatus.OK, "Loadprofile");
	}
	
	private List<PropMsg> formatCaptureObjects(final List<CaptureObjectDto> captureObjects) {
		List<PropMsg> props = new ArrayList<>();
		props.add(PropMsg.newBuilder().setKey("Capture objects").build());
		captureObjects.forEach(f -> props.addAll(formatResponse(f)));
		return props;
	}

	private List<PropMsg> formatProfileEntries(final List<ProfileEntryDto> entries) {
		List<PropMsg> props = new ArrayList<>();
		props.add(PropMsg.newBuilder().setKey("Profile entries").build());
		entries.forEach(f -> props.addAll(formatResponse(f)));
		return props;
	}

	private List<PropMsg> formatResponse(final CaptureObjectDto obj) {
		List<PropMsg> r = new ArrayList<>();
		r.add(prop("Logical name", obj.getLogicalName()));
		r.add(prop("Class id", obj.getClassId()));
		r.add(prop("Attribute", obj.getAttribute()));
		r.add(prop("Data index", obj.getDataIndex()));
		r.add(prop("Unit", obj.getUnit()));
		return r;
	}
	
	private List<PropMsg> formatResponse(final ProfileEntryDto obj) {
		List<PropMsg> r = new ArrayList<>();
		obj.getProfileEntryValues().forEach(f -> r.add(formatProfileEntryValueDto(f)));
		return r;
	}
 
	private PropMsg formatProfileEntryValueDto(final ProfileEntryValueDto obj) {
		if (obj.getDateValue() != null) return prop("value", obj.getDateValue());
		else if (obj.getFloatValue() != null) return prop("value", obj.getFloatValue());
		else if (obj.getLongValue() != null) return prop("value", obj.getLongValue());
		else return prop("value", obj.getStringValue());
	}
	
	private DateTime dateTime(final long epoch) {return new DateTime(epoch);	}

	//--------------------------------
	
	private List<GetResult> retrieveCaptureObjects(DlmsConnection conn, DlmsDevice device, final ObisCode obisCode)
			throws ProtocolAdapterException {
		AttributeAddress captureObjectsAttributeAddress = new AttributeAddress(InterfaceClass.PROFILE_GENERIC.id(),
				obisCode, ProfileGenericAttribute.CAPTURE_OBJECTS.attributeId());

		return this.dlmsHelperService.getAndCheck(conn, device, "retrieve profile generic capture objects",
				captureObjectsAttributeAddress);
	}

	private List<GetResult> retrieveBuffer(final DlmsConnection conn, final DlmsDevice device,
			final DateTime beginDateTime, final DateTime endDateTime, final ObisCode obisCode)
			throws ProtocolAdapterException {
		final SelectiveAccessDescription access = this.getSelectiveAccessDescription(beginDateTime, endDateTime);
		AttributeAddress bufferAttributeAddress = new AttributeAddress(InterfaceClass.PROFILE_GENERIC.id(), obisCode,
				ProfileGenericAttribute.BUFFER.attributeId(), access);
		return this.dlmsHelperService.getAndCheck(conn, device, "retrieve profile generic buffer",
				bufferAttributeAddress);
	}

	/*
	 * Process data Add units to capture objects Calculate the proper values in
	 * the buffer using the scaler
	 */
	private ProfileGenericDataResponseDto processData(final ObisCodeValuesDto obisCode,
			final List<GetResult> captureObjects, final List<GetResult> bufferList,
			List<ScalerUnitInfo> scalerUnitInfos) throws ProtocolAdapterException {

		List<CaptureObjectDto> captureObjectDtos = this.makeCaptureObjects(captureObjects, scalerUnitInfos);
		List<ProfileEntryDto> profileEntryDtos = this.makeProfileEntries(bufferList, scalerUnitInfos);
		return new ProfileGenericDataResponseDto(obisCode, captureObjectDtos, profileEntryDtos);
	}

	private List<ProfileEntryDto> makeProfileEntries(final List<GetResult> bufferList,
			final List<ScalerUnitInfo> scalerUnitInfos) throws ProtocolAdapterException {

		List<ProfileEntryDto> profileEntryDtos = new ArrayList<>();
		for (GetResult buffer : bufferList) {
			DataObject dataObject = buffer.getResultData();
			final List<DataObject> dataObjectList1 = dataObject.getValue();
			for (DataObject profEntryDataObject : dataObjectList1) {
				profileEntryDtos
						.add(new ProfileEntryDto(this.makeProfileEntryValueDto(profEntryDataObject, scalerUnitInfos)));
			}
		}
		return profileEntryDtos;
	}

	private List<CaptureObjectDto> makeCaptureObjects(final List<GetResult> captureObjects,
			List<ScalerUnitInfo> scalerUnitInfos) throws ProtocolAdapterException {

		List<CaptureObjectDto> captureObjectDtos = new ArrayList<>();
		for (GetResult captureObjectResult : captureObjects) {
			DataObject dataObject = captureObjectResult.getResultData();
			final List<DataObject> dataObjectList1 = dataObject.getValue();
			for (int i = 0; i < dataObjectList1.size(); i++) {
				captureObjectDtos.add(this.makeCaptureObjectDto(dataObjectList1.get(i), scalerUnitInfos.get(i)));
			}
		}
		return captureObjectDtos;
	}

	private SelectiveAccessDescription getSelectiveAccessDescription(final DateTime beginDateTime,
			final DateTime endDateTime) {

		final int accessSelector = ACCESS_SELECTOR_RANGE_DESCRIPTOR;

		/*
		 * Define the clock object {8,0-0:1.0.0.255,2,0} to be used as
		 * restricting object in a range descriptor with a from value and to
		 * value to determine which elements from the buffered array should be
		 * retrieved.
		 */
		final DataObject clockDefinition = this.dlmsHelperService.getClockDefinition();

		final DataObject fromValue = this.dlmsHelperService.asDataObject(beginDateTime);
		final DataObject toValue = this.dlmsHelperService.asDataObject(endDateTime);

		/*
		 * List of object definitions to determine which of the capture objects
		 * to retrieve from the buffer.
		 */
		final List<DataObject> objectDefinitions = new ArrayList<>();
		final DataObject selectedValues = DataObject.newArrayData(objectDefinitions);

		final DataObject accessParameter = DataObject
				.newStructureData(Arrays.asList(clockDefinition, fromValue, toValue, selectedValues));

		return new SelectiveAccessDescription(accessSelector, accessParameter);
	}

//	private ObisCode makeObisCode(final ObisCodeValuesDto obisCodeValues) {
//		final byte[] obisCodeBytes = { obisCodeValues.getA(), obisCodeValues.getB(), obisCodeValues.getC(),
//				obisCodeValues.getD(), obisCodeValues.getE(), obisCodeValues.getF() };
//		return new ObisCode(obisCodeBytes);
//	}

	private CaptureObjectDto makeCaptureObjectDto(final DataObject captureObjectDataObject,
			final ScalerUnitInfo scalerUnitInfo) throws ProtocolAdapterException {

		final CosemObjectDefinitionDto cosemObjectDefinitionDto = this.dlmsHelperService
				.readObjectDefinition(captureObjectDataObject, "capture-object");

		return new CaptureObjectDto(cosemObjectDefinitionDto.getClassId(),
				cosemObjectDefinitionDto.getLogicalName().toString(), cosemObjectDefinitionDto.getAttributeIndex(),
				cosemObjectDefinitionDto.getDataIndex(), this.getUnit(scalerUnitInfo));
	}

	private DlmsUnitTypeDto getUnitType(final ScalerUnitInfo scalerUnitInfo) {
		if (scalerUnitInfo.getScalerUnit() != null) {
			final List<DataObject> dataObjects = scalerUnitInfo.getScalerUnit().getValue();
			final int index = Integer.parseInt(dataObjects.get(1).getValue().toString());
			DlmsUnitTypeDto unitType = DlmsUnitTypeDto.getUnitType(index);
			if (unitType != null) {
				return unitType;
			}
		}
		return DlmsUnitTypeDto.UNDEFINED;
	}

	private String getUnit(final ScalerUnitInfo scalerUnitInfo) {
		return this.getUnitType(scalerUnitInfo).getUnit();
	}

	private List<ProfileEntryValueDto> makeProfileEntryValueDto(final DataObject profEntryDataObjects,
			List<ScalerUnitInfo> scalerUnitInfos) throws ProtocolAdapterException {

		final List<ProfileEntryValueDto> result = new ArrayList<>();
		final List<DataObject> dataObjects = profEntryDataObjects.getValue();
		for (int i = 0; i < dataObjects.size(); i++) {
			result.add(this.makeProfileEntryValueDto(dataObjects.get(i), scalerUnitInfos.get(i)));
		}
		return result;
	}

	private ProfileEntryValueDto makeProfileEntryValueDto(final DataObject dataObject,
			final ScalerUnitInfo scalerUnitInfo) {
		if (InterfaceClass.CLOCK.id() == scalerUnitInfo.getClassId()) {
			return this.makeDateProfileEntryValueDto(dataObject);
		} else if (dataObject.isNumber()) {
			return this.makeNumericProfileEntryValueDto(dataObject, scalerUnitInfo);
		} else {
			final String dbgInfo = this.dlmsHelperService.getDebugInfo(dataObject);
			LOGGER.debug("creating ProfileEntryDto from " + dbgInfo + " " + scalerUnitInfo);
			return new ProfileEntryValueDto(dbgInfo);
		}
	}

	private ProfileEntryValueDto makeDateProfileEntryValueDto(final DataObject dataObject) {
		CosemDateTime cosemDateTime = CosemDateTime.decode((byte[]) dataObject.getValue());
		return new ProfileEntryValueDto(cosemDateTime.toCalendar().getTime());
	}

	private ProfileEntryValueDto makeNumericProfileEntryValueDto(final DataObject dataObject,
			final ScalerUnitInfo scalerUnitInfo) {
		try {
			if (scalerUnitInfo.getScalerUnit() != null) {
				DlmsMeterValueDto meterValue = this.dlmsHelperService.getScaledMeterValue(dataObject,
						scalerUnitInfo.getScalerUnit(), "getScaledMeterValue");
				if (DlmsUnitTypeDto.COUNT.equals(this.getUnitType(scalerUnitInfo))) {
					return new ProfileEntryValueDto(meterValue.getValue().longValue());
				} else {
					return new ProfileEntryValueDto(meterValue.getValue());
				}
			} else {
				long value = this.dlmsHelperService.readLong(dataObject, "read long");
				return new ProfileEntryValueDto(value);
			}
		} catch (ProtocolAdapterException e) {
			LOGGER.error("Error creating ProfileEntryDto from " + dataObject + " :" + e);
			final String dbgInfo = this.dlmsHelperService.getDebugInfo(dataObject);
			return new ProfileEntryValueDto(dbgInfo);
		}
	}

	private List<ScalerUnitInfo> retrieveScalerUnits(DlmsConnection conn, DlmsDevice device,
			List<GetResult> captureObjects) throws ProtocolAdapterException {

		final List<ScalerUnitInfo> result = new ArrayList<>();

		for (GetResult captureObjectResult : captureObjects) {
			DataObject dataObject = captureObjectResult.getResultData();
			final List<DataObject> dataObjectList1 = dataObject.getValue();
			for (DataObject captureObjectDataObject : dataObjectList1) {

				final CosemObjectDefinitionDto cosemObjectDefinitionDto = this.dlmsHelperService
						.readObjectDefinition(captureObjectDataObject, "capture-object");
				final int classId = cosemObjectDefinitionDto.getClassId();
				final String logicalName = cosemObjectDefinitionDto.getLogicalName().toString();
				if (this.hasScalerUnit(classId)) {
					AttributeAddress addr = new AttributeAddress(classId, logicalName, SCALER_UNITS_MAP.get(classId));
					final List<GetResult> scalerUnitResult = this.dlmsHelperService.getAndCheck(conn, device,
							"retrieve scaler unit for capture object", addr);
					DataObject scalerUnitDataObject = scalerUnitResult.get(0).getResultData();
					result.add(new ScalerUnitInfo(logicalName, classId, scalerUnitDataObject));
				} else {
					result.add(new ScalerUnitInfo(logicalName, classId, null));
				}
			}
		}

		return result;
	}

	private boolean hasScalerUnit(final int classId) {
		return SCALER_UNITS_MAP.containsKey(classId);
	}

}
