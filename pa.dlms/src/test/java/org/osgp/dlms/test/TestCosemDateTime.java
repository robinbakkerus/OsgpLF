package org.osgp.dlms.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.CosemDateFormat;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.internal.DataConverter;
import org.openmuc.jdlms.internal.asn1.axdr.types.AxdrOctetString;
import org.openmuc.jdlms.internal.asn1.cosem.Data;
import org.openmuc.jdlms.internal.asn1.cosem.Selective_Access_Descriptor;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;

public class TestCosemDateTime {

	DlmsHelperService dlmsHelperService = new DlmsHelperService();
	
	@Test
	public void test() {
		final CosemDateTime dateTime = new CosemDateTime(2017, 1, 1, 0xff, 1, 1, 0, 0, 0);
		Calendar cal = dateTime.toCalendar();
		System.out.println(cal);
	}

	@Ignore
	public void test2() {
//		final DateTime beginDateTime = new DateTime(2017,1,1,0,0,0);
//		final DataObject fromValue = this.dlmsHelperService.asDataObject(beginDateTime);
		
		final List<DataObject> rangeDescriptor = (List<DataObject>) getSelectiveAccess().getAccessParameter()
                .getValue();
		
		CosemDateTime cosemDt1 = CosemDateTime.decode((byte[]) rangeDescriptor.get(1).getValue());
		Calendar cal = cosemDt1.toCalendar();
		System.out.println(cal.getTime());
	}
	
	@Test
	public void test3() {
		final DateTime beginDateTime = new DateTime(2017,1,1,0,0,0);
		final DataObject fromValue = this.dlmsHelperService.asDataObject(beginDateTime);
		CosemDateFormat cal = fromValue.getValue();
		Data data = new Data();
        data.setoctet_string(new AxdrOctetString(cal.encode()));
       
        Selective_Access_Descriptor sad = new Selective_Access_Descriptor(null, data);
        DataObject accessParameter = DataConverter.convertDataToDataObject(sad.access_parameters);
        
        CosemDateTime cosemDt1 = CosemDateTime.decode((byte[]) accessParameter.getValue());
        System.out.println(cosemDt1.toCalendar());
	}
	
	private SelectiveAccessDescription getSelectiveAccess() {
		final DataObject clockDefinition = this.dlmsHelperService.getClockDefinition();

		final int accessSelector = 1;
		final DateTime beginDateTime = new DateTime(2017,1,1,0,0,0);
		final DateTime endDateTime = new DateTime(2017,2,1,0,0,0);

		final DataObject fromValue = this.dlmsHelperService.asDataObject(beginDateTime);
		final DataObject toValue = this.dlmsHelperService.asDataObject(endDateTime);
				
		final List<DataObject> objectDefinitions = new ArrayList<>();
		final DataObject selectedValues = DataObject.newArrayData(objectDefinitions);

		final DataObject accessParameter = DataObject
				.newStructureData(Arrays.asList(clockDefinition, fromValue, toValue, selectedValues));
		
		SelectiveAccessDescription selcAccess = new SelectiveAccessDescription(accessSelector, accessParameter);
		return selcAccess;
	}


	
}
