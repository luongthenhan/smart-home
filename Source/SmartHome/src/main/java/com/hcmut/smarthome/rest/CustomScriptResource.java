package com.hcmut.smarthome.rest;

import java.util.Set;

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
import com.hcmut.smarthome.sec.IAuthenticationService;
import com.hcmut.smarthome.service.IDeviceService;

@RestController
@RequestMapping("homes/{homeId}/scripts")
@CrossOrigin
public class CustomScriptResource {
	@Autowired
	private IAuthenticationService authService;
	
	@Autowired
	private IDeviceService deviceService;
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Set<Integer>> getListDeviceId( @PathVariable int homeId, @RequestBody Script script) {
		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<Set<Integer>>(HttpStatus.UNAUTHORIZED);
		}
		
		try{
			return new ResponseEntity<Set<Integer>>(deviceService.getListDeviceIdInScript(homeId, script), HttpStatus.OK );
		}
		catch(Exception e){
			return new ResponseEntity<Set<Integer>>(HttpStatus.BAD_REQUEST);
		}
	} 
}
