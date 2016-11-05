package com.hcmut.smarthome.rest;

import java.util.List;

import javax.transaction.NotSupportedException;

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
import com.hcmut.smarthome.sec.IAuthenticationService;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IDeviceTypeService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.ConstantUtil;
import com.hcmut.smarthome.utils.NotFoundException;

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
	public ResponseEntity<ResponeString> deleteDevice(@PathVariable int homeId,
			@PathVariable int deviceId) {

		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<ResponeString>(HttpStatus.UNAUTHORIZED);
		}

		try {
			deviceService.deleteDevice(homeId, deviceId);
		} catch (NotFoundException e) {
			return new ResponseEntity<ResponeString>(new ResponeString(e.getMessage()),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ResponeString>(HttpStatus.NO_CONTENT);
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
			
		int addedDeviceId;
		try {
			addedDeviceId = deviceService.addDevice(homeId, deviceTypeId,
					device);
		} catch (Exception e) {
			return new ResponseEntity<ResponeString>(new ResponeString(e.getMessage()),
					HttpStatus.BAD_REQUEST);
		}
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
	public ResponseEntity<ResponeString> updateDevice(@PathVariable int homeId,
			@PathVariable int deviceId, @PathVariable int deviceTypeId,
			@RequestBody Device updatedDevice) {
		
		if(!authService.isAccessable(homeId)) {
			return new ResponseEntity<ResponeString>(HttpStatus.UNAUTHORIZED);
		}
		
		try {
			if (deviceService.updateDevice(homeId, deviceId, deviceTypeId,
					updatedDevice))
				return new ResponseEntity<ResponeString>(HttpStatus.NO_CONTENT);
			else
				return new ResponseEntity<ResponeString>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<ResponeString>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(method = RequestMethod.PATCH, path = "/device-types/{deviceTypeId}/devices/{deviceId}")
	public ResponseEntity<ResponeString> updatePartialDevice(@PathVariable int homeId,
			@PathVariable int deviceId, @PathVariable int deviceTypeId,
			@RequestBody Device updatedDevice) {
		
		if(!authService.isAccessable(homeId)) {
			return new ResponseEntity<ResponeString>(HttpStatus.UNAUTHORIZED);
		}
		
		try {
			if (deviceService.updatePartialDevice(homeId, deviceId, deviceTypeId,
					updatedDevice))
				return new ResponseEntity<ResponeString>(HttpStatus.NO_CONTENT);
			else
				return new ResponseEntity<ResponeString>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<ResponeString>(HttpStatus.BAD_REQUEST);
		}
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
				authService.getCurrentUserId(), homeId), HttpStatus.OK);
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
		
		try {
			return new ResponseEntity<List<Device>>(
					deviceService.getAllGivenHomeAndDeviceType(homeId, deviceTypeId),
					HttpStatus.OK);
		} catch (NotFoundException e) {
			return new ResponseEntity<List<Device>>(HttpStatus.BAD_REQUEST);
		}
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
}
