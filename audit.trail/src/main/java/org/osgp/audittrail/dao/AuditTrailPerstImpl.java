package org.osgp.audittrail.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.garret.perst.FieldIndex;
import org.osgp.audittrail.dao.perst.PerstAuditTrailMsg;
import org.osgp.util.dao.PK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

public class AuditTrailPerstImpl implements AuditTrailDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuditTrailPerstImpl.class);
	

	@Override
	public void saveRequestResponseMsg(RequestResponseMsg reqRespMsg) {
		String correlId = reqRespMsg.getCorrelId();
		save(correlId, reqRespMsg.toByteArray());
	}

	@Override
	public RequestResponseMsg getRequestResponseMsg(String correlId) {
		byte[] bytes = getBytes(correlId);
		try {
			if (bytes != null) {
				return RequestResponseMsg.parseFrom(bytes);
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	@Override
	public List<PK> getAllRequestResponseMsgPKs() {
		return getAllPks(auditTrailIndex()).stream().map(s -> new PK(s)).collect(Collectors.toList());	
	}

	@Override
	public void delete(PK pk) {
		byte[] bytes = getBytes(pk.perstKey());
		if (bytes != null) {
			PerstAuditTrailMsg obj = new PerstAuditTrailMsg(pk.perstKey(), bytes);
			auditTrailIndex().remove(obj);
			obj.deallocate();
		}
	}

	private void save(final String key, final byte[] bytes) {
		save(auditTrailIndex(), new PerstAuditTrailMsg(key, bytes));
	}
	
	public byte[] getBytes(final String key) {
		PerstAuditTrailMsg msg = auditTrailIndex().get(key);
		return msg==null ? null : msg.body;
	}
	

	//---
	
	private List<String> scanAll(FieldIndex<PerstAuditTrailMsg> fieldIndex) {
		List<String> r = new ArrayList<>();
		Iterator<PerstAuditTrailMsg> iter = fieldIndex.iterator();
		while (iter.hasNext()) {
			PerstAuditTrailMsg perstMsg = (PerstAuditTrailMsg) iter.next();
			r.add(perstMsg.strKey);
		}
		return r;
	}
	
	public List<PK> getAllPks(FieldIndex<PerstAuditTrailMsg> fieldIndex) {
		List<PK> r = new ArrayList<>();
		for (String key : scanAll(fieldIndex)) {
			try {
				r.add(new PK(key));
			} catch (Exception e) {
				LOGGER.error("error parsing record " + e);
			}
		}
		return r;
	}
	
	public void save(FieldIndex<PerstAuditTrailMsg> fieldIndex, final PerstAuditTrailMsg perstMsg) {
		try {
			if (fieldIndex.get(perstMsg.strKey) != null) {
				fieldIndex.remove(perstMsg);
			}
			fieldIndex.put(perstMsg);
		} catch(Exception ex) {
			LOGGER.error("error while saving " + perstMsg + " in " + " " + ex);
		}
	}
	
	//--------------
	
	private FieldIndex<PerstAuditTrailMsg> auditTrailIndex() {
		return AuditTrailDbsMgr.INSTANCE.dbsMgr().getPerstRoot().auditTrailIndex;
	}
	

}
