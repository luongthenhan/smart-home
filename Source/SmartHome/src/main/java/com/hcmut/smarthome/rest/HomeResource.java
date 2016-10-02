package com.hcmut.smarthome.rest;

import java.util.List;

import javax.transaction.NotSupportedException;

import org.json.simple.parser.ParseException;
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
	public ResponseEntity<List<Device>> getAllDevicesGivenHomeAndDeviceType(@PathVariable int homeId, @PathVariable int deviceTypeId) throws NotSupportedException {
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
	
	/**
	 * For testing purpose
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(method = RequestMethod.GET, path ="/test1")
	public ResponseEntity<Void> test() throws ParseException{
		String script3 = "[['If',['4','=', 'true'],[['TurnOnLight','2']]]]";
		Scenario scenario = scenarioService.JSONToScenario(script3);
		scenarioService.runScenario(scenario);
		return null;
	}
	
	@RequestMapping(method = RequestMethod.GET, path ="/test2")
	public ResponseEntity<Void> test2() throws ParseException{
		String script1 = "[['If',['5','>', '31.0'],[['TurnOnBuzzer','6']]]]";
		Scenario scenario = scenarioService.JSONToScenario(script1);
		scenarioService.runScenario(scenario);
		return null;
	}

}
