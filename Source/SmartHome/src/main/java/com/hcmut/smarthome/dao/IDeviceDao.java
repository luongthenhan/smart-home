package com.hcmut.smarthome.dao;

import java.util.List;

import com.hcmut.smarthome.entity.DeviceEntity;
import com.hcmut.smarthome.model.Device;

public interface IDeviceDao extends ICommonDao<DeviceEntity> {
	List<DeviceEntity> getAllGivenHomeAndDeviceType(int homeId , int deviceTypeId);

	List<DeviceEntity> getAll(int homeId);

	boolean deleteDevice(int deviceId);

	boolean updatePartialDevice(int deviceId, Device device);

	boolean isEnabled(int deviceId);

	boolean isDeviceNameExisted(int homeId, String deviceName);

}
