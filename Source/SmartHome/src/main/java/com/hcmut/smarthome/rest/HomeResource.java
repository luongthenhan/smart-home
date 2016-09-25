package com.hcmut.smarthome.rest;

import java.util.List;

import javax.transaction.NotSupportedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.service.IDeviceService;


@RestController
@RequestMapping("/homes")
public class HomeResource {

	@Autowired
	private IDeviceService deviceService;
	
	@RequestMapping(method = RequestMethod.GET, path = "/{homeId}/devices/type/{deviceTypeId}")
	public ResponseEntity<List<Device>> login(@PathVariable int homeId, @PathVariable int deviceTypeId) throws NotSupportedException {
		return new ResponseEntity<List<Device>>(deviceService.getAllGivenHomeAndDeviceType(homeId, deviceTypeId), HttpStatus.OK);
	}

}
