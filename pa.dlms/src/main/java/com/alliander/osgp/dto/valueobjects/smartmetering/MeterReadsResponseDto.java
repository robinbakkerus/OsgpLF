package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.Date;

public class MeterReadsResponseDto extends ActionResponseDto {
    private static final long serialVersionUID = -297320204916085999L;

    private final Date logTime;

    private final DlmsMeterValueDto activeEnergyImport;
    private final DlmsMeterValueDto activeEnergyExport;
    private final DlmsMeterValueDto activeEnergyImportTariffOne;
    // may be null
    private final DlmsMeterValueDto activeEnergyImportTariffTwo;
    private final DlmsMeterValueDto activeEnergyExportTariffOne;
    // may be null
    private final DlmsMeterValueDto activeEnergyExportTariffTwo;

    public MeterReadsResponseDto(final Date logTime, final DlmsMeterValueDto activeEnergyImport,
            final DlmsMeterValueDto activeEnergyExport, final DlmsMeterValueDto activeEnergyImportTariffOne,
            final DlmsMeterValueDto activeEnergyImportTariffTwo, final DlmsMeterValueDto activeEnergyExportTariffOne,
            final DlmsMeterValueDto activeEnergyExportTariffTwo) {
        super();
        this.logTime = new Date(logTime.getTime());
        this.activeEnergyImportTariffOne = activeEnergyImportTariffOne;
        this.activeEnergyImportTariffTwo = activeEnergyImportTariffTwo;
        this.activeEnergyExportTariffOne = activeEnergyExportTariffOne;
        this.activeEnergyExportTariffTwo = activeEnergyExportTariffTwo;
        this.activeEnergyImport = activeEnergyImport;
        this.activeEnergyExport = activeEnergyExport;
    }

    public Date getLogTime() {
        return new Date(this.logTime.getTime());
    }

    public DlmsMeterValueDto getActiveEnergyImportTariffOne() {
        return this.activeEnergyImportTariffOne;
    }

    public DlmsMeterValueDto getActiveEnergyImportTariffTwo() {
        return this.activeEnergyImportTariffTwo;
    }

    public DlmsMeterValueDto getActiveEnergyExportTariffOne() {
        return this.activeEnergyExportTariffOne;
    }

    public DlmsMeterValueDto getActiveEnergyExportTariffTwo() {
        return this.activeEnergyExportTariffTwo;
    }

    public DlmsMeterValueDto getActiveEnergyImport() {
        return this.activeEnergyImport;
    }

    public DlmsMeterValueDto getActiveEnergyExport() {
        return this.activeEnergyExport;
    }

    @Override
    public String toString() {
        return "MeterReads [logTime=" + this.logTime + ", activeEnergyImport=" + this.activeEnergyImport
                + ", activeEnergyExport=" + this.activeEnergyExport + ", activeEnergyImportTariffOne="
                + this.activeEnergyImportTariffOne + ", activeEnergyImportTariffTwo="
                + this.activeEnergyImportTariffTwo + ", activeEnergyExportTariffOne="
                + this.activeEnergyExportTariffOne + ", activeEnergyExportTariffTwo="
                + this.activeEnergyExportTariffTwo + "]";
    }

}
