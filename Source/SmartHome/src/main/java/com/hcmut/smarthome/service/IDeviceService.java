package com.hcmut.smarthome.service;

import java.util.List;

import com.hcmut.smarthome.model.Device;

public interface IDeviceService {
	List<Device> getAllGivenHomeAndDeviceType(int homeId, int deviceTypeId);
	
}
