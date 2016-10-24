package com.hcmut.smarthome.service;

import java.util.List;

import javax.script.ScriptException;
import javax.transaction.NotSupportedException;

import org.json.simple.parser.ParseException;

import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.model.Script;
import com.hcmut.smarthome.utils.ConflictConditionException;

public interface IDeviceService {
	List<Device> getAllGivenHomeAndDeviceType(int homeId, int deviceTypeId);
	
	List<Device> getAllDevices(int homeId);
	
	Device getDevice(int homeId, int deviceId);
	
	List<Script> getScripts(int modeId, int deviceId);
	
	Script getScript(int scriptId);
	
	int addScript(Script script, int deviceId , int modeId, int homeId) throws ParseException, NotSupportedException, ConflictConditionException, ScriptException;
	
	boolean deleteScript( int deviceId, int scriptId);
	
	boolean updateScript(int homeId, int modeId, int deviceId, int scriptId, Script updatedScript) throws ParseException, NotSupportedException, ConflictConditionException, ScriptException;
	
	boolean updatePartialScript(int homeId, int modeId, int deviceId, int scriptId, Script updatedScript) throws ParseException, NotSupportedException, ConflictConditionException, ScriptException;
	
	List<Integer> getAllAvailableGpio(int homeId);

	boolean updateDevice(int homeId, int deviceId, int deviceTypeId, Device updatedDevice);
	
	int addDevice(int homeId, int deviceTypeId, Device device);
	
	boolean deleteDevice(int homeId, int deviceId);

	boolean updatePartialDevice(int homeId, int deviceId, int deviceTypeId, Device updatedDevice);

	boolean isDeviceEnabled(int deviceId);
}
