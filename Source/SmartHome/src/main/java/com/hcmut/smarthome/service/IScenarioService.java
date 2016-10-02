package com.hcmut.smarthome.service;

import java.util.List;

import javax.transaction.NotSupportedException;

import org.json.simple.parser.ParseException;

import com.hcmut.smarthome.scenario.model.Scenario;

public interface IScenarioService {
	/**
	 * Parse a JSONArray object to String
	 * @return
	 */
	String JSONToString();
	
	/**
	 * Run a given scenario 
	 * @param scenario
	 */
	void runScenario(Scenario scenario);
	
	/**
	 * Convert one JSONArray object to Scenario
	 * @param script
	 * @return
	 * @throws ParseException
	 */
	Scenario JSONToScenario(String script) throws ParseException;
	
	/**
	 * Check scenario validation
	 * @param inputScenario
	 * @param existedScenarios
	 * @return
	 */
	boolean isScenarioValidate(Scenario inputScenario, List<Scenario> existedScenarios) throws NotSupportedException;
}
