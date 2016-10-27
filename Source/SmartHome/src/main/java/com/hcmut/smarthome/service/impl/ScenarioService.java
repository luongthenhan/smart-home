package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.INPUT_SCRIPT_HAS_SAME_CONTENT_WITH_EXISTING_ONE;
import static com.hcmut.smarthome.utils.ConstantUtil.INPUT_SCRIPT_HAS_SAME_NAME_WITH_EXISTING_ONE_IN_SAME_MODE;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.NotSupportedException;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.model.Script;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.scenario.model.Scenario.ScenarioStatus;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.ConflictConditionException;

@Service
public class ScenarioService implements IScenarioService {

	private static final Logger LOGGER = Logger.getLogger(ScenarioService.class);

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

	@Override
	public boolean isValid(int modeId, int deviceId, Script script, Scenario scenario) throws Exception{
		List<Script> existedScripts = deviceService.getScripts(modeId,deviceId);
		
		List<Scenario> existedScenarios = new ArrayList<Scenario>();
		for (Script existedScript : existedScripts) {
			
			if( script.getContent() != null 
					&& existedScript.getContent() != null
					&& isScriptExisted(script.getContent(), existedScript.getContent()) ){
				LOGGER.debug(INPUT_SCRIPT_HAS_SAME_CONTENT_WITH_EXISTING_ONE);
				return false;
			}
			
			// Check existing name in mode level , because existed scripts is get by mode
			if( checkExistingName(script.getName(), existedScript.getName()) ){
				LOGGER.debug(INPUT_SCRIPT_HAS_SAME_NAME_WITH_EXISTING_ONE_IN_SAME_MODE);
				return false;
			}
			
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
	
	@Override
	public void runScenario(Scenario scenario) {
		scenarioRunner.runScenario(scenario);
	}
	
	@Override
	public void updateScenarioStatus(int scenarioId, ScenarioStatus status){
		scenarioRunner.updateScenarioStatus(scenarioId, status);
	}
	
	@Override
	public void updateAllScenarioStatusInHome(int homeId, ScenarioStatus status){
		scenarioRunner.updateAllScenarioStatusInHome(homeId, status);
	}

	@Override
	public void updateAllScenarioStatusInDevice(int deviceId, ScenarioStatus status){
		scenarioRunner.updateAllScenarioStatusInDevice(deviceId, status);
	}

	@Override
	public void updateAllScenarioStatusInMode(int modeId, ScenarioStatus status){
		scenarioRunner.updateAllScenarioStatusInMode(modeId, status);
	}
	
	@Override
	public Scenario JSONToScenario(String script) throws ParseException, NotSupportedException, ConflictConditionException {
		return scenarioCreator.from(script);
	}
}
