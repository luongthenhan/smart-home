package com.hcmut.smarthome.service;

import java.util.List;

import javax.transaction.NotSupportedException;

import org.json.simple.parser.ParseException;

import com.hcmut.smarthome.model.Script;
import com.hcmut.smarthome.scenario.model.Scenario;
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
	 */
	Scenario JSONToScenario(String script) throws ParseException;
	
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
	 * @throws ParseException
	 * @throws NotSupportedException
	 * @throws ConflictConditionException
	 */
	boolean isValid(int modeId, int deviceId, Script script, Scenario scenario) throws ParseException, NotSupportedException, ConflictConditionException;
	
	/**
	 * Stop ( remove ) forever a scenario
	 * @param id
	 */
	void stopForeverScenario(int id);
	
	/**
	 * Pause the timer , not run scenario in period of time 
	 * @param id
	 */
	void stopScenario( int id );
}
