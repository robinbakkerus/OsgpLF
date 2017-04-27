package org.osgp.pa.dlms.dlms.cmdexec;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.util.MsgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTimeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsResponseDto;
import com.alliander.osgp.shared.PropMsg;
import com.alliander.osgp.shared.ResponseStatus;
import com.alliander.osgp.shared.ResponseValuesMsg;
import static org.osgp.util.MsgMapper.prop;

@AnnotCommandExecutor(action=RequestType.GET_CONFIGURATION)
public class GetConfigurationCmdExec extends AbstractCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetConfigurationCmdExec.class);
    
	@Override
	public ResponseValuesMsg execute(DlmsConnection conn, DlmsDevice device, DlmsActionMsg reqItem)
			throws ProtocolAdapterException {
		
		final ActualMeterReadsQueryDto queryDto = new ActualMeterReadsQueryDto();
		final MeterReadsResponseDto responseDto = execute(conn, device, queryDto);
		return makeTheResponse(responseDto);
	}

	private ResponseValuesMsg makeTheResponse(final MeterReadsResponseDto responseDto) {
		return MsgMapper.makeResponseValues(makeProps(responseDto), ResponseStatus.OK, "GetConfiguration");
	}
	
	private List<PropMsg> makeProps(final MeterReadsResponseDto responseDto) {
		List<PropMsg> result = new ArrayList<>();
		result.add(this.propMsg("ActiveEnergyExport", responseDto.getActiveEnergyExport()));
		result.add(this.propMsg("ActiveEnergyExportTariffOne", responseDto.getActiveEnergyExportTariffOne()));
		result.add(this.propMsg("ActiveEnergyExportTariffTwo", responseDto.getActiveEnergyExportTariffTwo()));
		result.add(this.propMsg("ActiveEnergyImport", responseDto.getActiveEnergyImport()));
		result.add(this.propMsg("ActiveEnergyImportTariffOne", responseDto.getActiveEnergyImportTariffOne()));
		result.add(this.propMsg("ActiveEnergyImportTariffTwo", responseDto.getActiveEnergyImportTariffTwo()));
		return result;
	}

	private PropMsg propMsg(final String key, final DlmsMeterValueDto valueDto) {
		return prop(key, valueDto.getValue());
	}
	//------------------------------
	
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

    private DlmsHelperService dlmsHelperService = new DlmsHelperService();


//    public ActualMeterReadsQueryDto fromBundleRequestInput(final ActionRequestDto bundleInput)
//            throws ProtocolAdapterException {public
//
//        this.checkActionRequestType(bundleInput);
//
//        /*
//         * The ActionRequestDto, which is an ActualMeterReadsDataDto does not
//         * contain any data, so no further configuration of the
//         * ActualMeterReadsQueryDto is necessary.
//         */
//        return new ActualMeterReadsQueryDto();
//    }

    private MeterReadsResponseDto execute(final DlmsConnection conn, final DlmsDevice device,
            final ActualMeterReadsQueryDto actualMeterReadsQuery) throws ProtocolAdapterException {

        if (actualMeterReadsQuery != null && actualMeterReadsQuery.isMbusQuery()) {
            throw new IllegalArgumentException("ActualMeterReadsQuery object for energy reads should not be about gas.");
        }

//        conn.getDlmsMessageListener().setDescription("GetActualMeterReads retrieve attributes: "
//                + JdlmsObjectToStringUtil.describeAttributes(ATTRIBUTE_ADDRESSES));

        LOGGER.info("Retrieving actual energy reads");
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

        return new MeterReadsResponseDto(time.toDate(), activeEnergyImport, activeEnergyExport, activeEnergyImportRate1,
                activeEnergyImportRate2, activeEnergyExportRate1, activeEnergyExportRate2);
    }

}
