package com.hcmut.smarthome.service;

import java.util.List;

import com.hcmut.smarthome.model.DeviceType;

public interface IDeviceTypeService {
	List<DeviceType> getAll(int userId, int homeId);
}
