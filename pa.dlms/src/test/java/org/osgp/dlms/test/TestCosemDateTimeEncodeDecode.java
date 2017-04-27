package org.osgp.dlms.test;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;
import org.openmuc.jdlms.datatypes.CosemDateFormat;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.internal.DataConverter;
import org.openmuc.jdlms.internal.asn1.axdr.types.AxdrOctetString;
import org.openmuc.jdlms.internal.asn1.cosem.Data;
import org.openmuc.jdlms.internal.asn1.cosem.Selective_Access_Descriptor;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;

public class TestCosemDateTimeEncodeDecode {

	DlmsHelperService dlmsHelperService = new DlmsHelperService();
	
	@Test
	public void test1() {
		final DateTime beginDateTime = new DateTime(2017,1,1,0,0,0);
		final DataObject fromValue = this.dlmsHelperService.asDataObject(beginDateTime);
		CosemDateFormat cal1 = fromValue.getValue();
		Data data = new Data();
        data.setoctet_string(new AxdrOctetString(cal1.encode()));
       
        Selective_Access_Descriptor sad = new Selective_Access_Descriptor(null, data);
        DataObject accessParameter = DataConverter.convertDataToDataObject(sad.access_parameters);
        
        CosemDateTime cosemDt1 = CosemDateTime.decode((byte[]) accessParameter.getValue());
        Calendar cal2 = cosemDt1.toCalendar();
        Date date2 = cal2.getTime();
        System.out.println(date2);
	}
}
