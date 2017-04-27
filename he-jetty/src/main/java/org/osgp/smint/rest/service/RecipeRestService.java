package org.osgp.smint.rest.service;

import java.util.List;

import org.osgp.smint.dao.SmIntDao;
import org.osgp.smint.dao.SmIntDaoFact;
import org.osgp.smint.service.AbstractService;

import com.alliander.osgp.dlms.RecipeMsg;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

public class RecipeRestService extends AbstractService {


	public String getAllRecipes() {
		StringBuilder sb = startJsonArray();
		
		List<RecipeMsg> allMsg = dao().getAllrecipes();
		int i=0;
		for (RecipeMsg msg : allMsg) {
			addJson(msg, sb);
			if (i++ < allMsg.size()-1) sb.append(",");
		}
		return endJsonArray(sb);
	}

	public String getRecipe(final long recipeId) {
		StringBuilder sb = startJson();
		RecipeMsg msg = dao().getRecipe(recipeId);
		addJson(msg, sb);
		return endJson(sb);
	}

	public String addRecipe(String data) {
		System.out.println(data);
		try {
	        RecipeMsg.Builder builder = RecipeMsg.newBuilder();
			JsonFormat.parser().merge(data, builder);
			RecipeMsg msg = builder.build();
			dao().saveRecipe(msg);
			System.out.println(msg);
			return "ok";
		} catch (InvalidProtocolBufferException e) {
			return "error : " + e;
		}

	}
	//---------
	
	private SmIntDao dao() {
		return SmIntDaoFact.INSTANCE.getDao();
	}
	
}
