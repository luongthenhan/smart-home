package com.hcmut.smarthome.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hcmut.smarthome.entity.ScriptEntity;
import com.hcmut.smarthome.model.Script;

public class ScriptConverter {
	public static Script toModel(ScriptEntity scriptEntity){
		Script script = new Script();
		script.setId(scriptEntity.getId());
		script.setName(scriptEntity.getName());
		script.setContent(scriptEntity.getContent());
		script.setEnabled(scriptEntity.isEnabled());
		script.setType(ScriptTypeConverter.toModel(scriptEntity.getScriptType()));
		
		return script;
	}
	
	public static List<Script> toListModel(Collection<ScriptEntity> scriptEntities){
		List<Script> scripts = new ArrayList<>();
		for (ScriptEntity scriptEntity : scriptEntities) {
			scripts.add(toModel(scriptEntity));
		}
		
		return scripts;
	}
	
}
