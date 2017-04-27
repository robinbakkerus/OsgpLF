package org.osgp.smint.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.garret.perst.FieldIndex;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.perst.PbMsgPerst;
import org.osgp.shared.dbs.perst.PerstUtils;
import org.osgp.smint.dao.perst.PerstDeviceGroupMsg;
import org.osgp.smint.dao.perst.PerstJobMsg;
import org.osgp.smint.dao.perst.PerstRecipeMsg;
import org.osgp.smint.dao.perst.PerstRequestResponseMsg;
import org.osgp.smint.dao.perst.SmIntPerstRoot;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.perst.PerstSaveHelper;
import org.osgp.util.dao.perst.PerstSelectAllHelper;
import org.osgp.util.dao.perst.PerstSelectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DeviceGroupMsg;
import com.alliander.osgp.dlms.JobMsg;
import com.alliander.osgp.dlms.RecipeMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

public class SmIntDaoPerstImpl implements SmIntDao, CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmIntDaoPerstImpl.class);

	private static final Map<SmIntTable, FieldIndex<?>> FIELD_INDEX_MAP = new HashMap<>();
	static {
		FIELD_INDEX_MAP.put(SmIntTable.RR_NEW, root().reqRespNewIndex);
		FIELD_INDEX_MAP.put(SmIntTable.RR_SEND, root().reqRespSendIndex);
		FIELD_INDEX_MAP.put(SmIntTable.RR_DONE, root().reqRespDoneIndex);
		FIELD_INDEX_MAP.put(SmIntTable.JOB, root().jobIndex);
		FIELD_INDEX_MAP.put(SmIntTable.RECIPE, root().recipeIndex);
	}

	@Override
	public void delete(final SmIntTable table, final PK pk) {
		synchronized (SmIntDaoPerstImpl.class) {
			byte[] bytes = getBytes(getIndex(table), pk.perstKey());
			if (bytes != null) {
				PbMsgPerst obj = new PbMsgPerst(pk.perstKey(), bytes);
				getIndex(table).remove(obj);
				obj.deallocate();
			}
		}
	}

	@Override
	public void deleteList(final SmIntTable table, List<PK> pkList) {
		pkList.forEach(f -> this.delete(table, f));
	}
	
	@Override
	public byte[] get(final SmIntTable table, final PK pk) {
		synchronized (SmIntDaoPerstImpl.class) {
			return null; //TODO
		}
	}


	private byte[] getBytes(final FieldIndex<?> index, final String key) {
		PbMsgPerst msg = (PbMsgPerst) index.get(key);
		return msg == null ? null : msg.body;
	}

	// ----------- HeGui ---------------

	@Override
	public List<JobMsg> getAllJobs() {
		synchronized (SmIntDaoPerstImpl.class) {
			List<JobMsg> r = new ArrayList<>();
			for (String key : PerstUtils.scanAll(jobIndex(), null)) {
				try {
					r.add(JobMsg.parseFrom(getBytes(jobIndex(), key)));
				} catch (Exception e) {
					LOGGER.error("error parsing record " + e);
				}
			}
			return r;
		}
	}

	@Override
	public void saveJob(JobMsg job) {
		synchronized (SmIntDaoPerstImpl.class) {
			PerstSaveHelper<PerstJobMsg> saver = new PerstSaveHelper<>();
			saver.save(jobIndex(), new PerstJobMsg(key(job.getId()), job.toByteArray()));	
		}
	}


	@Override
	public JobMsg getJob(long id) {
		PK pk = new PK(key(id));
		byte[] bytes = getBytes(jobIndex(), pk.perstKey());
		if (bytes != null) {
			try {
				return JobMsg.parseFrom(bytes);
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	// --- recipe

	@Override
	public void saveRecipe(RecipeMsg recipe) {
		synchronized (SmIntDaoPerstImpl.class) {
			PerstSaveHelper<PerstRecipeMsg> saver = new PerstSaveHelper<>();
			saver.save(recipeIndex(), new PerstRecipeMsg(key(recipe.getId()), recipe.toByteArray()));
		}
	}

	@Override
	public List<RecipeMsg> getAllrecipes() {
		synchronized (SmIntDaoPerstImpl.class) {
			List<RecipeMsg> r = new ArrayList<>();
			for (String key : PerstUtils.scanAll(recipeIndex(), null)) {
				try {
					r.add(RecipeMsg.parseFrom(getBytes(recipeIndex(), key)));
				} catch (Exception e) {
					LOGGER.error("error parsing record " + e);
				}
			}
			return r;
		}
	}


	@Override
	public RecipeMsg getRecipe(final long id) {
		PK pk = new PK(id);
		byte[] bytes = getBytes(recipeIndex(), pk.perstKey());
		if (bytes != null) {
			try {
				return RecipeMsg.parseFrom(bytes);
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	// RequestResponseMsg

	@Override
	public void saveRequestResponse(RequestResponseMsg reqRespMsg, SmIntTable table) {
		synchronized (SmIntDaoPerstImpl.class) {
			PerstSaveHelper<PerstRequestResponseMsg> saver = new PerstSaveHelper<>();
			saver.save(reqRespIndex(table), new PerstRequestResponseMsg(reqRespMsg.getCorrelId(), reqRespMsg.toByteArray()));
		}
	}

	@Override
	public void saveRequestResponses(List<RequestResponseMsg> reqRespMsgList, SmIntTable table) {
		reqRespMsgList.forEach(f -> saveRequestResponse(f, table));
	}

	@Override
	public RequestResponseMsg getRequestResponse(String correlId, SmIntTable table) {
		synchronized (SmIntDaoPerstImpl.class) {
			PerstSelectHelper<PerstRequestResponseMsg, RequestResponseMsg> selecter = new PerstSelectHelper<>();
			return selecter.select(reqRespIndex(table), RequestResponseMsg.class, correlId);
		}
	}	

	@Override
	public List<RequestResponseMsg> getAllRequestResponses(SmIntTable table) {
		synchronized (SmIntDaoPerstImpl.class) {
			PerstSelectAllHelper<PerstRequestResponseMsg, RequestResponseMsg> selecter = new PerstSelectAllHelper<>();
			return selecter.selectAll(reqRespIndex(table), RequestResponseMsg.class);
		}
	}	
	
	@Override
	public List<RequestResponseMsg> getRequestResponsesByJobId(long jobId, SmIntTable table) {
		return getAllRequestResponses(table).stream().filter(f -> f.getCommon().getJobId() == jobId).collect(Collectors.toList());
	}

	
	//--- DeviceGroup
	
	@Override
	public void saveDeviceGroup(DeviceGroupMsg deviceGroupMsg) {
		synchronized (SmIntDaoPerstImpl.class) {
			PerstSaveHelper<PerstDeviceGroupMsg> saver = new PerstSaveHelper<>();
			saver.save(devGroupIndex(), new PerstDeviceGroupMsg(key(deviceGroupMsg.getId()), deviceGroupMsg.toByteArray()));
		}
	}


	@Override
	public List<DeviceGroupMsg> getDeviceGroups() {
		synchronized (SmIntDaoPerstImpl.class) {
			PerstSelectAllHelper<PerstDeviceGroupMsg, DeviceGroupMsg> selecter = new PerstSelectAllHelper<>();
			return selecter.selectAll(devGroupIndex(), DeviceGroupMsg.class);
		}
	}	

	@Override
	public DeviceGroupMsg getDeviceGroup(final long id) {
		synchronized (SmIntDaoPerstImpl.class) {
			PerstSelectHelper<PerstDeviceGroupMsg, DeviceGroupMsg> selecter = new PerstSelectHelper<>();
			return selecter.select(devGroupIndex(), DeviceGroupMsg.class, ""+id);
		}
	}	

	// ---- indexes ----------
	
	private FieldIndex<PerstJobMsg> jobIndex() {
		return root().jobIndex;
	}

	private FieldIndex<PerstRecipeMsg> recipeIndex() {
		return root().recipeIndex;
	}

	private FieldIndex<PerstDeviceGroupMsg> devGroupIndex() {
		return root().devGroupIndex;
	}

	private FieldIndex<PerstRequestResponseMsg> reqRespIndex(final SmIntTable table) {
		if (SmIntTable.RR_NEW.equals(table)) return root().reqRespNewIndex;
		if (SmIntTable.RR_SEND.equals(table)) return root().reqRespSendIndex;
		return root().reqRespDoneIndex;
	}

	private FieldIndex<?> getIndex(final SmIntTable table) {
		if (SmIntTable.RR_NEW.equals(table)) return root().reqRespNewIndex;
		return reqRespIndex(table);
	}

	private static SmIntPerstRoot root() {
		return SmIntDbsMgr.INSTANCE.dbsMgr().getPerstRoot();
	}

	//--
	private String key(final Long id) {return id.toString();}
	//	private String makeUniqueId() {return UUID.randomUUID().toString();}
	//	private String key(final String prefix, final String keyValue) {return prefix + keyValue;}

}
