package com.hcmut.smarthome.converter;

import java.util.ArrayList;
import java.util.List;

import com.hcmut.smarthome.entity.ModeEntity;
import com.hcmut.smarthome.model.Mode;

public class ModeConverter {
	public static Mode toModel(ModeEntity modeEntity){
		Mode mode = new Mode();
		mode.setId(modeEntity.getId());
		mode.setName(modeEntity.getName());
		
		return mode;
	}
	
	public static List<Mode> toListModel(List<ModeEntity> modeEntities){
		List<Mode> modes = new ArrayList<>();
		
		for (ModeEntity modeEntity : modeEntities) {
			modes.add(toModel(modeEntity));
		}
		
		return modes;
	}
}
