package com.hcmut.smarthome.rest;

import static com.hcmut.smarthome.utils.ConstantUtil.TURN_ON;

import java.util.List;

import javax.transaction.NotSupportedException;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.model.DeviceType;
import com.hcmut.smarthome.model.ResponeString;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.sec.IAuthenticationService;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IDeviceTypeService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.ConstantUtil;
import com.hcmut.smarthome.utils.ScriptBuilder;

@CrossOrigin
@RestController
@RequestMapping("/homes/{homeId}")
public class DeviceResource {

	@Autowired
	private IDeviceService deviceService;

	@Autowired
	private IScenarioService scenarioService;

	@Autowired
	private IDeviceTypeService deviceTypeService;

	@Autowired
	private IAuthenticationService authService;

	/**
	 * Delete device given device id
	 * 
	 * @param deviceId
	 * @param updatedDevice
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE, path = "/device-types/{deviceTypeId}/devices/{deviceId}")
	public ResponseEntity<Void> deleteDevice(@PathVariable int homeId,
			@PathVariable int deviceId) {

		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}

		deviceService.deleteDevice(homeId, deviceId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	/**
	 * Add new device given home and device type
	 * 
	 * @param deviceTypeId
	 * @param homeId
	 * @param device
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/device-types/{deviceTypeId}/devices")
	public ResponseEntity<ResponeString> addDevice(@PathVariable int deviceTypeId,
			@PathVariable int homeId, @RequestBody Device device) {
		
		if(!authService.isAccessable(homeId)) {
			return new ResponseEntity<ResponeString>(HttpStatus.UNAUTHORIZED);
		}
			
		int addedDeviceId = deviceService.addDevice(homeId, deviceTypeId,
				device);
		if (addedDeviceId > 0) {
			String URINewAddedObject = String.format(
					"homes/%s/device-types/%s/devices/%s", homeId,
					deviceTypeId, addedDeviceId);
			return new ResponseEntity<ResponeString>(new ResponeString(addedDeviceId, URINewAddedObject),
					HttpStatus.CREATED);
		} else
			return new ResponseEntity<ResponeString>(HttpStatus.NOT_FOUND);
	}

	/**
	 * Update device
	 * 
	 * @param homeId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/device-types/{deviceTypeId}/devices/{deviceId}")
	public ResponseEntity<Void> updateDevice(@PathVariable int homeId,
			@PathVariable int deviceId, @PathVariable int deviceTypeId,
			@RequestBody Device updatedDevice) {
		
		if(!authService.isAccessable(homeId)) {
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		
		if (deviceService.updateDevice(homeId, deviceId, deviceTypeId,
				updatedDevice))
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(method = RequestMethod.PATCH, path = "/device-types/{deviceTypeId}/devices/{deviceId}")
	public ResponseEntity<Void> updatePartialDevice(@PathVariable int homeId,
			@PathVariable int deviceId, @PathVariable int deviceTypeId,
			@RequestBody Device updatedDevice) {
		
		if(!authService.isAccessable(homeId)) {
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		
		if (deviceService.updatePartialDevice(homeId, deviceId, deviceTypeId,
				updatedDevice))
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}

	/**
	 * Get all device types that user have
	 * 
	 * @param homeId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/device-types")
	public ResponseEntity<List<DeviceType>> getAllDevicesTypeUserHave(
			@PathVariable int homeId) {
		
		if(!authService.isAccessable(homeId)) {
			return new ResponseEntity<List<DeviceType>>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<List<DeviceType>>(deviceTypeService.getAll(
				ConstantUtil.VALID_USER_ID, homeId), HttpStatus.OK);
	}

	/**
	 * Get all devices given home and device type
	 * 
	 * @param homeId
	 * @param deviceTypeId
	 * @return
	 * @throws NotSupportedException
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/device-types/{deviceTypeId}/devices")
	public ResponseEntity<List<Device>> getAllDevicesGivenHomeAndDeviceType(
			@PathVariable int deviceTypeId, @PathVariable int homeId)
			throws NotSupportedException {
		
		if(!authService.isAccessable(homeId)) {
			return new ResponseEntity<List<Device>>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<List<Device>>(
				deviceService
						.getAllGivenHomeAndDeviceType(homeId, deviceTypeId),
				HttpStatus.OK);
	}

	/**
	 * Get all devices in home
	 * 
	 * @return
	 * @throws NotSupportedException
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/devices")
	public ResponseEntity<List<Device>> getAllDevices(@PathVariable int homeId)
			throws NotSupportedException {
		
		if(!authService.isAccessable(homeId)) {
			return new ResponseEntity<List<Device>>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<List<Device>>(
				deviceService.getAllDevices(homeId), HttpStatus.OK);
	}

	/**
	 * For testing purpose
	 * 
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/test1")
	public ResponseEntity<Void> test(@PathVariable int homeId) throws ParseException {
		
		if(!authService.isAccessable(homeId)) {
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		
		String script3 = "[['If',['4','=', 'true'],[['TurnOnLight','2']]]]";
		Scenario scenario = scenarioService.JSONToScenario(script3);
		scenario.setId(1);
		scenarioService.runScenario(scenario);
		return null;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/test2")
	public ResponseEntity<Void> test2(@PathVariable int homeId) throws ParseException {
		
		if(!authService.isAccessable(homeId)) {
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		
		String script1 = "[['If',['5','>=', '31.0'],[['TurnOn','2']]]]";
		Scenario scenario = scenarioService.JSONToScenario(script1);
		scenario.setId(2);
		scenario.setHomeId(ConstantUtil.HOME_ID);
		scenarioService.runScenario(scenario);
		return null;
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/test3")
	public ResponseEntity<Void> test3(@PathVariable int homeId) throws ParseException {
		
		String input = new ScriptBuilder()
		.begin()
			.FromTo("00:00", "00:20")
				.action(TURN_ON, 2)
			.endFromTo()
		.end().build();
		
		Scenario scenario = scenarioService.JSONToScenario(input);
		scenario.setId(1);
		scenario.setHomeId(1);
		scenarioService.runScenario(scenario);
		return null;
	}

}
