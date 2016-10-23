package com.hcmut.smarthome.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.converter.UserConverter;
import com.hcmut.smarthome.dao.IUserDao;
import com.hcmut.smarthome.entity.UserEntity;
import com.hcmut.smarthome.model.User;
import com.hcmut.smarthome.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {
	
	@Autowired
	private IUserDao userDao;

	@Override
	public int addUser(User user) {

		UserEntity userEntity = new UserEntity(user.getUsrName(),
				user.getPassword(), user.getName(), user.getEmail());
		
		return userDao.addUser(userEntity);
	}

	@Override
	public boolean activateUser(int userId) {
		return userDao.activateUser(userId);
	}

	@Override
	public User getById(int id) {
		
		UserEntity userEntity = userDao.getById(id);
		if(userEntity == null) {
			return null;
		}
		
		return UserConverter.toModel(userEntity);
	}

}
