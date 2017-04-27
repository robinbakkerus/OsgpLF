package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.HashMap;
import java.util.Map;

/**
 * This class maps all possible Dlms units with the corrresponding string that
 * are available as an enum in @see OsgpUnitType. The index property corresponds
 * with the 'unit' in the Blue book. The 'unit' string property corresponds with
 * the enum in OsgpUnitType. The additional 'quantity' and 'unitName' properties
 * also correspond with the values from the Blue book.
 */
public enum DlmsUnitTypeDto {

    UNDEFINED(0, "UNDEFINED", "", ""),
    YEAR(1, "YEAR", "", ""),
    MONTH(2, "MONTH", "", ""),
    WEEK(3, "WEEK", "", ""),
    DAY(4, "DAY", "", ""),
    HOUR(5, "HOUR", "", ""),
    MIN(6, "MIN", "", ""),
    SECOND(7, "SECOND", "", ""),
    DEGREE(8, "DEGREE", "", ""),
    DEGREE_CELCIUS(9, "DEGREE_CELCIUS", "", ""),
    CURRENCY(10, "CURRENCY", "", ""),
    METER(11, "METER", "", ""),
    METER_PER_SECOND(12, "METER_PER_SECOND", "", ""),
    M3(13, "M_3", "", ""),
    M3_CORR(14, "M_3", "", ""),
    M3_FLUX(15, "M_3_FLUX", "", ""),
    M3_FLUX_CORR(16, "M_3_FLUX_CORR", "", ""),
    VOLUME_FLUX(17, "VOLUME_FLUX", "", ""),
    VOLUME_FLUX_CORR(18, "VOLUME_FLUX_CORR", "", ""),
    LITRE(19, "LITRE", "", ""),
    KILOGRAM(20, "KILOGRAM", "", ""),
    NEWTON(21, "NEWTON", "", ""),
    NEWTON_METER(22, "NEWTON_METER", "", ""),
    PASCAL(23, "PASCAL", "", ""),
    BAR(24, "BAR", "", ""),
    JOULE(25, "JOULE", "", ""),
    JOULE_PER_HOUR(26, "JOULE_PER_HOUR", "", ""),
    WATT(27, "WATT", "", ""),
    VOLT_AMPERE(28, "VOLT_AMPERE", "", ""),
    VAR(29, "VAR", "", ""),
    KWH(30, "KWH", "", ""),
    VOLT_AMP_HOUR(31, "VOLT_AMP_HOUR", "", ""),
    VAR_HOUR(32, "VAR_HOUR", "", ""),
    AMPERE(33, "AMPERE", "", ""),
    COULOMB(34, "COULOMB", "", ""),
    VOLT(35, "VOLT", "", ""),
    VOLT_PER_METER(36, "VOLT_PER_METER", "", ""),
    FARAD(37, "FARAD", "", ""),
    OHM(38, "OHM", "", ""),
    RESTISTIVITY(39, "RESTISTIVITY", "", ""),
    WEBER(40, "WEBER", "", ""),
    TESLA(41, "TESLA", "", ""),
    AMP_PER_METER(42, "AMP_PER_METER", "", ""),
    HENRY(43, "HENRY", "", ""),
    HERTZ(44, "HERTZ", "", ""),
    ACTIVE_ENERGY(45, "ACTIVE_ENERGY", "", ""),
    REACTIVE_ENERGY(46, "REACTIVE_ENERGY", "", ""),
    APPARENT_ENERGY(47, "APPARENT_ENERGY", "", ""),
    VOLT_SQUARED_HOURS(48, "VOLT_SQUARED_HOURS", "", ""),
    AMP_SQUARED_HOURS(49, "AMP_SQUARED_HOURS", "", ""),
    KG_PER_SECOND(50, "KG_PER_SECOND", "", ""),
    SIEMENS(51, "SIEMENS", "", ""),
    KELVIN(52, "KELVIN", "", ""),
    VOLT_PULSE_VALUE(53, "VOLT_PULSE_VALUE", "", ""),
    AMP_PULSE_VALUE(54, "AMP_PULSE_VALUE", "", ""),
    VOLUME(55, "VOLUME", "", ""),
    PERCENTAGE(56, "PERCENTAGE", "", ""),
    AMP_HOUR(57, "AMP_HOUR", "", ""),
    ENGERY(60, "ENGERY", "", ""),
    WOBBE(61, "WOBBE", "", ""),
    MOLE_PERCENT(62, "MOLE_PERCENT", "", ""),
    MASS_DENSITY(63, "MASS_DENSITY", "", ""),
    PASCAL_SECOND(64, "UNDEFINED", "", ""),
    JOULE_KG(65, "JOULE_KG", "", ""),
    DB_MILLIWAT(70, "DB_MILLIWAT", "", ""),
    DB_MICROVOLT(71, "DB_MICROVOLT", "", ""),
    DB(72, "DB", "", ""),
    COUNT(255, "UNDEFINED", "", "");

    private static final Map<Integer, DlmsUnitTypeDto> UNIT_TYPES_MAP = new HashMap<Integer, DlmsUnitTypeDto>();

    static {
        for (DlmsUnitTypeDto unitType : DlmsUnitTypeDto.values()) {
            UNIT_TYPES_MAP.put(unitType.getIndex(), unitType);
        }
    }

    private final int index;
    private final String unit;
    private final String quantity;
    private final String unitName;

    private DlmsUnitTypeDto(int index, String unit, String quantity, String unitName) {
        this.index = index;
        this.unit = unit;
        this.quantity = quantity;
        this.unitName = unitName;
    }

    public static Map<Integer, DlmsUnitTypeDto> getUnitTypesMap() {
        return UNIT_TYPES_MAP;
    }

    public static DlmsUnitTypeDto getUnitType(final int index) {
        return UNIT_TYPES_MAP.get(index);
    }

    public static String getUnit(final int index) {
        DlmsUnitTypeDto unitType = getUnitType(index);
        return unitType == null ? UNDEFINED.getUnit() : unitType.getUnit();
    }

    public int getIndex() {
        return this.index;
    }

    public String getUnit() {
        return this.unit;
    }

    public String getQuantity() {
        return this.quantity;
    }

    public String getUnitName() {
        return this.unitName;
    }
}
