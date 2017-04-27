package org.osgp.pa.dlms.dlms.cmdexec;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.util.MsgMapper;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTimeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import com.alliander.osgp.shared.PropMsg;
import com.alliander.osgp.shared.ResponseStatus;
import com.alliander.osgp.shared.ResponseValuesMsg;

@AnnotCommandExecutor(action = RequestType.GET_ACTUAL_METER_READS)
public class GetActualMeterReadsCmdExec extends AbstractCommandExecutor {

	private static final int CLASS_ID_REGISTER = 3;
	private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_IMPORT = new ObisCode("1.0.1.8.0.255");
	private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_EXPORT = new ObisCode("1.0.2.8.0.255");
	private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_1 = new ObisCode("1.0.1.8.1.255");
	private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_2 = new ObisCode("1.0.1.8.2.255");
	private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_1 = new ObisCode("1.0.2.8.1.255");
	private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_2 = new ObisCode("1.0.2.8.2.255");
	private static final byte ATTRIBUTE_ID_VALUE = 2;
	private static final byte ATTRIBUTE_ID_SCALER_UNIT = 3;

	private static final int CLASS_ID_CLOCK = 8;
	private static final ObisCode OBIS_CODE_CLOCK = new ObisCode("0.0.1.0.0.255");
	private static final byte ATTRIBUTE_ID_TIME = 2;

