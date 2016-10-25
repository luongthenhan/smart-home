package com.hcmut.smarthome.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.NotSupportedException;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.model.Script;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.ConflictConditionException;

@Service
public class ScenarioService implements IScenarioService {

	private static final Logger LOGGER = Logger
			.getLogger(ScenarioService.class);

	// TODO: Remove new object here after testing
	@Autowired
	private ScenarioCreator scenarioCreator;
	
	@Autowired
	private ScenarioRunner scenarioRunner;
	
	@Autowired
	private ScenarioConflictValidator scenarioConflictValidator;
	
	@Autowired
	private IDeviceService deviceService;
	
	public String JSONToString() {
		throw new UnsupportedOperationException("Not supported");
	}

	// TODO: when update , don't forget that input scenario may exist in one of them
	@Override
	public boolean isValid(int modeId, int deviceId, Script script, Scenario scenario) throws ParseException, NotSupportedException, ConflictConditionException{
		List<Script> existedScripts = deviceService.getScripts(modeId,deviceId);
		
		List<Scenario> existedScenarios = new ArrayList<Scenario>();
		for (Script existedScript : existedScripts) {
			
			if( isScriptExisted(script.getContent(), existedScript.getContent()) )
				return false;
			
			if( checkExistingName(script.getName(), existedScript.getName()) )
				return false;
			
			Scenario existedScenario = JSONToScenario(existedScript.getContent());
			existedScenarios.add(existedScenario);
		}
		
		if( scenario != null )
			return isNotConflicted(scenario, existedScenarios);
		return true;
	}
	
	private boolean isScriptExisted(String inputScriptContent, String existedScriptContent){
		if( inputScriptContent.contains(existedScriptContent) || existedScriptContent.contains(inputScriptContent) )
			return true;
		return false;
	}
	
	
	private boolean checkExistingName(String inputScriptName, String existedScriptName) {
		if( inputScriptName != null && existedScriptName != null){
			if( "".equals(inputScriptName) )
				return false;
			else if ( existedScriptName.equals(inputScriptName) )
				return true;
		}
		return false;
	}
	
	private boolean isNotConflicted(Scenario inputScenario,
			List<Scenario> existedScenarios) throws NotSupportedException, ConflictConditionException {
		return scenarioConflictValidator.isNotConflicted(inputScenario, existedScenarios);
	}
	
	// TODO : When one script is removed , how to know and get rid of it and
	// also stop the timer
	@Override
	public void runScenario(Scenario scenario) {
		scenarioRunner.runScenario(scenario);
	}

	@Override
	public void stopScenario(int id) {
		scenarioRunner.stopScenario(id);
	}

	@Override
	public void stopScenarioInHome(int homeId){
		scenarioRunner.stopScenarioInHome(homeId);
	}
	
	@Override
	public void stopScenarioInDevice(int deviceId){
		scenarioRunner.stopScenarioInDevice(deviceId);
	}
	
	@Override
	public void stopForeverScenario(int id) {
		scenarioRunner.stopForeverScenario(id);
	}

	@Override
	public void stopForeverScenarioInHome(int homeId){
		scenarioRunner.stopForeverScenarioInHome(homeId);
	}
	
	@Override
	public void stopForeverScenarioInDevice(int deviceId){
		scenarioRunner.stopForeverScenarioInDevice(deviceId);
	}
	
	// TODO: Change parameter from String to Script ( for assigning id to
	// scenario after return)
	@Override
	public Scenario JSONToScenario(String script) throws ParseException {
		return scenarioCreator.from(script);
	}
}
