package com.hcmut.smarthome.rest;

import java.util.List;

import javax.script.ScriptException;
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

import com.hcmut.smarthome.model.ResponeString;
import com.hcmut.smarthome.model.Script;
import com.hcmut.smarthome.sec.IAuthenticationService;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IHomeService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.ConflictConditionException;
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
	public ResponseEntity<Void> deleteScript(@PathVariable int modeId, @PathVariable int deviceId, @PathVariable int scriptId){
		int homeId = homeService.getHomeIdGivenMode(modeId);
		
		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		
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
		int homeId = homeService.getHomeIdGivenMode(modeId);
		
		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<List<Script>>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<List<Script>>(deviceService.getScripts( modeId, deviceId),HttpStatus.OK);
	}
	
	/**
	 * Update one script
	 * @param scriptId
	 * @param script
	 * @return
	 * @throws ScriptException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 * @throws ParseException 
	 */
	@RequestMapping(method = RequestMethod.PUT, path="/{scriptId}")
	public ResponseEntity<Void> updateScript(@PathVariable int modeId, @PathVariable int deviceId, @PathVariable int scriptId, @RequestBody Script script ) throws ParseException, NotSupportedException, ConflictConditionException, ScriptException{
		int homeId = homeService.getHomeIdGivenMode(modeId);
		
		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		
		deviceService.updateScript(homeId, modeId, deviceId, scriptId,script);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(method = RequestMethod.PATCH, path="/{scriptId}")
	public ResponseEntity<Void> updatePartialScript(@PathVariable int modeId, @PathVariable int deviceId, @PathVariable int scriptId, @RequestBody Script script ) throws ParseException, NotSupportedException, ConflictConditionException, ScriptException{
		int homeId = homeService.getHomeIdGivenMode(modeId);
		
//		if (!authService.isAccessable(homeId)) {
//			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
//		}
		
		deviceService.updatePartialScript(homeId, modeId, deviceId, scriptId, script);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * Add new script given mode and device
	 * @param deviceId
	 * @param modeId
	 * @param script
	 * @return
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 * @throws ScriptException 
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
		} catch (ParseException | NotSupportedException
				| ConflictConditionException | ScriptException e) {
			ResponeString response = new ResponeString(e.getMessage());
			return new ResponseEntity<ResponeString>(response, HttpStatus.BAD_REQUEST);
		}
		if (addedScriptId > 0) {
			String URINewAddedObject = String.format( "devices/%s/modes/%s/scripts/%s", deviceId, modeId, addedScriptId);
			return new ResponseEntity<ResponeString>(new ResponeString(addedScriptId,URINewAddedObject),HttpStatus.CREATED);
		}

		return new ResponseEntity<ResponeString>(HttpStatus.NOT_FOUND);
	}

}
