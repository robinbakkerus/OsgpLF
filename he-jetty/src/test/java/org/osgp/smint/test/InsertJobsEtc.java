package org.osgp.smint.test;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.osgp.smint.dao.SmIntDao;
import org.osgp.smint.dao.SmIntDaoFact;
import org.osgp.smint.dao.SmIntDbsMgr;
import org.osgp.smint.dao.SmIntTable;
import org.osgp.smint.test.dao.builders.JobAndRecipeBuilder;

import com.alliander.osgp.dlms.DeviceGroupMsg;
import com.alliander.osgp.dlms.JobMsg;
import com.alliander.osgp.dlms.RecipeMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

public class InsertJobsEtc {

	public static void main(String[] args) {
		SmIntDbsMgr.INSTANCE.open();
		final InsertJobsEtc inserter = new InsertJobsEtc();
		inserter.insertRecipes();
		inserter.insertDeviceGroups();
		inserter.insertJobs();
		inserter.insertReqResponses();
		SmIntDbsMgr.INSTANCE.close();
		
		System.out.println(DateTime.now(DateTimeZone.UTC).toDate());
	}

	public void insertRecipes() {
		RecipeMsg recipe1 = JobAndRecipeBuilder.makeRecipe(1);
		dao().saveRecipe(recipe1);
		RecipeMsg recipe2 = JobAndRecipeBuilder.makeRecipe(2);
		dao().saveRecipe(recipe2);
		System.out.println("inserted Recipes");		
	}
	
	public void insertDeviceGroups() {
		DeviceGroupMsg group1 = JobAndRecipeBuilder.makeSingleDeviceGroupMsg();
		dao().saveDeviceGroup(group1);
		DeviceGroupMsg group1k = JobAndRecipeBuilder.make1KDeviceGroupMsg();
		dao().saveDeviceGroup(group1k);
		DeviceGroupMsg group10k = JobAndRecipeBuilder.make10KDeviceGroupMsg();
		dao().saveDeviceGroup(group10k);
		DeviceGroupMsg group50k = JobAndRecipeBuilder.make50KDeviceGroupMsg();
		dao().saveDeviceGroup(group50k);
		DeviceGroupMsg group100k = JobAndRecipeBuilder.make100KDeviceGroupMsg();
		dao().saveDeviceGroup(group100k);
		DeviceGroupMsg group1M = JobAndRecipeBuilder.make1MDeviceGroupMsg();
		dao().saveDeviceGroup(group1M);
		System.out.println("inserted Devicegroups");
	}
	
	public void insertJobs() {
		JobMsg job1 = JobAndRecipeBuilder.makeJob(1L);
		dao().saveJob(job1);
		System.out.println("inserted Jobs");
		JobMsg job = dao().getJob(1L);
	}
	
	public void insertReqResponses() {
		RequestResponseMsg reqResp1 = JobAndRecipeBuilder.makeRequestResponseMsg(JobAndRecipeBuilder.JOB_ID_1);
		dao().saveRequestResponse(reqResp1, SmIntTable.RR_DONE);
		RequestResponseMsg reqResp2 = JobAndRecipeBuilder.makeRequestResponseMsg(JobAndRecipeBuilder.JOB_ID_1);
		dao().saveRequestResponse(reqResp2, SmIntTable.RR_DONE);
		RequestResponseMsg reqResp3 = JobAndRecipeBuilder.makeRequestResponseMsg(JobAndRecipeBuilder.JOB_ID_1);
		dao().saveRequestResponse(reqResp3, SmIntTable.RR_DONE);
		System.out.println("inserted RequestResponses");
	}
	
	//--
	private SmIntDao dao() {return SmIntDaoFact.INSTANCE.getDao();}

}
