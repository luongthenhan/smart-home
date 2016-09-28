package com.hcmut.smarthome.converter;

import com.hcmut.smarthome.entity.HomeEntity;
import com.hcmut.smarthome.model.Home;

public class HomeConverter {
	public static Home toModel(HomeEntity homeEntity){
		Home home = new Home();
		home.setId(homeEntity.getId());
		home.setName(homeEntity.getName());
		home.setAddress(homeEntity.getAddress());
		home.setDescription(homeEntity.getDescription());
		
		return home;
	}
}
