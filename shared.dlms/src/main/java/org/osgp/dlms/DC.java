package org.osgp.dlms;

public interface DC {

	// constants for database
    String TABLE_DLMS_DEVICE = "dlms_device";
//    String TABLE_DLMS_SECKEY = "dlms_seckey";
//    
//    String INDEX_SECKEY_NAME = "dlms_seckey_idx";
//    String INDEX_SECKEY_BIN = "seckey_devid";


    // used for serialized protobuf messages
    String PB_DATA = "data";
	String PREFIX_DEVICE = "DEV";
//	String PREFIX_SECURITY_KEY = "DEV";
	
	// dbs namespace
	String DBS_NAMESPACE_PLATFORM = "platform";
	String DBS_NAMESPACE_DLMS = "dlms";
	String DBS_NAMESPACE_SMINT = "smint";
	
	String TABLE_REQRESP_MSG = "request_response_data"; 
	String TABLE_UNDELIVERED_CORE = "undelivered_pf_core";

	// dbs bins
	String DBS_BIN_DEVOPER = "devoper";
	String DBS_BIN_BUNDLE_SEND = "bundle-send";
	String DBS_BIN_BUNDLE_DONE = "bundle-done";
	String BIN_UNDELIVERED_CORE = "ud_pf_core";
	
	String CORRELID = "correlid";
	String CREATED_AT = "created_at";
	String MODIFIED_AT = "modified_at";
	String REQ_RESP_MSG = "req_resp_msg";
	String STATUS_MSG = "status_msg";


	// response codes
	String RC_FUNCTION_ERROR = "functional-error";
	String RC_FIND_EVENTS = "find-event";
	String RC_GET_ACTUAL_METER_READ = "get-actual-meter-read";
	String RC_GET_SPECIFIC_OBJ = "get-specific-object";
	
	 
}
