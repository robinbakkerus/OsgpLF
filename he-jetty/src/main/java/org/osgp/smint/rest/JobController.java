package org.osgp.smint.rest;

import static org.osgp.smint.util.JsonUtil.toJson;
import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

import org.osgp.smint.rest.service.DeviceGroupRestService;
import org.osgp.smint.rest.service.JobRestService;
import org.osgp.smint.rest.service.RecipeRestService;
import org.osgp.smint.rest.service.StatisticsRestService;
import org.osgp.smint.rest.util.CorsHeaders;


public class JobController {


	public JobController(final JobRestService jobService, 
			final RecipeRestService recipeService,
			final DeviceGroupRestService deviceGroupService,
			final StatisticsRestService statisticsService) {

		CorsHeaders.enableCORS("http://localhost:9000", "*", "*");
//		CorsHeaders.enableCORS("http://localhost:8080", "*", "*");
//		CorsHeaders.enableCORS("http://localhost:63342", "*", "*");
				
		get("/jobs", (req, res) -> jobService.getAllJobs());

		get("/job/:id", (req, res) -> {
			String id = req.params(":id");
			String job = jobService.getJob(id);
			if (job != null) {
				return job;
			}
			res.status(400);
			return new ResponseError("No job with id '%s' found", id);
		});

		post("/addJob", (req, res) -> jobService.addJob(req.body()));

		get("/recipes", (req, res) -> recipeService.getAllRecipes());

		get("/recipe/:id", (req, res) -> {
			String id = req.params(":id");
			String recipe = recipeService.getRecipe(Long.parseLong(id));
			if (recipe != null) {
				return recipe;
			}
			res.status(400);
			return new ResponseError("No recipe with id '%s' found", id);
		});

		post("/addRecipe", (req, res) -> recipeService.addRecipe(req.body()));

		get("/deviceGroups", (req, res) -> deviceGroupService.getAllDeviceGroups());
		
		get("/deviceGroup/:id", (req, res) -> {
			String id = req.params(":id");
			String recipe = deviceGroupService.getDeviceGroup(Long.parseLong(id));
			if (recipe != null) {
				return recipe;
			}
			res.status(400);
			return new ResponseError("No recipe with id '%s' found", id);
		});		

		get("/resetStatistics", (req, res) -> statisticsService.resetStatics());
		get("/statistics", (req, res) -> statisticsService.getStatics());

		get("/response/:correlId", (req, res) -> {
			String correlId = req.params(":correlId");
			String recipe = jobService.getResponse(correlId);
			if (recipe != null) {
				return recipe;
			}
			res.status(400);
			return new ResponseError("No response for '%s' found", correlId);
		});
		

		after((req, res) -> {
			res.type("application/json");
		});

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(toJson(new ResponseError(e)));
		});
	}
	

}
