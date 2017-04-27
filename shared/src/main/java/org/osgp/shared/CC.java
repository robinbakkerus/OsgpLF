package org.osgp.shared;

public interface CC {

	String PLATFORM_SYSTEM = "PlatformSystem";
//	String PLATFORM_REQUEST_ACTOR = "platformReqActor";
	String PLATFORM_RESPONSE_ACTOR = "platformRespActor";
//	int PLATFORM_PORT = 2542;
	
	String CORE_SYSTEM = "CoreSystem";
//	String CORE_REQUEST_ACTOR = "coreReqActor";
//	String CORE_RESPONSE_ACTOR = "coreRespActor";
//	int CORE_PORT = 2552;
	
	String DLMS_SYSTEM = "DlmsSystem";
//	String DLMS_REQUEST_ACTOR = "dlmsReqActor";
//	String DLMS_RESPONSE_ACTOR = "dlmsRespActor";
//	int DLMS_PORT = 2562;
	
	String DBS_NAMESPACE_CORE = "core";

    // used for serialized protobuf messages
    String PB_DATA = "data";
	String PREFIX_DEVICE = "DEV";
	String PREFIX_SCHEDULE = "SCH";

	// tijdelijk voor test-client
	int GPRC_DLMSSRV_PLATFORM_PORT = 50051;
	
	//organization
	String INFOSTROOM = "Infostroom"; 
	String TEST_ORG = "test-org"; //Green Power
	
	//Redis prefix-keys
	String RK_DEVICE_OPERATION = "do:";
	String RK_CORE_DEVICE = "cd:";
	String RK_DLMS_DEVICE = "dd:";
	String RK_BUNDLED_DEVOP = "bu:";
	String RK_PF_REQUEST_RESP = "rr:";
	String RK_CORE_SCHEDTASKS = "st:";
	String RK_CORE2PA_UNDELIVERED = "cu:";
	String RK_PF2CORE_UNDELIVERED = "pu:";
	
	//perst dbs names
	String PERST_DBS_CORE = "/var/perst/core.data";
	String PERST_DBS_PLATFORM = "/var/perst/platform.data";
	String PERST_DBS_DLMS = "/var/perst/dlms.data";
	String PERST_DBS_CLIENT = "/var/perst/client.data";
	String PERST_DBS_SMINT = "/var/perst/sm-int.data";
	String PERST_DBS_AUDITRAIL = "/var/perst/audit-trail.data";

}
