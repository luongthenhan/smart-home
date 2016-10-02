package com.hcmut.smarthome.controller;

import java.util.Arrays;

import javax.transaction.NotSupportedException;

import org.json.simple.parser.ParseException;

import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.service.impl.ScenarioService;

public class Test {

	public static void main(String[] args) throws ParseException, NotSupportedException {
		String script1 = "[['If',['LightIsOn','=','true'],[['If',['BuzzerIsBeeping','=','true'],[['ToggleBuzzer','A'],['ToggleLight','A']]],[['ToggleLight','B']]]]]";
		String script2 = "[['ToggleBuzzer'],['ToggleLight']]";
		String script3 = "[['If',['Temperature SensorABC','>=','35.5'],[['Toggle','2']]]]";
		String script4 = "[['If',['Light Near Door','=','true'],[['Toggle','Buzzle Near Gas']]]]";
		//"[['ControlBlock','If',['Condition','Light Near Door','=','true'],['Action',['SimpleAction','ToggleBuzzer','Buzzle Near Gas']]]]";
		IScenarioService scenarioService = new ScenarioService();
		//Scenario scenario = scenarioService.JSONToScenario(script3);
		//scenarioService.runScenario(scenario);
		//scenarioService.runScenario(scenario);
	
		// SET 1: Input is more simpler than existing one 
		String inputScript1 = "[['If',['Temperature Sensor','>=','35.5'],[['Turn On','2']]]]";
		String existedScript1 = "[['If',['Temperature Sensor','>=','35.5'],[['If',['Light Sensor','>=','35.5'],[['Turn Off','2']]]]]]";
		String existedScript2 = "[['If',['Temperature Sensor','>=','35.5'],[['Turn Off','2']]]]";
		String existedScript3 = "[['If',['Temperature Sensor','<','35.5'],[['Turn Off','3']],[['Turn Off','2']]]]";
		String existedScript4 = "[['If',['Temperature Sensor','>=','35.5'],[['Turn Off','3']]]]";
		
		// SET 2: Input is more complex than existing one
		String inputScript2 = "[['If',['Temperature Sensor','>=','35.5'],[['If',['Light Sensor 1','>=','35.5'],[['Turn On','2']]]] , [['If',['Light Sensor 2','<=','35.5'],[['Turn On','2']]  ] ]]]";
		String existedScript5 = "[['If',['Light Sensor 1','>=','35.5'], [ ['Turn Off','2'] ] ] ]";
		
		Scenario inputScenario = scenarioService.JSONToScenario(inputScript2);
		Scenario existedScenario = scenarioService.JSONToScenario(existedScript5);
		
		boolean isValidate = scenarioService.isScenarioValidate(inputScenario, Arrays.asList(existedScenario));
		System.out.println("Validation result: " + isValidate);
	}
}
