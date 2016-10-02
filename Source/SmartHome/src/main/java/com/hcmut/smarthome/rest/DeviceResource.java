package com.hcmut.smarthome.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hcmut.smarthome.model.Script;
import com.hcmut.smarthome.service.IDeviceService;

@RestController
@RequestMapping("devices/{deviceId}/modes/{modeId}/scripts")
@CrossOrigin
public class DeviceResource {

	@Autowired
	private IDeviceService deviceService;
	
	/**
	 * Delete one script given scriptId
	 * @param scriptId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE, path="/{scriptId}")
	public ResponseEntity<Void> deleteScript(@PathVariable int scriptId){
		deviceService.deleteScript(scriptId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * Get all scripts given mode and device
	 * @param deviceId
	 * @param modeId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Script>> getScripts(@PathVariable int deviceId,@PathVariable int modeId){
		return new ResponseEntity<List<Script>>(deviceService.getScripts( modeId, deviceId),HttpStatus.OK);
	}
	
	/**
	 * Update one script
	 * @param scriptId
	 * @param script
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, path="/{scriptId}")
	public ResponseEntity<Void> updateScript(@PathVariable int scriptId, @RequestBody Script script ){
		deviceService.updateScript(scriptId,script);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * Add new script given mode and device
	 * @param deviceId
	 * @param modeId
	 * @param script
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> addScript(@PathVariable int deviceId,@PathVariable int modeId,@RequestBody Script script ){
		deviceService.addScript(script,deviceId, modeId);
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}
	
	/*@RequestMapping(method = RequestMethod.GET, path = "/{deviceId}")
	public ResponseEntity<Void> getDeviceById(@PathVariable int deviceId)
			throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> addNewDevice(@RequestBody Device newDevice)
			throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/{deviceId}")
	public ResponseEntity<Void> updateDeviceById(@PathVariable int deviceId)
			throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/{deviceId}")
	public ResponseEntity<Void> removeDeviceById(@PathVariable int deviceId)
			throws NotSupportedException {
		throw new NotSupportedException();
	}
	*/

	/*
	@RequestMapping(method = RequestMethod.GET, path = "/status")
	public ResponseEntity<Void> getStatusAllDevices()
			throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{deviceId}/scenarios")
	public ResponseEntity<Void> getAllScenariosOfDevice(
			@PathVariable int deviceId) throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{deviceId}/scenarios")
	public ResponseEntity<Void> getPresetScenariosOfDevice(
			@PathVariable int deviceId,
			@RequestParam(value = "preset", required = true, defaultValue = "true") boolean isPreset)
			throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{deviceId}/scenarios/{scenarioId}")
	public ResponseEntity<Void> getScenarioByIdOfDevice(
			@PathVariable int deviceId, @PathVariable int scenarioId)
			throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.POST, path = "/{deviceId}/scenario")
	public ResponseEntity<Void> addNewScenarioOfDevice(
			@PathVariable int deviceId, @RequestBody Scenario newScenario)
			throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/{deviceId}/scenario/{scenarioId}")
	public ResponseEntity<Void> updateScenarioOfDevice(
			@PathVariable int deviceId, @PathVariable int scenarioId)
			throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/{deviceId}/scenarios/{scenarioId}")
	public ResponseEntity<Void> removeScenarioOfDevice(
			@PathVariable int deviceId, @PathVariable int scenarioId)
			throws NotSupportedException {
		throw new NotSupportedException();
	}*/

	

	/*@RequestMapping(method = RequestMethod.GET, path = "/{deviceId}/toggle")
	public ResponseEntity<Void> toggleDeviceById(@PathVariable int deviceId)
			throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{deviceId}/turnOn")
	public ResponseEntity<Void> turnOnDeviceById(@PathVariable int deviceId)
			throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{deviceId}/turnOff")
	public ResponseEntity<Void> turnOffDeviceById(@PathVariable int deviceId)
			throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{deviceId}/capture")
	public ResponseEntity<Void> capture(@PathVariable int deviceId)
			throws NotSupportedException {
		throw new NotSupportedException();
	}*/
}
