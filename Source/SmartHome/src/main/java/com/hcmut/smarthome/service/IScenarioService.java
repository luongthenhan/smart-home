package com.hcmut.smarthome.service;

import javax.transaction.NotSupportedException;

import org.json.simple.parser.ParseException;

import com.hcmut.smarthome.model.Script;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.scenario.model.Scenario.ScenarioStatus;
import com.hcmut.smarthome.utils.ConflictConditionException;

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
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	Scenario JSONToScenario(String script) throws ParseException, NotSupportedException, ConflictConditionException;
	
	/**
	 * Check scenario whether it is validate or not <br/>
	 * Validate: name <br/>
	 * Check conflict: scenario
	 * 
	 * @param modeId
	 * @param deviceId
	 * @param script script used to check name valid or not
	 * @param scenario if it is null , just check valid name or not. Otherwise, also check scenario conflict
	 * @return
	 * @throws Exception 
	 */
	boolean isValid(int modeId, int deviceId, Script script, Scenario scenario) throws Exception;

	void updateScenarioStatus(int scenarioId, ScenarioStatus status);

	void updateAllScenarioStatusInHome(int homeId, ScenarioStatus status);

	void updateAllScenarioStatusInDevice(int deviceId, ScenarioStatus status);

	void updateAllScenarioStatusInMode(int modeId, ScenarioStatus status);
}
