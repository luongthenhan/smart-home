package com.hcmut.smarthome.dao;

import java.util.List;

import com.hcmut.smarthome.entity.DeviceEntity;

public interface IDeviceDao {
	List<DeviceEntity> getAllGivenHomeAndDeviceType(int homeId , int deviceTypeId);

	List<DeviceEntity> getAll(int homeId);
}
