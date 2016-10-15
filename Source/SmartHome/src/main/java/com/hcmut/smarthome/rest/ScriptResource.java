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
public class ScriptResource {

	@Autowired
	private IDeviceService deviceService;
	
	/**
	 * Delete one script given scriptId
	 * @param scriptId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE, path="/{scriptId}")
	public ResponseEntity<Void> deleteScript(@PathVariable int deviceId, @PathVariable int scriptId){
		deviceService.deleteScript(deviceId, scriptId);
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
	
	@RequestMapping(method = RequestMethod.PATCH, path="/{scriptId}")
	public ResponseEntity<Void> updatePartialScript(@PathVariable int scriptId, @RequestBody Script script ){
		deviceService.updatePartialScript(scriptId,script);
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
	public ResponseEntity<String> addScript(@PathVariable int deviceId,@PathVariable int modeId,@RequestBody Script script ){
		int addedScriptId = deviceService.addScript(script,deviceId, modeId);
		
		if( addedScriptId > 0 ){
			String URINewAddedObject = String.format("devices/%s/modes/%s/scripts/%s", deviceId, modeId, addedScriptId);
			return new ResponseEntity<String>(URINewAddedObject,HttpStatus.CREATED);
		}
		
		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
	}
}
