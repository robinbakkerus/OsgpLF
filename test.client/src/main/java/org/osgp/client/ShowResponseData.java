package org.osgp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.osgp.dlms.DC;
import org.osgp.dlms.MsgUtils;
import org.osgp.platform.dbs.PlatformDaoFact;
import org.osgp.platform.dbs.PlatformDbsMgr;
import org.osgp.platform.dbs.PlatformTable;
import org.osgp.shared.exceptionhandling.TechnicalException;
import org.osgp.util.MsgMapper;
import org.osgp.util.dao.PK;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.DlmsSpecificMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

public class ShowResponseData implements DC {

	//private static final Logger LOGGER = LoggerFactory.getLogger(ShowResponseData.class.getName());

	public static void main(String[] args) throws Exception {
		try {
			PlatformDbsMgr.INSTANCE.open();
			ShowResponseData client = new ShowResponseData();
			client.execute();
		} finally {
			PlatformDbsMgr.INSTANCE.close();
		}
	}

	public void execute() throws InvalidProtocolBufferException {
		
		List<PK> keys = PlatformDaoFact.INSTANCE.getDao().getAllRequestResponseMsgPKs();
		if (keys.size() == 0) {
			System.out.println("no responses left");
		} else {
			int cnt = 0;
			String action = askShowResponseAction();
			while (action.equals("V")) {
				printResponses(cnt, keys);
				cnt++;
				if (cnt < keys.size()) {
					action = askShowResponseAction();
				} else {
					action = "Q";
					System.err.println("no more responses left");
				}
			}
			deleteResponses(cnt, keys);
		}
	}

	private void printResponses(int index, List<PK> keys) throws InvalidProtocolBufferException {
		try {
			PK pk = keys.get(index);
			RequestResponseMsg reqRespMsg = PlatformDaoFact.INSTANCE.getDao().getResponse(pk.getKey().toString());
			System.out.println(MsgMapper.formatResponse(reqRespMsg.getResponse()));
			DlmsSpecificMsg specificMsg = getDlmsSpecific(reqRespMsg);
			System.out.println("Action response(s) : ");
			for (DlmsActionMsg actionMsg : specificMsg.getActionsList()) {
				System.out.println(actionMsg.getRequestType());
//				System.out.println(MsgMapper.format(actionMsg.getResponse()));
			}
			PlatformDaoFact.INSTANCE.getDao().delete(PlatformTable.REQ_RESP, pk);
		} catch (TechnicalException e) {
			e.printStackTrace();
		}
	}

	private void deleteResponses(int cnt, List<PK> keys) {
		for (int i=cnt; i<keys.size(); i++) {
			PK pk = keys.get(i);
			PlatformDaoFact.INSTANCE.getDao().delete(PlatformTable.REQ_RESP, pk);
		}
	}

	private DlmsSpecificMsg getDlmsSpecific(RequestResponseMsg reqRespMsg) throws TechnicalException {
		return MsgUtils.getDlmsSpecific(reqRespMsg);
		
	}
	
	private String askShowResponseAction() {
		System.out.println("Wat wil je zien (V=volgende, Q=stop) [V]  : ");
		try {
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			String r = bufferRead.readLine();
			if (r.isEmpty() || r.toUpperCase().startsWith("V")) {
				return "V";
			} else {
				return "Q";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "Q";
		}
	}

  
}
