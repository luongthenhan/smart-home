package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.CONDITION_CHECKING_PERIOD;
import static com.hcmut.smarthome.utils.ConstantUtil.CONTROL_BLOCK_IF_ELSE;
import static com.hcmut.smarthome.utils.ConstantUtil.DEFAULT_ZONE_ID;
import static com.hcmut.smarthome.utils.ConstantUtil.DELAY;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.scenario.model.ControlBlock;
import com.hcmut.smarthome.scenario.model.ControlBlockFromTo;
import com.hcmut.smarthome.scenario.model.ControlBlockIfElse;
import com.hcmut.smarthome.scenario.model.IBlock;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.scenario.model.Scenario.ScenarioStatus;
import com.hcmut.smarthome.scenario.model.SimpleAction;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IHomeService;
import com.hcmut.smarthome.utils.NotFoundException;

@Service
public class ScenarioRunner {
	private static final String NOT_FOUND_OLD_SCENARIO_WITH_ID_TO_UPDATE_STATUS = "Not found old scenario with id %s to update status";

	private static final String NOT_ENOUGH_INFORMATION_OF_SCRIPT_TO_RUN = "Not enough information of script to run";

	private final static Logger LOGGER = Logger.getLogger(ScenarioRunner.class);
	
	private Map<Integer, Scenario> mapScenarioController = new HashMap<>();
	
	@Autowired
	private IHomeService homeService;
	
	@Autowired
	private IDeviceService deviceService;

	
	public void runScenario(int scenarioId, int homeId, int deviceId, int modeId, Scenario scenario) throws Exception{
		if( scenarioId > 0 ){
			scenario.setId(scenarioId);
			scenario.setHomeId(homeId);
			scenario.setDeviceId(deviceId);
			scenario.setModeId(modeId);
			runScenario(scenario);
		}
	}
	
	private void runScenario( Scenario scenario) throws Exception {
		if (scenario == null || scenario.getId() == null 
				|| scenario.getHomeId() <= 0
				|| scenario.getModeId() <= 0
				|| scenario.getDeviceId() <= 0)
			throw new Exception(NOT_ENOUGH_INFORMATION_OF_SCRIPT_TO_RUN);

		// Put new scenario to queue
		ScenarioStatus status = determineStatusScenario(scenario.getHomeId(), scenario.getDeviceId(), scenario);
		scenario.setStatus(status);
		
		mapScenarioController.put(scenario.getId(), scenario);
		scheduleTask(scenario);
		
		LOGGER.debug(String.format("New scenario with id %s is put to queue with status %s", scenario.getId(), scenario.getStatus() ));
		
	}

	private ScenarioStatus determineStatusScenario(int homeId, int deviceId, Scenario scenario) throws Exception{
		if( deviceService.checkHomeOrDevicesDisabled(homeId, deviceId, scenario)
				|| !checkIfScenarioCanRunInCurrentModeOrStopIt(scenario))
			return ScenarioStatus.STOPPING;
		return ScenarioStatus.RUNNING;
	}
	
	private boolean checkIfScenarioCanRunInCurrentModeOrStopIt(Scenario scenario){
		if( homeService.getCurrentModeIdGivenHome(scenario.getHomeId()) != scenario.getModeId() ){
			LOGGER.debug(String.format("The script %s can only run in mode %s", scenario.getId(), scenario.getModeId()));
			updateScenarioStatus(scenario.getId(), ScenarioStatus.STOPPING);
			return false;
		}
		return true;
	}
	
	private void scheduleTask(Scenario scenario) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				ScenarioStatus status = mapScenarioController.get(scenario.getId()).getStatus();

