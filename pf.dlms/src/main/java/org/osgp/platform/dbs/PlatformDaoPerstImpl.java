package org.osgp.platform.dbs;

import java.util.List;
import java.util.stream.Collectors;

import org.garret.perst.FieldIndex;
import org.osgp.platform.dbs.perst.PerstReqRespMsg;
import org.osgp.platform.dbs.perst.PlatformPerstRoot;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.UndeliveredTuple;
import org.osgp.shared.dbs.perst.PbMsgPerst;
import org.osgp.shared.dbs.perst.PerstUtils;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.perst.PerstSaveHelper;
import org.osgp.util.dao.perst.PerstSelectAllHelper;
import org.osgp.util.dao.perst.PerstSelectHelper;

import com.alliander.osgp.shared.CorrelIdMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

public class PlatformDaoPerstImpl implements PlatformDao, CC {

	public PlatformDaoPerstImpl() {
		super();
	}

	@Override
	public void saveRequestResponse(RequestResponseMsg reqRespMsg) {
		synchronized (PlatformDaoPerstImpl.class) {
			this.saveReqRespMsg(reqRespMsg.getCorrelId(), reqRespMsg.toByteArray());
		}
	}

	@Override
	public void saveRequestResponses(List<RequestResponseMsg> reqRespMsgList) {
		reqRespMsgList.forEach(this::saveRequestResponse);
	}

	@Override
	public RequestResponseMsg getResponse(String correlId) throws InvalidProtocolBufferException {
		synchronized (PlatformDaoPerstImpl.class) {
			PerstSelectHelper<PerstReqRespMsg, RequestResponseMsg> selecter = new PerstSelectHelper<>();
			return selecter.select(root().reqRespIndex, RequestResponseMsg.class, correlId);
		}
	}

	@Override
	public RequestResponseMsg getResponse(CorrelIdMsg responseCorrelIdMsg) throws InvalidProtocolBufferException {
		return this.getResponse(responseCorrelIdMsg.getCorrelid());
	}

	@Override
	public void saveUndeliveredRequest(RequestResponseMsg reqRespMsg) {
		this.saveUndeliveredMsg(reqRespMsg.getCorrelId(), reqRespMsg.toByteArray());
	}

	@Override
	public List<UndeliveredTuple> getAllUndeliveredRequests() {
		synchronized (PlatformDaoPerstImpl.class) {
			PerstSelectAllHelper<PerstReqRespMsg, RequestResponseMsg> selecter = new PerstSelectAllHelper<>();
			List<RequestResponseMsg> msglist = selecter.selectAll(root().undeliveredIndex, RequestResponseMsg.class);
			return msglist.stream().map(f -> new UndeliveredTuple(new PK(f.getCorrelId()), f))
					.collect(Collectors.toList());
		}
	}

	@Override
	public List<RequestResponseMsg> getAllRequestResponseMsgs() {
		synchronized (PlatformDaoPerstImpl.class) {
			PerstSelectAllHelper<PerstReqRespMsg, RequestResponseMsg> selecter = new PerstSelectAllHelper<>();
			return selecter.selectAll(root().reqRespIndex, RequestResponseMsg.class);
		}
	}

	@Override
	public void delete(PlatformTable table, PK pk) {
		synchronized (PlatformDaoPerstImpl.class) {
			byte[] bytes = getBytes(index(table), pk.perstKey());
			if (bytes != null) {
				PbMsgPerst obj = new PbMsgPerst(pk.perstKey(), bytes);
				index(table).remove(obj);
				obj.deallocate();
			}
		}
	}

	@Override
	public void deleteList(PlatformTable table, List<PK> pkList) {
		pkList.forEach(f -> this.delete(table, f));
	}

	private byte[] getBytes(final FieldIndex<?> index, final String key) {
		PbMsgPerst msg = (PbMsgPerst) index.get(key);
		return msg == null ? null : msg.body;
	}

	@Override
	public Object get(PlatformTable table, PK pk) {
		return null;
	}

	@Override
	public List<PK> getAllRequestResponseMsgPKs() {
		return PerstUtils.getAllPks(root().reqRespIndex);
	}

	private void saveReqRespMsg(final String key, final byte[] bytes) {
		synchronized (PlatformDaoPerstImpl.class) {
			PerstSaveHelper<PerstReqRespMsg> saver = new PerstSaveHelper<>();
			saver.save(root().reqRespIndex, new PerstReqRespMsg(key, bytes));
		}
	}

	private void saveUndeliveredMsg(final String key, final byte[] bytes) {
		synchronized (PlatformDaoPerstImpl.class) {
			PerstSaveHelper<PerstReqRespMsg> saver = new PerstSaveHelper<>();
			saver.save(root().undeliveredIndex, new PerstReqRespMsg(key, bytes));
		}
	}

	private FieldIndex<?> index(final PlatformTable table) {
		if (PlatformTable.CORE_UNDELIVERED.equals(table))
			return root().undeliveredIndex;
		return root().reqRespIndex;
	}

	private PlatformPerstRoot root() {
		return PlatformDbsMgr.INSTANCE.dbsMgr().getPerstRoot();
	}

}