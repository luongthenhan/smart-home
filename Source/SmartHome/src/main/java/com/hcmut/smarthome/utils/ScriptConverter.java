package com.hcmut.smarthome.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.hcmut.smarthome.entity.ScriptEntity;
import com.hcmut.smarthome.model.Script;

public class ScriptConverter {
	public static Script toModel(ScriptEntity scriptEntity){
		Script script = new Script();
		script.setId(scriptEntity.getId());
		script.setName(scriptEntity.getName());
		script.setScript(scriptEntity.getContent());
		
		return script;
	}
	
	public static List<Script> toListModel(Set<ScriptEntity> scriptEntities){
		List<Script> scripts = new ArrayList<>();
		for (ScriptEntity scriptEntity : scriptEntities) {
			scripts.add(toModel(scriptEntity));
		}
		
		return scripts;
	}
}
