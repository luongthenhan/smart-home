package com.hcmut.smarthome.service;

import java.util.List;
import java.util.Set;

import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.model.Script;
import com.hcmut.smarthome.model.ScriptMoreDetail;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.utils.NotFoundException;

public interface IDeviceService {
	List<Device> getAllGivenHomeAndDeviceType(int homeId, int deviceTypeId) throws NotFoundException;
	
	List<Device> getAllDevices(int homeId);
	
	Device getDevice(int homeId, int deviceId) throws NotFoundException;
	
	List<Script> getScripts(int modeId, int deviceId) throws Exception;
	
	Script getScript(int scriptId) throws NotFoundException;
	
	int addScript(Script script, int deviceId , int modeId, int homeId) throws Exception;
	
	boolean deleteScript( int deviceId, int scriptId) throws NotFoundException;
	
	boolean updateScript(int homeId, int modeId, int deviceId, int scriptId, Script updatedScript) throws Exception;
	
	boolean updatePartialScript(int homeId, int modeId, int deviceId, int scriptId, Script updatedScript) throws Exception;
	
	List<Integer> getAllAvailableGpio(int homeId);

	boolean updateDevice(int homeId, int deviceId, int deviceTypeId, Device updatedDevice) throws Exception;
	
	int addDevice(int homeId, int deviceTypeId, Device device) throws Exception;
	
	boolean deleteDevice(int homeId, int deviceId) throws Exception;

	boolean updatePartialDevice(int homeId, int deviceId, int deviceTypeId, Device updatedDevice) throws Exception;

	boolean isDeviceEnabled(int deviceId) throws Exception;

	Integer getDeviceIdGivenNameAndHomeId(int homeId, String deviceName) throws NotFoundException;

	List<Script> getAllScriptsGivenHome(int homeId) throws Exception;

	List<Script> getScriptsGivenMode(int modeId);

	Set<Integer> getListDeviceIdInScript(int homeId, Script script)
			throws Exception;

	Set<Integer> getListDeviceIdInScript(int homeId, int scriptId)
			throws Exception;

	List<Script> getAllScriptsGivenMode(int modeId) throws Exception;

	List<ScriptMoreDetail> getAllScripts() throws Exception;

	boolean checkHomeOrDevicesDisabled(int homeId, int deviceId,
			Scenario scenario) throws Exception;
}
