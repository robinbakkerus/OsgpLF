package org.osgp.smint.test.dao;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgp.smint.dao.SmIntDao;
import org.osgp.smint.dao.SmIntDaoFact;
import org.osgp.smint.dao.SmIntDbsMgr;
import org.osgp.smint.test.dao.builders.JobAndRecipeBuilder;

import com.alliander.osgp.dlms.JobMsg;
import com.alliander.osgp.dlms.RecipeMsg;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.protobuf.util.JsonFormat;

public class TestReadWrite {

//	private static final Logger LOGGER = LoggerFactory.getLogger(TestReadWrite.class.getName());

	@BeforeClass
	public static void before() {
		SmIntDbsMgr.INSTANCE.open();
	}
	
	@AfterClass
	public static void after() {
		System.out.println("after");
		SmIntDbsMgr.INSTANCE.close();
	}
	
//	@Test
//	public void testPerformanceAeroSpike() {
//		testPerformance(Database.Aerospike);
//	}
//
//	@Test
//	public void testPerformanceRedis() {
//		testPerformance(Database.Redis);
//	}
	
	@Test
	public void testJob() {
		JobMsg job = JobAndRecipeBuilder.makeJob(1);
		dao().saveJob(job);
		List<JobMsg> allJobs = dao().getAllJobs();
		System.out.println(allJobs.get(0));
		JobMsg job1 = (JobMsg) dao().getJob(job.getId());
		System.out.println(job1);
	}
	
	@Test
	public void testRecipe() {
		RecipeMsg recipe = JobAndRecipeBuilder.makeRecipe(1);
		dao().saveRecipe(recipe);
		List<RecipeMsg> allRecipes = dao().getAllrecipes();
		System.out.println(allRecipes.get(0));
		RecipeMsg recipe1 = (RecipeMsg) dao().getRecipe(recipe.getId());
		System.out.println(recipe1);
	}
	
	@Test
	public void testJobFromToJson() throws IOException {
		JobMsg job1 = JobAndRecipeBuilder.makeJob(1);
		String json = JsonFormat.printer().print(job1);
		System.out.println(json);
		JobMsg.Builder job2 = JobMsg.newBuilder();
		JsonFormat.parser().merge(json, job2);
		System.out.println(job2);
		
//		URL url = Resources.getResource("recipe2.json");
//		String json2 = Resources.toString(url, Charsets.UTF_8);
//		JobMsg.Builder recipe3 = JobMsg.newBuilder();
//		JsonFormat.parser().merge(json2, recipe3);
//		System.out.println(recipe3);
	}
	@Test
	public void testRecipeFromToJson() throws IOException {
		RecipeMsg recipe1 = JobAndRecipeBuilder.makeRecipe(1);
		String json = JsonFormat.printer().print(recipe1);
		System.out.println(json);
		RecipeMsg.Builder recipe2 = RecipeMsg.newBuilder();
		JsonFormat.parser().merge(json, recipe2);
		System.out.println(recipe2);
		
		URL url = Resources.getResource("recipe3.json");
		String json2 = Resources.toString(url, Charsets.UTF_8);
		RecipeMsg.Builder recipe3 = RecipeMsg.newBuilder();
		JsonFormat.parser().merge(json2, recipe3);
		System.out.println(recipe3);
	}

	private SmIntDao dao() {
		return SmIntDaoFact.INSTANCE.getDao();
	}

}
