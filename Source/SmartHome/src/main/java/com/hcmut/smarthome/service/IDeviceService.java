package com.hcmut.smarthome.service;

import java.util.List;

import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.model.Script;

public interface IDeviceService {
	List<Device> getAllGivenHomeAndDeviceType(int homeId, int deviceTypeId);
	
	List<Device> getAllDevices(int homeId);
	
	Device getDevice(int homeId, int deviceId);
	
	List<Script> getScripts(int modeId, int deviceId);
	
	boolean addScript(Script script, int deviceId , int modeId);
	
	boolean deleteScript(int scriptId);
	
	boolean updateScript(int scriptId, Script updatedScript);
}
