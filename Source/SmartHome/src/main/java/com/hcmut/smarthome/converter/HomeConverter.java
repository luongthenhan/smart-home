package com.hcmut.smarthome.converter;

import java.util.ArrayList;
import java.util.List;

import com.hcmut.smarthome.entity.HomeEntity;
import com.hcmut.smarthome.model.Home;

public class HomeConverter {
	public static Home toModel(HomeEntity homeEntity){
		Home home = new Home();
		home.setId(homeEntity.getId());
		home.setName(homeEntity.getName());
		home.setAddress(homeEntity.getAddress());
		home.setDescription(homeEntity.getDescription());
		home.setCurrentMode(ModeConverter.toModel(homeEntity.getCurrentMode()));
		home.setModes(ModeConverter.toListModel(homeEntity.getModes()));
		
		return home;
	}

	public static List<Home> toListModel(List<HomeEntity> homeEntities) {
		List<Home> homes = new ArrayList<>();
		for (HomeEntity homeEntity : homeEntities) {
			homes.add(toModel(homeEntity));
		}
		
		return homes;
	}
}
