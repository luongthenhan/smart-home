package com.hcmut.smarthome.dao;

import java.util.List;

import com.hcmut.smarthome.entity.DeviceTypeEntity;

public interface IDeviceTypeDao extends ICommonDao<DeviceTypeEntity>{
	List<DeviceTypeEntity> getAllGivenUserAndHome(int userId, int homeId);
}
