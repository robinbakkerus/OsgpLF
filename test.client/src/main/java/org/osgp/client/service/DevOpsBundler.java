package org.osgp.client.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgp.client.dao.ClientDaoFact;
import org.osgp.dlms.MsgUtils;
import org.osgp.shared.CC;
import org.osgp.util.CorrelId;
import org.osgp.util.dao.PK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.DlmsDevOperMsg;
import com.alliander.osgp.dlms.DlmsSpecificMsg;
import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

public class DevOpsBundler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DevOpsBundler.class);
	
	public void execute()  {
		MapDevOperations mapper = new MapDevOperations();
		final Map<MapDevOperKey, Set<PK>> devopermap = mapper.makeMap();
		processDeviceOperations(devopermap);
		int devopsCnt = 0;
		for (Set<PK> keyset : devopermap.values()) {
			devopsCnt += keyset.size();
		}
		System.out.println("found " + devopsCnt + " device operations");
		System.out.println("and generated " + devopermap.keySet().size() + " bundles");
	}

	/**
	 * this method generates a RequestResponseMsg from the selected
	 * DevOperMsg(-es) sent a request to the osgp platform, stores the result
	 * correlid in another 'bundle-send' table and deletes the processe device
	 * operation from the 'devoper' table
	 * 
	 * @param devopermap
	 * @param mapkey
	 */
	private void processDeviceOperations(final Map<MapDevOperKey, Set<PK>> devopermap) {
		int cnt = 0;
		try {
			for (MapDevOperKey mapkey : devopermap.keySet()) {
				Set<PK> pks = devopermap.get(mapkey);
				RequestResponseMsg request = makeDlmsReqRespMsg(mapkey, pks);
				storeRequest(RequestResponseMsg.newBuilder(request).build(), pks);
				if (cnt++ % 10000 == 0)
					System.out.print(".");
			}
			System.out.println("");
		} catch (RuntimeException e) {
			throw e;
		}
	}

	private void storeRequest(RequestResponseMsg reqRespMsg, Set<PK> pks) {
		String correlid = ClientDaoFact.INSTANCE.getDao().saveBundledDeviceOperation(reqRespMsg);
		LOGGER.debug("saving RequestResponseMsg with correlid " + correlid);
		for (PK delkey : pks) {
			ClientDaoFact.INSTANCE.getDao().delete(delkey);
		}
	}

	private RequestResponseMsg makeDlmsReqRespMsg(MapDevOperKey mapKey, Set<PK> pks) {
		List<DlmsActionMsg> actions = new ArrayList<>();
		for (PK pk : pks) {
			actions.add(buildAction(pk));
		}

		DlmsSpecificMsg specificMsg = DlmsSpecificMsg.newBuilder().addAllActions(actions).build();

		return RequestResponseMsg.newBuilder().setCommon(makeCommon(mapKey)).setCorrelId(CorrelId.generate())
				.setAction(MsgUtils.makeDlmsAction(specificMsg)).build();
	}

	private CommonMsg makeCommon(MapDevOperKey mapKey) {
		return CommonMsg.newBuilder().setApplicationName("Appname").setOrganisation(CC.INFOSTROOM)
				.setDeviceId(mapKey.getDeviceId()).setUserName("robinb").build();
	}

	private DlmsActionMsg buildAction(final PK pk) {
		DlmsDevOperMsg devoper = ClientDaoFact.INSTANCE.getDao().getDeviceOperation(pk);
		return devoper == null ? null : devoper.getAction();
	}


}