				try {
					handleScenarioBasedOnStatus(scenario, status);
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}

			}

			private void handleScenarioBasedOnStatus(Scenario scenario,
					ScenarioStatus status) throws Exception {
				switch (status) {
					case RUNNING:
						runBlocks(scenario.getBlocks());
						break;
					case STOPPING:
						// Just skip
						break;
					case STOP_FOREVER:
						mapScenarioController.remove(scenario.getId());
						LOGGER.debug("Stop forever scenario " + scenario.getId() );
						this.cancel(); 
						break;
					default:
						break;
				}
			}
			
		}, 0, CONDITION_CHECKING_PERIOD);
	}

	
	/**
	 * Run a list of blocks
	 * 
	 * @param blocks
	 */
	private void runBlocks(List<IBlock> blocks) {
		for (IBlock block : blocks) {
			if (block instanceof SimpleAction) {
				SimpleAction action = (SimpleAction) block;
				action.doAction();

			} else if (block instanceof ControlBlock) {
				runControlBlock((ControlBlock<?>) block);
				// TODO: Move timeout to each model
			}
		}
	}

	/**
	 * Run one control block such as: If-Then or If-Then-Else
	 * 
	 * @param controlBlock
	 */
	private void runControlBlock(ControlBlock<?> controlBlock) {
		if ( ControlBlockFromTo.class.equals(controlBlock.getClass())){
			runControlBlockFromTo(controlBlock);
		} else if (controlBlock.getCondition().check()) {
			runBlocks(controlBlock.getAction().getBlocks());
		} else if (CONTROL_BLOCK_IF_ELSE.equals(controlBlock.getName())) {
			ControlBlockIfElse controlBlockIfElse = (ControlBlockIfElse) controlBlock;
			runBlocks(controlBlockIfElse.getElseAction().getBlocks());
		}
		
	}

	private void runControlBlockFromTo(ControlBlock<?> controlBlock) {
		ControlBlockFromTo controlBlockFromTo = (ControlBlockFromTo) controlBlock; 
		// NOTE: Consider locale of time
		LocalDateTime dateTimeToCheck = LocalDateTime.now(DEFAULT_ZONE_ID);
		
		// If date is not defined , it means that we only care the TIME, e.g 14:00 to 23:00 or 22:00 to 02:00
		// Due to time can be cross date, e.g today 22:00 -> tomorrow 02:00
		// -> so we need to check either of @dateTimeToCheck and @dateTimeToCheck.plusDays(1) is contained 
		// in the range or not
		if( !controlBlockFromTo.getCondition().isDateDefined() ){
			LocalDate dateMustBeIgnored = controlBlockFromTo.getCondition().getRange().lowerEndpoint().toLocalDate();
			dateTimeToCheck = dateTimeToCheck.with(dateMustBeIgnored);
			
			if( controlBlockFromTo.getCondition().getRange().contains(dateTimeToCheck) 
					|| controlBlockFromTo.getCondition().getRange().contains(dateTimeToCheck.plusDays(1)))
				runBlocks(controlBlockFromTo.getAction().getBlocks());
		}
	}
	
	public void updateScenarioStatus(int scenarioId, ScenarioStatus status){
		if (mapScenarioController.containsKey(scenarioId)){
			mapScenarioController.get(scenarioId).setStatus(status);
			LOGGER.debug(String.format("Update scenario id %s status %s", scenarioId, status));
		}
	}
	
	public void updateAllScenarioStatusOfHome(int homeId, ScenarioStatus status){
		mapScenarioController.forEach((key,scenario) -> {
			if( scenario.getHomeId() == homeId ){ 
				scenario.setStatus(status);
				LOGGER.debug(String.format("Update scenario id %s status %s", scenario.getId(), status));
			}
		});
	}

	public void updateAllScenarioStatusOfDevice(int deviceId, ScenarioStatus status){
		mapScenarioController.forEach((key,scenario) -> {
			if( scenario.getDeviceId() == deviceId ) {
				scenario.setStatus(status);
				LOGGER.debug(String.format("Update scenario id %s status %s", scenario.getId(), status));
			}
		});
	}

	public void updateAllScenarioStatusOfMode(int modeId, ScenarioStatus status){
		mapScenarioController.forEach((key,scenario) -> {
			if( scenario.getModeId() == modeId ) {
				scenario.setStatus(status);
				LOGGER.debug(String.format("Update scenario id %s status %s", scenario.getId(), status));
			}
		});
	}

	public boolean replaceOldScenarioWithNewOne(int scenarioId, Scenario newScenario) throws Exception{
		Scenario oldScenario = mapScenarioController.get(scenarioId);
		if( oldScenario == null ){
			LOGGER.error(String.format(NOT_FOUND_OLD_SCENARIO_WITH_ID_TO_UPDATE_STATUS, scenarioId));
			throw new NotFoundException(String.format(NOT_FOUND_OLD_SCENARIO_WITH_ID_TO_UPDATE_STATUS, scenarioId));
		}
			
		updateScenarioStatus(scenarioId, ScenarioStatus.STOP_FOREVER);
		
		// TODO: So far, we may put scenario.getTimeout() here
		Thread.sleep(CONDITION_CHECKING_PERIOD + DELAY);
		
		newScenario.setTimeout(oldScenario.getTimeout());
		runScenario(oldScenario.getId(), oldScenario.getHomeId(), oldScenario.getDeviceId(), oldScenario.getModeId(), newScenario);
		
		return true;
	}
}
