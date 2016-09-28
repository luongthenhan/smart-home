package com.hcmut.smarthome.rest;

import java.util.List;

import javax.transaction.NotSupportedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.model.DeviceType;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IDeviceTypeService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.ConstantUtil;

@RestController
@RequestMapping("/homes/{homeId}/devices")
@CrossOrigin
public class DeviceResource {

	@Autowired
	private IScenarioService scenarioService;

	@Autowired
	private IDeviceService deviceService;
	
	@Autowired
	private IDeviceTypeService deviceTypeService;

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
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Device>> getAllDevices() throws NotSupportedException {
		return new ResponseEntity<List<Device>>(deviceService.getAllDevices(1),HttpStatus.OK);
	}
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

	@RequestMapping(method = RequestMethod.GET, path = "/type")
	public ResponseEntity<List<DeviceType>> getAllDevicesTypeUserHave(@PathVariable int homeId){
		return new ResponseEntity<List<DeviceType>>(deviceTypeService.getAll(ConstantUtil.VALID_USER_ID, homeId), HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "type/{deviceTypeId}")
	public ResponseEntity<List<Device>> getAllDevicesGivenHomeAndDeviceType(@PathVariable int homeId, @PathVariable int deviceTypeId) throws NotSupportedException {
		return new ResponseEntity<List<Device>>(deviceService.getAllGivenHomeAndDeviceType(homeId, deviceTypeId), HttpStatus.OK);
	}

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
