package org.osgp.client.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgp.client.dao.ClientDaoFact;
import org.osgp.client.dao.DeviceOperationTuple;
import org.osgp.util.dao.PK;

import com.alliander.osgp.dlms.DlmsDevOperMsg;

public class MapDevOperations {

//	private Logger LOGGER = LoggerFactory.getLogger(MapDevOperations.class);
	
	private Map<MapDevOperKey, Set<PK>> devOperMap;
	
	public Map<MapDevOperKey, Set<PK>> makeMap() {
		devOperMap = new HashMap<>();
    	scanDeviceOperations();
		return devOperMap;
	}
	
    public void scanDeviceOperations() {
    	List<DeviceOperationTuple> devopTuples = ClientDaoFact.INSTANCE.getDao().getAllDeviceOperations();
    	for (DeviceOperationTuple tuple : devopTuples) {
    		updateMap(tuple.getPk(), tuple.getDeviceOperation());
    	}
    }

    private void updateMap(final PK pk, final DlmsDevOperMsg devoper) {
    	MapDevOperKey mapkey = new MapDevOperKey(devoper.getCommon().getDeviceId(), 
    			devoper.getCommon().getScheduleTime(),
    			DevOperType.BUNDLE_OPERATION); //todo
    	
    	if (devOperMap.containsKey(mapkey)) {
    		Set<PK> ids = devOperMap.get(mapkey);
    		ids.add(pk);
    	} else {
    		Set<PK> newset = new HashSet<>();
    		newset.add(pk);
    		devOperMap.put(mapkey, newset);
    	}
    }
    
 }
