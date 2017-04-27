package org.osgp.smint.dao.perst;

import org.garret.perst.FieldIndex;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;
import org.osgp.shared.CC;

public class SmIntPerstRoot extends Persistent implements CC {

	public FieldIndex<PerstJobMsg> jobIndex;
	public FieldIndex<PerstRecipeMsg> recipeIndex;
	public FieldIndex<PerstRequestResponseMsg> reqRespNewIndex;
	public FieldIndex<PerstRequestResponseMsg> reqRespSendIndex;
	public FieldIndex<PerstRequestResponseMsg> reqRespDoneIndex;
	public FieldIndex<PerstDeviceGroupMsg> devGroupIndex;

	public SmIntPerstRoot(Storage storage) {
		super(storage);
		jobIndex = storage.<PerstJobMsg> createFieldIndex(PerstJobMsg.class, "strKey", true);
		recipeIndex = storage.<PerstRecipeMsg> createFieldIndex(PerstRecipeMsg.class, "strKey", true);
		reqRespNewIndex = storage.<PerstRequestResponseMsg> createFieldIndex(PerstRequestResponseMsg.class, "strKey", true);
		reqRespSendIndex = storage.<PerstRequestResponseMsg> createFieldIndex(PerstRequestResponseMsg.class, "strKey", true);
		reqRespDoneIndex = storage.<PerstRequestResponseMsg> createFieldIndex(PerstRequestResponseMsg.class, "strKey", true);
		devGroupIndex = storage.<PerstDeviceGroupMsg> createFieldIndex(PerstDeviceGroupMsg.class, "strKey", true);
	}
	
}
