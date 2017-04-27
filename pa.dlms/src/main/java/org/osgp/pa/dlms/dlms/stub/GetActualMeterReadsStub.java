package org.osgp.pa.dlms.dlms.stub;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.util.MsgMapper;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import com.alliander.osgp.shared.PropMsg;
import com.alliander.osgp.shared.ResponseStatus;
import com.alliander.osgp.shared.ResponseValuesMsg;

@AnnotCommandExecutorStub(action = RequestType.GET_ACTUAL_METER_READS)
public class GetActualMeterReadsStub extends AbstractExecutorStub {

	@Override
	protected ResponseValuesMsg makeResponse(final DlmsActionMsg reqItem) {
		return makeTheResponse();
	}

	@Override
	protected void simulate() throws ProtocolAdapterException {
	}
	
	private ResponseValuesMsg makeTheResponse() {
		DlmsMeterValueDto activeEnergyImport = new DlmsMeterValueDto(new BigDecimal("10.5"), DlmsUnitTypeDto.KWH);
		DlmsMeterValueDto activeEnergyExport = new DlmsMeterValueDto(new BigDecimal("10.5"), DlmsUnitTypeDto.KWH);
		DlmsMeterValueDto activeEnergyImportTariffOne = new DlmsMeterValueDto(new BigDecimal("10.5"), DlmsUnitTypeDto.KWH);
		DlmsMeterValueDto activeEnergyImportTariffTwo = new DlmsMeterValueDto(new BigDecimal("10.5"), DlmsUnitTypeDto.KWH);
		DlmsMeterValueDto activeEnergyExportTariffOne = new DlmsMeterValueDto(new BigDecimal("10.5"), DlmsUnitTypeDto.KWH);
		DlmsMeterValueDto activeEnergyExportTariffTwo = new DlmsMeterValueDto(new BigDecimal("10.5"), DlmsUnitTypeDto.KWH);
		
		List<PropMsg> props = makeProps(new Date(), activeEnergyImport, activeEnergyExport,
				activeEnergyImportTariffOne, activeEnergyImportTariffTwo, activeEnergyExportTariffOne, activeEnergyExportTariffTwo);
		
		return MsgMapper.makeResponseValues(props, ResponseStatus.OK, "FindEvents");
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
