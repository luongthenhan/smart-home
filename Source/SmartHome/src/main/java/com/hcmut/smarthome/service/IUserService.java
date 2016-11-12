package com.hcmut.smarthome.service;

import com.hcmut.smarthome.model.User;

public interface IUserService {
	
	public int addUser(User user) throws Exception;
	
	public boolean activateUser(int userId);
	
	public User getById(int id);

}
