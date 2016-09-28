package com.hcmut.smarthome.converter;

import java.util.ArrayList;
import java.util.List;

import com.hcmut.smarthome.entity.ConditionEntity;
import com.hcmut.smarthome.model.Condition;

public class ConditionConverter {
	public static Condition toModel(ConditionEntity conditionEntity){
		Condition condition = new Condition();
		condition.setId(conditionEntity.getId());
		condition.setName(conditionEntity.getName());
		condition.setHasParameter(conditionEntity.isHasParameter());
		condition.setScript(conditionEntity.getScript());
		
		return condition;
	}
	
	public static List<Condition> toListModel(List<ConditionEntity> conditionEntities){
		List<Condition> conditions = new ArrayList<>();
		for (ConditionEntity conditionEntity : conditionEntities) {
			conditions.add(toModel(conditionEntity));
		}
		
		return conditions;
	}
}
