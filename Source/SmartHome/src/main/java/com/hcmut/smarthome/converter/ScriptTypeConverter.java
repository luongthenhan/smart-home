package com.hcmut.smarthome.converter;

import javax.transaction.NotSupportedException;

import com.hcmut.smarthome.entity.ScriptTypeEntity;
import com.hcmut.smarthome.model.ScriptType;

public class ScriptTypeConverter {
	public static ScriptType toModel(ScriptTypeEntity scriptTypeEntity){
		ScriptType scriptType = new ScriptType();
		scriptType.setId(scriptTypeEntity.getId());
		scriptType.setName(scriptTypeEntity.getName());
		scriptType.setTemplate(scriptTypeEntity.getTemplate());
		
		return scriptType;
	}
	
	public static ScriptType toListModel(ScriptTypeEntity scriptTypeEntity) throws NotSupportedException{
		throw new NotSupportedException();
	}
}
