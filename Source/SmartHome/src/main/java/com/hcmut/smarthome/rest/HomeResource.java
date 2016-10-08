package com.hcmut.smarthome.rest;

import static com.hcmut.smarthome.utils.ConstantUtil.ALL_GPIO;

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
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IDeviceTypeService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.ConstantUtil;

@CrossOrigin
@RestController
@RequestMapping("/homes")
public class HomeResource {

	@Autowired
	private IDeviceService deviceService;
	
	@Autowired
	private IScenarioService scenarioService;
	
	@Autowired
	private IDeviceTypeService deviceTypeService;
	
	/**
	 * Delete device given device id
	 * @param deviceId
	 * @param updatedDevice
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE, path = "/{homeId}/devices/{deviceId}")
	public ResponseEntity<Void> deleteDevice(@PathVariable int deviceId){
		deviceService.deleteDevice(deviceId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * Add new device given home and device type
	 * @param deviceTypeId
	 * @param homeId
	 * @param device
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/{homeId}/deviceTypes/{deviceTypeId}/devices")
	public ResponseEntity<Void> updateDevice(@PathVariable int deviceTypeId, @PathVariable int homeId, @RequestBody Device device){
		deviceService.addDevice(homeId, deviceTypeId, device);
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}
	
	/**
	 * Update device
	 * @param homeId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/{homeId}/deviceTypes/{deviceTypeId}/devices/{deviceId}")
	public ResponseEntity<Void> updateDevice(@PathVariable int homeId, @PathVariable int deviceId, @PathVariable int deviceTypeId, @RequestBody Device updatedDevice){
		deviceService.updateDevice(homeId, deviceId, deviceTypeId, updatedDevice);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	/**
	 * Get all device types that user have
	 * @param homeId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/{homeId}/deviceTypes")
	public ResponseEntity<List<DeviceType>> getAllDevicesTypeUserHave(@PathVariable int homeId){
		return new ResponseEntity<List<DeviceType>>(deviceTypeService.getAll(ConstantUtil.VALID_USER_ID, homeId), HttpStatus.OK);
	}
	
	/**
	 * Get all devices given home and device type
	 * @param homeId
	 * @param deviceTypeId
	 * @return
	 * @throws NotSupportedException
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/{homeId}/deviceTypes/{deviceTypeId}/devices")
	public ResponseEntity<List<Device>> getAllDevicesGivenHomeAndDeviceType( @PathVariable int deviceTypeId, @PathVariable int homeId) throws NotSupportedException {
		return new ResponseEntity<List<Device>>(deviceService.getAllGivenHomeAndDeviceType(homeId, deviceTypeId), HttpStatus.OK);
	}
	
	
	/**
	 * Get all devices in home
	 * @return
	 * @throws NotSupportedException
	 */
	@RequestMapping(method = RequestMethod.GET, path="/devices")
	public ResponseEntity<List<Device>> getAllDevices() throws NotSupportedException {
		return new ResponseEntity<List<Device>>(deviceService.getAllDevices(1),HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, path="{homeId}/availableGPIOs")
	public ResponseEntity<List<Integer>> getAllAvailableGPIOs(@PathVariable int homeId) {
		return new ResponseEntity<List<Integer>>(deviceService.getAllAvailableGpio(homeId),HttpStatus.OK);
	}
	
	/**
	 * Get all gpio
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/all-gpios")
	public ResponseEntity<List<Integer>> getAllGpio() {
		return new ResponseEntity<List<Integer>>(ALL_GPIO, HttpStatus.OK);
	}
	
	/**
	 * For testing purpose
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(method = RequestMethod.GET, path ="/test1")
	public ResponseEntity<Void> test() throws ParseException{
		String script3 = "[['If',['4','=', 'true'],[['TurnOnLight','2']]]]";
		Scenario scenario = scenarioService.JSONToScenario(script3);
		scenario.setId(1);
		scenarioService.runScenario(scenario);
		return null;
	}
	
	@RequestMapping(method = RequestMethod.GET, path ="/test2")
	public ResponseEntity<Void> test2() throws ParseException{
		String script1 = "[['If',['5','>', '31.0'],[['TurnOnBuzzer','6']]]]";
		Scenario scenario = scenarioService.JSONToScenario(script1);
		scenario.setId(2);
		scenarioService.runScenario(scenario);
		return null;
	}

}
