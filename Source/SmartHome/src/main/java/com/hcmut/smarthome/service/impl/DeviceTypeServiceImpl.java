package com.hcmut.smarthome.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.converter.DeviceTypeConverter;
import com.hcmut.smarthome.dao.IDeviceTypeDao;
import com.hcmut.smarthome.entity.DeviceTypeEntity;
import com.hcmut.smarthome.model.DeviceType;
import com.hcmut.smarthome.service.IDeviceTypeService;

@Service
public class DeviceTypeServiceImpl implements IDeviceTypeService{

	@Autowired
	IDeviceTypeDao deviceTypeDao;
	
	@Override
	public List<DeviceType> getAll(int userId, int homeId) {
		List<DeviceTypeEntity> deviceTypeEntities = deviceTypeDao.getAllGivenUserAndHome(userId, homeId);
		return DeviceTypeConverter.toListModel(deviceTypeEntities);
	}
	
	
}
