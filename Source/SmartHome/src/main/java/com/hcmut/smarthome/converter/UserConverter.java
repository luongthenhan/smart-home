package com.hcmut.smarthome.converter;

import com.hcmut.smarthome.entity.UserEntity;
import com.hcmut.smarthome.model.User;

public class UserConverter {
	
	public static User toModel(UserEntity userEntity) {
		
		User userModel = new User();
		userModel.setId(userEntity.getId());
		userModel.setUsrName(userEntity.getUsrName());
		userModel.setPassword(userEntity.getPassword());
		userModel.setName(userEntity.getName());
		userModel.setAbout(userEntity.getDescription());
		userModel.setEmail(userEntity.getEmail());
		userModel.setActivated(userEntity.isActivated());
		return userModel;
	}

}
