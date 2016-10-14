package com.hcmut.smarthome.service;

import java.util.List;

import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.model.Script;

public interface IDeviceService {
	List<Device> getAllGivenHomeAndDeviceType(int homeId, int deviceTypeId);
	
	List<Device> getAllDevices(int homeId);
	
	Device getDevice(int homeId, int deviceId);
	
	List<Script> getScripts(int modeId, int deviceId);
	
	int addScript(Script script, int deviceId , int modeId);
	
	boolean deleteScript( int deviceId, int scriptId);
	
	boolean updateScript(int scriptId, Script updatedScript);
	
	boolean updatePartialScript(int scriptId, Script updatedScript);
	
	List<Integer> getAllAvailableGpio(int homeId);

	boolean updateDevice(int homeId, int deviceId, int deviceTypeId, Device updatedDevice);
	
	int addDevice(int homeId, int deviceTypeId, Device device);
	
	boolean deleteDevice(int homeId, int deviceId);

	boolean updatePartialDevice(int homeId, int deviceId, int deviceTypeId, Device updatedDevice);
}
