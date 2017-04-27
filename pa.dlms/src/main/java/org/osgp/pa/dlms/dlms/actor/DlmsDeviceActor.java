package org.osgp.pa.dlms.dlms.actor;

import java.io.IOException;
import java.util.List;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.dlms.MsgUtils;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.device.Hls5ConnectorLite;
import org.osgp.pa.dlms.dlms.DlmsAkkaServer;
import org.osgp.pa.dlms.dlms.request.DlmsRequestTuple;
import org.osgp.pa.dlms.dlms.stats.DlmsStatistics;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.pa.dlms.service.BundleExecutorFact;
import org.osgp.pa.dlms.service.BundleService;
import org.osgp.pa.dlms.service.BundleServiceImpl;
import org.osgp.shared.CC;
import org.osgp.shared.exceptionhandling.FunctionalException;
import org.osgp.shared.exceptionhandling.TechnicalException;
import org.osgp.util.MsgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.shared.DeviceMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseStatus;

import akka.actor.UntypedActor;
import io.grpc.StatusRuntimeException;

/**
 * Deze (ad-hoc) actor is diegene, waarbij voor het eerst daadwerkelijk iets met
 * de inhoud van request actions wordt gedaan. Deze laat het echte werk over aan
 * BundleService, die door alle actions heen loopt en uiteindelijk een antwoord
 * teruggeeft. Dit antwoord dan teruggestuurd naar de CoreResponseActor
 */
public class DlmsDeviceActor extends UntypedActor implements CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(DlmsDeviceActor.class);

	private static boolean DO_RETRY = true;
	private static boolean NO_RETRY = false;

	private BundleService service = new BundleServiceImpl();

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof DlmsRequestTuple) {
			handleRequest((DlmsRequestTuple) msg);
		}
	}

	private static int count = 0;

	private void handleRequest(final DlmsRequestTuple dlmsRequestTuple) {
		LOGGER.debug("DlmsDevice actor called " + count++);
		DlmsConnection conn = null;
		RequestResponseMsg reqRespMsg = dlmsRequestTuple.getReqRespMsg();
		DlmsDevice dlmsDevice = dlmsRequestTuple.getDlmsDevice();
		try {
			conn = makeConnection(reqRespMsg.getDevice(), dlmsDevice);
			List<DlmsActionMsg> updatedActions = service.callExecutors(conn, dlmsDevice, reqRespMsg);
			RequestResponseMsg sendMsg = MsgUtils.updateActions(reqRespMsg, updatedActions);
			ResponseMsg response = ResponseMsg.newBuilder().setStatus(ResponseStatus.OK).build();
			sendResponseToCore(sendMsg, response, NO_RETRY);
		} catch (ProtocolAdapterException ex) {
			handleError(reqRespMsg, "protocol.adapter.exception", "protocol exception while handling request for "
					+ reqRespMsg.getCommon().getDeviceId() + ", " + ex, DO_RETRY);
		} catch (TechnicalException ex) {
			handleError(reqRespMsg, "technical.exception",
					"technical error while handling request for " + reqRespMsg.getCommon().getDeviceId() + ", " + ex, DO_RETRY);
		} catch (FunctionalException ex) {
			handleError(reqRespMsg, "functional.exception",
					"functional error while handling request for " + reqRespMsg.getCommon().getDeviceId() + ", " + ex, NO_RETRY);
		} catch (StatusRuntimeException sre) {
			handleError(reqRespMsg, "grpc.exception",
					"StatusRuntimeException error while handling request for " + reqRespMsg.getCommon().getDeviceId() + ", " + sre, NO_RETRY);
		}
		finally {
			closeConnection(conn);
			context().stop(self());
		}
	}

	private void handleError(RequestResponseMsg reqRespMsg, String code, String errmsg, boolean retry) {
		LOGGER.error(errmsg);
		ResponseMsg response = MsgMapper.simpleResponseMsg(ResponseStatus.NOT_OK, code, "error", errmsg);
		sendResponseToCore(reqRespMsg, response, retry);
	}

	private void sendResponseToCore(final RequestResponseMsg reqRespMsg, 
			final ResponseMsg response, final boolean retry) {
		RequestResponseMsg sendmsg = MsgMapper.makeSendReqRespToCore(reqRespMsg, response, retry);
		DlmsAkkaServer.sendResponsesActor.tell(sendmsg, self());
	}

	private void closeConnection(DlmsConnection conn) {
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (IOException e) {
				LOGGER.warn("error while closing dlmsconnection " + e);
			}
		}
	}

	private DlmsConnection makeConnection(final DeviceMsg deviceMsg, final DlmsDevice dlmsDevice)
			throws FunctionalException, TechnicalException {

		if (BundleExecutorFact.useStub()) {
			return null;
		} else {
			LOGGER.debug("make connection with " + deviceMsg.getNetworkAddress());
			final Hls5ConnectorLite connector = new Hls5ConnectorLite(dlmsDevice);
			return connector.connect();
		}
	}


}
