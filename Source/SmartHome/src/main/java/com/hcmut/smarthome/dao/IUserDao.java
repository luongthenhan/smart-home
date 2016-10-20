package com.hcmut.smarthome.dao;

import com.hcmut.smarthome.entity.UserEntity;

public interface IUserDao extends ICommonDao<UserEntity> {
	
	public UserEntity getByUsername(String username);

}
