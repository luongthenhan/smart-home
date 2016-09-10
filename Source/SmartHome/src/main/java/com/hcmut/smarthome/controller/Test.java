package com.hcmut.smarthome.controller;

import org.json.simple.parser.ParseException;

import com.hcmut.smarthome.dto.Scenario;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.service.impl.ScenarioService;

public class Test {

	public static void main(String[] args) throws ParseException {
		String script1 = "[['If',['LightIsOn','=','true'],[['If',['BuzzerIsBeeping','=','true'],[['ToggleBuzzer']],[['ToggleLight']]]],[['ToggleLight']]]]";
		String script2 = "[['ToggleBuzzer'],['ToggleLight']]";
		String script3 = "[['If',['TemperatureSensorABC','=','35.5'],[['ToggleBuzzer','Buzzle Near Gas']]]]";
		String script4 = "[['If',['Light Near Door','=','true'],[['ToggleBuzzer','Buzzle Near Gas']]]]";
		//"[['ControlBlock','If',['Condition','Light Near Door','=','true'],['Action',['SimpleAction','ToggleBuzzer','Buzzle Near Gas']]]]";
		IScenarioService scenarioService = new ScenarioService();
		Scenario scenario = scenarioService.JSONToScenario(script3);
		scenarioService.runScenario(scenario);
		//scenarioService.runScenario(scenario);
		
		
	}

}
