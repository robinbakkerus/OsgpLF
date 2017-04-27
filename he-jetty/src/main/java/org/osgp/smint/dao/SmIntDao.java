package org.osgp.smint.dao;

import java.util.List;

import org.osgp.util.dao.PK;

import com.alliander.osgp.dlms.DeviceGroupMsg;
import com.alliander.osgp.dlms.JobMsg;
import com.alliander.osgp.dlms.RecipeMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

public interface SmIntDao {

	void delete(final SmIntTable table, final PK pk);
	void deleteList(final SmIntTable table, final List<PK> pkList);
	Object get(final SmIntTable table, final PK pk);
	
	void saveJob(JobMsg job);
	List<JobMsg> getAllJobs();
	JobMsg getJob(long id);
	
	void saveRecipe(RecipeMsg recipe);
	List<RecipeMsg> getAllrecipes();
	RecipeMsg getRecipe(long recipeId);

	void saveRequestResponse(final RequestResponseMsg reqRespMsg, final SmIntTable table);
	void saveRequestResponses(final List<RequestResponseMsg> reqRespMsgList, final SmIntTable table);
	List<RequestResponseMsg> getAllRequestResponses(final SmIntTable table);
	RequestResponseMsg getRequestResponse(final String correlId, final SmIntTable table);
	List<RequestResponseMsg> getRequestResponsesByJobId(final long jobId, final SmIntTable table);
	
	void saveDeviceGroup(final DeviceGroupMsg deviceGroupMsg);
	List<DeviceGroupMsg> getDeviceGroups();
	DeviceGroupMsg getDeviceGroup(final long groupId);
}

