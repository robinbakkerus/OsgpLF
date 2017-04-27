package org.osgp.pa.dlms.service;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.dlms.MsgUtils;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.shared.exceptionhandling.TechnicalException;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseStatus;
import com.alliander.osgp.shared.ResponseValuesMsg;

/**
 * In deze service wordt door alle actions die in de RequestResponseMsg staan gelopen, en diegene die nog niet
 * verwerkt zijn worden alsnog (opnieuw) verwerkt, door een CommandExecutor die met behulp van een factory opgehaald
 * a.h.v de betreffende action. In dit geval wordt een stub gebruikt.
 *
 */
public class BundleServiceImpl implements BundleService {

	public List<DlmsActionMsg> callExecutors(final DlmsConnection connection, final DlmsDevice device,
			final RequestResponseMsg reqRespMsg) throws ProtocolAdapterException, TechnicalException {

		List<DlmsActionMsg> updatedActions = copyActions(reqRespMsg);
		ResponseValuesMsg actionResp = null;
		
		List<DlmsActionMsg> actions = MsgUtils.getDlmsActions(reqRespMsg);
		for (int i = 0; i < actions.size(); i++) {
			DlmsActionMsg action = actions.get(i);
			try {
				if (!isAlreadyHandled(action)) {
					actionResp = bundleExec().getExecutor(action).execute(connection, device, action);
					updatedActions.set(i, DlmsActionMsg.newBuilder(action).setResponse(actionResp).build());
				} else {
					updatedActions.set(i, action);
				}
			} catch (ExecutorNotFoundException ex) {
				throw new RuntimeException(ex);
			} 
		}

		return updatedActions;
	}

	private List<DlmsActionMsg> copyActions(final RequestResponseMsg reqRespMsg) throws TechnicalException {
		List<DlmsActionMsg> result = new ArrayList<>();
		for (DlmsActionMsg action : MsgUtils.getDlmsActions(reqRespMsg)) {
			result.add(action);
		}
		return result;
	}
	
	private boolean isAlreadyHandled(DlmsActionMsg action) {
		final ResponseValuesMsg actResp = action.getResponse();
		return !actResp.getStatus().equals(ResponseStatus.NOT_SET);
	}

	private BundleExecutor bundleExec() {
		return BundleExecutorFact.get();
	}
}