	// scaler unit attribute address is filled dynamically
	private static final AttributeAddress[] ATTRIBUTE_ADDRESSES = {
			new AttributeAddress(CLASS_ID_CLOCK, OBIS_CODE_CLOCK, ATTRIBUTE_ID_TIME),
			new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT, ATTRIBUTE_ID_VALUE),
			new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_1, ATTRIBUTE_ID_VALUE),
			new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_2, ATTRIBUTE_ID_VALUE),
			new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT, ATTRIBUTE_ID_VALUE),
			new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_1, ATTRIBUTE_ID_VALUE),
			new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_2, ATTRIBUTE_ID_VALUE),
			new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT, ATTRIBUTE_ID_SCALER_UNIT),
			new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_1, ATTRIBUTE_ID_SCALER_UNIT),
			new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_2, ATTRIBUTE_ID_SCALER_UNIT),
			new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT, ATTRIBUTE_ID_SCALER_UNIT),
			new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_1, ATTRIBUTE_ID_SCALER_UNIT),
			new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_2, ATTRIBUTE_ID_SCALER_UNIT) };

	private static final int INDEX_TIME = 0;
	private static final int INDEX_ACTIVE_ENERGY_IMPORT = 1;
	private static final int INDEX_ACTIVE_ENERGY_IMPORT_RATE_1 = 2;
	private static final int INDEX_ACTIVE_ENERGY_IMPORT_RATE_2 = 3;
	private static final int INDEX_ACTIVE_ENERGY_EXPORT = 4;
	private static final int INDEX_ACTIVE_ENERGY_EXPORT_RATE_1 = 5;
	private static final int INDEX_ACTIVE_ENERGY_EXPORT_RATE_2 = 6;
	private static final int INDEX_ACTIVE_ENERGY_IMPORT_SCALER_UNIT = 7;
	private static final int INDEX_ACTIVE_ENERGY_IMPORT_RATE_1_SCALER_UNIT = 8;
	private static final int INDEX_ACTIVE_ENERGY_IMPORT_RATE_2_SCALER_UNIT = 9;
	private static final int INDEX_ACTIVE_ENERGY_EXPORT_SCALER_UNIT = 10;
	private static final int INDEX_ACTIVE_ENERGY_EXPORT_RATE_1_SCALER_UNIT = 11;
	private static final int INDEX_ACTIVE_ENERGY_EXPORT_RATE_2_SCALER_UNIT = 12;

	@Override
	public ResponseValuesMsg execute(DlmsConnection conn, DlmsDevice device, DlmsActionMsg reqItem)
			throws ProtocolAdapterException {

		// LOGGER.info("Retrieving actual energy reads");
		final List<GetResult> getResultList = this.dlmsHelperService.getAndCheck(conn, device,
				"retrieve actual meter reads", ATTRIBUTE_ADDRESSES);

		final CosemDateTimeDto cosemDateTime = this.dlmsHelperService.readDateTime(getResultList.get(INDEX_TIME),
				"Actual Energy Reads Time");
		final DateTime time = cosemDateTime.asDateTime();
		if (time == null) {
			throw new ProtocolAdapterException("Unexpected null/unspecified value for Actual Energy Reads Time");
		}
		final DlmsMeterValueDto activeEnergyImport = this.dlmsHelperService.getScaledMeterValue(
				getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT),
				getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT_SCALER_UNIT), "Actual Energy Reads +A");
		final DlmsMeterValueDto activeEnergyExport = this.dlmsHelperService.getScaledMeterValue(
				getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT),
				getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT_SCALER_UNIT), "Actual Energy Reads -A");
		final DlmsMeterValueDto activeEnergyImportRate1 = this.dlmsHelperService.getScaledMeterValue(
				getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT_RATE_1),
				getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT_RATE_1_SCALER_UNIT), "Actual Energy Reads +A rate 1");
		final DlmsMeterValueDto activeEnergyImportRate2 = this.dlmsHelperService.getScaledMeterValue(
				getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT_RATE_2),
				getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT_RATE_2_SCALER_UNIT), "Actual Energy Reads +A rate 2");
		final DlmsMeterValueDto activeEnergyExportRate1 = this.dlmsHelperService.getScaledMeterValue(
				getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT_RATE_1),
				getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT_RATE_1_SCALER_UNIT), "Actual Energy Reads -A rate 1");
		final DlmsMeterValueDto activeEnergyExportRate2 = this.dlmsHelperService.getScaledMeterValue(
				getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT_RATE_2),
				getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT_RATE_2_SCALER_UNIT), "Actual Energy Reads -A rate 2");

		return makeTheResponse(new Date(), activeEnergyImport, activeEnergyExport, activeEnergyImportRate1,
				activeEnergyImportRate2, activeEnergyExportRate1, activeEnergyExportRate2);
	}

	private ResponseValuesMsg makeTheResponse(final Date logTime, final DlmsMeterValueDto activeEnergyImport,
			final DlmsMeterValueDto activeEnergyExport, final DlmsMeterValueDto activeEnergyImportTariffOne,
			final DlmsMeterValueDto activeEnergyImportTariffTwo, final DlmsMeterValueDto activeEnergyExportTariffOne,
			final DlmsMeterValueDto activeEnergyExportTariffTwo) {

		List<PropMsg> props = makeProps(new Date(), activeEnergyImport, activeEnergyExport,
				activeEnergyImportTariffOne, activeEnergyImportTariffTwo, activeEnergyExportTariffOne,
				activeEnergyExportTariffTwo);

		return MsgMapper.makeResponseValues(props, ResponseStatus.OK, "GetActualMeterReads");
	}

	private List<PropMsg> makeProps(final Date logTime, final DlmsMeterValueDto activeEnergyImport,
			final DlmsMeterValueDto activeEnergyExport, final DlmsMeterValueDto activeEnergyImportTariffOne,
			final DlmsMeterValueDto activeEnergyImportTariffTwo, final DlmsMeterValueDto activeEnergyExportTariffOne,
			final DlmsMeterValueDto activeEnergyExportTariffTwo) {

		List<PropMsg> result = new ArrayList<>();
		result.add(makeProp("activeEnergyImport", logTime));
		result.add(makeProp("activeEnergyImport", activeEnergyImport));
		result.add(makeProp("activeEnergyExport", activeEnergyExport));
		result.add(makeProp("activeEnergyImportTariffOne", activeEnergyImportTariffOne));
		result.add(makeProp("activeEnergyImportTariffTwo", activeEnergyImportTariffTwo));
		result.add(makeProp("activeEnergyExportTariffOne", activeEnergyExportTariffOne));
		result.add(makeProp("activeEnergyExportTariffTwo", activeEnergyExportTariffTwo));
		return result;
	}

	private PropMsg makeProp(final String key, final DlmsMeterValueDto dto) {
		return MsgMapper.prop(key, dto.getValue().doubleValue());
	}

	private PropMsg makeProp(final String key, final Date date) {
		return MsgMapper.prop(key, date);
	}

}
