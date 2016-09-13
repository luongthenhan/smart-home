package com.hcmut.smarthome.rest;

import javax.transaction.NotSupportedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcmut.smarthome.dto.Scenario;
import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IScenarioService;

@RestController
@RequestMapping("/devices")
public class DeviceResource {

	@Autowired
	private IScenarioService scenarioService;

	@Autowired
	private IDeviceService deviceService;

	@RequestMapping(method = RequestMethod.GET, path = "/{deviceId}")
	public ResponseEntity<Void> getDeviceById(@PathVariable int deviceId)
			throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> addNewDevice(@RequestBody Device newDevice) throws NotSupportedException {
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

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Void> getAllDevices() throws NotSupportedException {
		throw new NotSupportedException();
	}

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
			@PathVariable int deviceId, @RequestBody Scenario newScenario) throws NotSupportedException {
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
	}

	@RequestMapping(method = RequestMethod.GET, path = "/type")
	public ResponseEntity<Void> getAllDevicesTypeUserHave()
			throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{deviceId}/toggle")
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
	}
}
