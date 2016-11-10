package com.hcmut.smarthome.service;

import java.util.Set;

import javax.script.ScriptException;
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
	 * @throws Exception 
	 */
	void runScenario(Scenario scenario) throws Exception;
	
	/**
	 * Convert one JSONArray object to Scenario
	 * @param script
	 * @return
	 * @throws ParseException
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	Scenario JSONToScenario(int homeId, String script) throws ParseException, NotSupportedException, ConflictConditionException;
	
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
	boolean isValid(int homeId, int modeId, int deviceId, Script script, Scenario scenario) throws Exception;

	void updateScenarioStatus(int scenarioId, ScenarioStatus status);

	void updateAllScenarioStatusOfHome(int homeId, ScenarioStatus status);

	void updateAllScenarioStatusOfDevice(int deviceId, ScenarioStatus status);

	void updateAllScenarioStatusOfMode(int modeId, ScenarioStatus status);
	
	/**
	 * Convert script to scenario <br/>
	 * If script is custom type , need to use ScriptBuilder.parse first
	 * @param script
	 * @return
	 * @throws Exception 
	 */
	Scenario scriptToScenario(int homeId, Script script) throws ParseException, ScriptException, NotSupportedException, ConflictConditionException, Exception;
	
	boolean replaceOldScenarioWithNewOne(int scenarioId, Scenario newScenario) throws Exception;

	boolean isValid(int homeId, int modeId, Script script, Scenario scenario)
			throws Exception;

	Set<Integer> getListDeviceIdInScenario(Scenario scenario) throws Exception;
}
