package com.hcmut.smarthome.rest;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IScenarioService;


@RestController
@RequestMapping("/homes")
public class HomeResource {

	@Autowired
	private IDeviceService deviceService;
	
	@Autowired
	private IScenarioService scenarioService;
	
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
