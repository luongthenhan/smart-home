package com.hcmut.smarthome.rest;

import java.util.List;
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

import com.hcmut.smarthome.model.ResponeString;
import com.hcmut.smarthome.model.Script;
import com.hcmut.smarthome.sec.IAuthenticationService;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IHomeService;
import com.hcmut.smarthome.service.IScenarioService;
@RestController
@RequestMapping("devices/{deviceId}/modes/{modeId}/scripts")
@CrossOrigin
public class ScriptResource {

	@Autowired
	private IDeviceService deviceService;
	
	@Autowired
	private IScenarioService scenarioService;
	
	@Autowired
	private IHomeService homeService;
	
	@Autowired
	private IAuthenticationService authService;
	
	/**
	 * Delete one script given scriptId
	 * @param scriptId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE, path="/{scriptId}")
	public ResponseEntity<ResponeString> deleteScript(@PathVariable int modeId, @PathVariable int deviceId, @PathVariable int scriptId){
		int homeId = homeService.getHomeIdGivenMode(modeId);
		
		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<ResponeString>(HttpStatus.UNAUTHORIZED);
		}
		
		try {
			Set<Integer> setDeviceIdsInScript = deviceService.getListDeviceIdInScript(homeId, scriptId);
			deviceService.deleteScript(deviceId, scriptId);
			return new ResponseEntity<ResponeString>( new ResponeString(scriptId, "", setDeviceIdsInScript), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<ResponeString>(new ResponeString(e.getMessage()),HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * Get all scripts given mode and device
	 * @param deviceId
	 * @param modeId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Script>> getScripts(@PathVariable int deviceId,@PathVariable int modeId){
		int homeId = homeService.getHomeIdGivenMode(modeId);
		
		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<List<Script>>(HttpStatus.UNAUTHORIZED);
		}
		
		try {
			return new ResponseEntity<List<Script>>(deviceService.getScripts( modeId, deviceId),HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<Script>>(HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * Update one script
	 * @param scriptId
	 * @param script
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, path="/{scriptId}")
	public ResponseEntity<ResponeString> updateScript(@PathVariable int modeId, @PathVariable int deviceId, @PathVariable int scriptId, @RequestBody Script script ) {
		int homeId = homeService.getHomeIdGivenMode(modeId);
		
		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<ResponeString>(HttpStatus.UNAUTHORIZED);
		}
		
		try {
			deviceService.updateScript(homeId, modeId, deviceId, scriptId,script);
			return new ResponseEntity<ResponeString>( new ResponeString(scriptId, "", deviceService.getListDeviceIdInScript(homeId, scriptId)), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<ResponeString>(new ResponeString(e.getMessage()) ,HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(method = RequestMethod.PATCH, path="/{scriptId}")
	public ResponseEntity<ResponeString> updatePartialScript(@PathVariable int modeId, @PathVariable int deviceId, @PathVariable int scriptId, @RequestBody Script script ) {
		int homeId = homeService.getHomeIdGivenMode(modeId);
		
		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<ResponeString>(HttpStatus.UNAUTHORIZED);
		}
		
		try {
			deviceService.updatePartialScript(homeId, modeId, deviceId, scriptId, script);
			return new ResponseEntity<ResponeString>( new ResponeString(scriptId, "", deviceService.getListDeviceIdInScript(homeId, scriptId)), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<ResponeString>(new ResponeString(e.getMessage()) ,HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * Add new script given mode and device
	 * @param deviceId
	 * @param modeId
	 * @param script
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<ResponeString> addScript(@PathVariable int deviceId,@PathVariable int modeId,@RequestBody Script script ){
		
		int homeId = homeService.getHomeIdGivenMode(modeId);
		
		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<ResponeString>(HttpStatus.UNAUTHORIZED);
		}
		
		int addedScriptId;
		try {
			addedScriptId = deviceService.addScript(script, deviceId, modeId , homeId);
			if (addedScriptId > 0) {
				String URINewAddedObject = String.format( "devices/%s/modes/%s/scripts/%s", deviceId, modeId, addedScriptId);
				return new ResponseEntity<ResponeString>(new ResponeString(addedScriptId, URINewAddedObject, deviceService.getListDeviceIdInScript(homeId, script)),HttpStatus.CREATED);
			}
		} catch (Exception e) {
			return new ResponseEntity<ResponeString>(new ResponeString(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<ResponeString>(HttpStatus.BAD_REQUEST);
	}

}
