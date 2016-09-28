package com.hcmut.smarthome.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.hcmut.smarthome.entity.ActionEntity;
import com.hcmut.smarthome.model.Action;

public class ActionConverter {
	public static Action toModel(ActionEntity actionEntity){
		Action action = new Action();
		action.setId(actionEntity.getId());
		action.setName(actionEntity.getName());
		action.setScript(actionEntity.getScript());
		
		return action;
	}
	
	public static List<Action> toListModel(Set<ActionEntity> actionEntities){
		List<Action> actions = new ArrayList<>();
		for (ActionEntity actionEntity : actionEntities) {
			actions.add(toModel(actionEntity));
		}
		
		return actions;
	}
}
