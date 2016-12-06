package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.CUSTOM_SCRIPT_ID;
import static com.hcmut.smarthome.utils.ConstantUtil.CUSTOM_SCRIPT_TYPE;
import static com.hcmut.smarthome.utils.ConstantUtil.INPUT_SCRIPT_HAS_SAME_NAME_WITH_EXISTING_ONE_IN_SAME_MODE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.NotSupportedException;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.model.Script;
import com.hcmut.smarthome.scenario.model.ControlBlock;
import com.hcmut.smarthome.scenario.model.ControlBlockFromTo;
import com.hcmut.smarthome.scenario.model.ControlBlockIfElse;
import com.hcmut.smarthome.scenario.model.IBlock;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.scenario.model.Scenario.ScenarioStatus;
import com.hcmut.smarthome.scenario.model.SimpleAction;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.ConflictConditionException;
import com.hcmut.smarthome.utils.ScriptBuilder;

@Service
public class ScenarioService implements IScenarioService {

	private static final String CAN_NOT_GET_LIST_DEVICE_ID_FROM_EMPTY_SCENARIO = "Can't get list device id from empty scenario";
	private static final String INPUT_SCRIPT_IS_NULL = "Input script is null";
	private static final String REQUIRED_SCRIPT_TYPE_TO_UPDATE = "Required script type to update";
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
	public boolean isValid(int homeId, int modeId, Script script, Scenario scenario) throws Exception{
		if( hasScriptNotUpdatedNameAndContent(script) )
			return true;
		
		List<Script> existedScripts = deviceService.getAllScriptsGivenMode(modeId);
		return checkValidWithExistingScripts(homeId, script, scenario,
				existedScripts);
	}

	private boolean checkValidWithExistingScripts(int homeId, Script script,
			Scenario scenario, List<Script> existedScripts) throws Exception,
			NotSupportedException, ConflictConditionException {
		List<Scenario> existedScenarios = new ArrayList<Scenario>();
		for (Script existedScript : existedScripts) {
			
			// Don't compare with itself
			if( script.getId() == existedScript.getId() )
				continue;
			
			// Check existing name in mode level , because existed scripts is get by mode
			if( checkExistingName(script.getName(), existedScript.getName()) ){
				LOGGER.debug(INPUT_SCRIPT_HAS_SAME_NAME_WITH_EXISTING_ONE_IN_SAME_MODE);
				throw new Exception(INPUT_SCRIPT_HAS_SAME_NAME_WITH_EXISTING_ONE_IN_SAME_MODE);
			}			
			
			// Below code is used to check content of the script, if the script didn't have content
			// so we just skip this step
			if( script.getContent() == null )
				continue;
			
			Scenario existedScenario = scriptToScenario(homeId, existedScript);
			existedScenarios.add(existedScenario);
		}
		
		if( script.getContent() != null )
			return isNotDuplicated(scenario, existedScenarios)
					&& isNotConflicted(scenario, existedScenarios);
		return true;
	}

	private boolean hasScriptNotUpdatedNameAndContent(Script script) throws Exception {
		if( script == null )
			throw new Exception(INPUT_SCRIPT_IS_NULL);
		
		return script.getName() == null && script.getContent() == null;
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
	
	private boolean isNotDuplicated(Scenario inputScenario, List<Scenario> existedScenarios) 
			throws NotSupportedException, ConflictConditionException{
		
		return !scenarioConflictValidator.checkDuplicateScenario(inputScenario, existedScenarios);
	}
	
	private boolean isNotConflicted(Scenario inputScenario,
			List<Scenario> existedScenarios) throws NotSupportedException, ConflictConditionException {
		return scenarioConflictValidator.isNotConflicted(inputScenario, existedScenarios);
	}
	
	@Override
	public void updateScenarioStatus(int scenarioId, ScenarioStatus status){
		scenarioRunner.updateScenarioStatus(scenarioId, status);
	}
	
	@Override
	public void updateAllScenarioStatusOfHome(int homeId, ScenarioStatus status){
		scenarioRunner.updateAllScenarioStatusOfHome(homeId, status);
	}

	@Override
	public void updateAllScenarioStatusOfDevice(int deviceId, ScenarioStatus status){
		scenarioRunner.updateAllScenarioStatusOfDevice(deviceId, status);
	}

	@Override
	public void updateAllScenarioStatusOfMode(int modeId, ScenarioStatus status){
		scenarioRunner.updateAllScenarioStatusOfMode(modeId, status);
	}
	
	@Override
	public Scenario JSONToScenario(int homeId, String script) throws Exception {
		return scenarioCreator.from(homeId, script);
	}

	@Override
	public Scenario scriptToScenario(int homeId, Script script)
			throws Exception {
		Scenario scenario = null;
		if( script.getContent() != null ){
			String jsonScript = script.getContent();
			
			if( script.getType() == null 
					|| ( script.getType().getName() == null && script.getType().getId() <= 0 ))
				throw new Exception(REQUIRED_SCRIPT_TYPE_TO_UPDATE);
			
			if( CUSTOM_SCRIPT_TYPE.equals(script.getType().getName()) 
					|| CUSTOM_SCRIPT_ID == script.getType().getId()	){
				jsonScript = ScriptBuilder.parseFromCodeAsString(script.getContent(), homeId);
			}
			scenario = JSONToScenario(homeId, jsonScript);
		}
		return scenario;
	}

	@Override
	public boolean replaceOldScenarioWithNewOne(int scenarioId, Scenario newScenario) throws Exception{
		return scenarioRunner.replaceOldScenarioWithNewOne(scenarioId, newScenario);
	}
	
	@Override
	public Set<Integer> getListDeviceIdInScenario(Scenario scenario) throws Exception {
		if( scenario == null )
			throw new Exception(CAN_NOT_GET_LIST_DEVICE_ID_FROM_EMPTY_SCENARIO);
		return getListDeviceIdInScenario(scenario.getBlocks());
	}
	
	private Set<Integer> getListDeviceIdInScenario(List<IBlock> blocks) {

		Set<Integer> set = new HashSet<>();

		if (blocks == null)
			return set;

		for (IBlock block : blocks) {
			if (block instanceof SimpleAction) {
				set.add(((SimpleAction) block).getDeviceId());
			}
			else if ( block instanceof ControlBlockFromTo ){
				ControlBlockFromTo blockFromTo = (ControlBlockFromTo) block;
				set.addAll(getListDeviceIdInScenario( blockFromTo.getAction().getBlocks() ));
			}
			// Block If, IfElse
			else if (block instanceof ControlBlock) {
				ControlBlock<?> blockIf = (ControlBlock<?>) block;
				set.add(Integer.valueOf(blockIf.getCondition().getName()));
				set.addAll(getListDeviceIdInScenario( blockIf.getAction().getBlocks() ));

				if (block instanceof ControlBlockIfElse) {
					ControlBlockIfElse blockIfElse = (ControlBlockIfElse) block;
					set.add(Integer.valueOf(blockIfElse.getCondition().getName()));
					set.addAll(getListDeviceIdInScenario( blockIfElse.getElseAction().getBlocks() ));
				}
			}
		}
		
		return set;
	}
	
	@Override
	public void runScenario(int scenarioId, int homeId, int deviceId, int modeId, Scenario scenario) throws Exception{
		scenarioRunner.runScenario(scenarioId, homeId, deviceId, modeId, scenario);
	}
}
