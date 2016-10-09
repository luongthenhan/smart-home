package com.hcmut.smarthome.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.converter.HomeConverter;
import com.hcmut.smarthome.dao.IHomeDao;
import com.hcmut.smarthome.model.Home;
import com.hcmut.smarthome.service.IHomeService;

@Service
public class HomeService implements IHomeService{

	@Autowired
	private IHomeDao homeDao;
	
	@Override
	public List<Home> getAllHomes(int userId) {
		return HomeConverter.toListModel(homeDao.getAllHomes(userId));
	}

	@Override
	public boolean updateEnabled(int homeId, boolean enabled) {
		return homeDao.updateEnabled(homeId, enabled);
	}

	@Override
	public boolean isEnabled(int homeId) {
		return homeDao.isEnabled(homeId);
	}

}
