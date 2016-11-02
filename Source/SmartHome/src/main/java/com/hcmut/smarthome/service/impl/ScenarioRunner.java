package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.CONDITION_CHECKING_PERIOD;
import static com.hcmut.smarthome.utils.ConstantUtil.CONTROL_BLOCK_IF_ELSE;
import static com.hcmut.smarthome.utils.ConstantUtil.DEFAULT_ZONE_ID;
import static com.hcmut.smarthome.utils.ConstantUtil.DELAY;

import java.time.LocalTime;
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
	private static final String NOT_ENOUGH_INFORMATION_OF_SCRIPT_TO_RUN = "Not enough information of script to run";

	private final static Logger LOGGER = Logger.getLogger(ScenarioRunner.class);
	
	private Map<Integer, Scenario> mapScenarioController = new HashMap<>();
	
	@Autowired
	private IHomeService homeService;

	@Autowired
	private IDeviceService deviceService;
	
	public void runScenario( Scenario scenario) throws Exception {
		if (scenario == null || scenario.getId() == null 
				|| scenario.getHomeId() <= 0
				|| scenario.getModeId() <= 0
				|| scenario.getDeviceId() <= 0)
			throw new Exception(NOT_ENOUGH_INFORMATION_OF_SCRIPT_TO_RUN);

		// Mark scenario as running
		scenario.setStatus(ScenarioStatus.RUNNING);
		mapScenarioController.put(scenario.getId(), scenario);
		LOGGER.debug(String.format("New scenario with id %s is running", scenario.getId() ));
		
		scheduleTask(scenario);
		
	}

	private void scheduleTask(Scenario scenario) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				System.out.println("Goes here - timertask in runScenario "+ scenario.getId());
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
						// TODO: Improve performance
						// Only do action when home is enabled || device is enabled
						if( canScenarioRunInCurrentMode(scenario) 
							&& homeService.isEnabled(scenario.getHomeId()) 
							&& deviceService.isDeviceEnabled(scenario.getDeviceId()))
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
			
			private boolean canScenarioRunInCurrentMode(Scenario scenario){
				if( homeService.getCurrentModeIdGivenHome(scenario.getHomeId()) != scenario.getModeId() ){
					LOGGER.debug(String.format("The script %s can only run in mode %s", scenario.getId(), scenario.getModeId()));
					return false;
				}
				return true;
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
				runControlBlock((ControlBlock) block);
				// TODO: Move timeout to each model
			}
		}
	}

	/**
	 * Run one control block such as: If-Then or If-Then-Else
	 * 
	 * @param controlBlock
	 */
	private void runControlBlock(ControlBlock controlBlock) {
		if ( controlBlock.getClass().equals(ControlBlockFromTo.class) ){
			ControlBlockFromTo controlBlockFromTo = (ControlBlockFromTo) controlBlock;
			// TODO: Consider locale of time
			if( controlBlockFromTo.getCondition().getRange().contains(LocalTime.now(DEFAULT_ZONE_ID)) )
				runBlocks(controlBlockFromTo.getAction().getBlocks());
		}
		else if (controlBlock.getCondition().check()) {
			runBlocks(controlBlock.getAction().getBlocks());
		} else if (CONTROL_BLOCK_IF_ELSE.equals(controlBlock.getName())) {
			ControlBlockIfElse controlBlockIfElse = (ControlBlockIfElse) controlBlock;
			runBlocks(controlBlockIfElse.getElseAction().getBlocks());
		}
		
	}
	
	public void updateScenarioStatus(int scenarioId, ScenarioStatus status){
		if (mapScenarioController.containsKey(scenarioId))
			mapScenarioController.get(scenarioId).setStatus(status);
	}
	
	public void updateAllScenarioStatusOfHome(int homeId, ScenarioStatus status){
		mapScenarioController.forEach((key,scenario) -> {
			if( scenario.getHomeId() == homeId ) 
				scenario.setStatus(status);
		});
	}

	public void updateAllScenarioStatusOfDevice(int deviceId, ScenarioStatus status){
		mapScenarioController.forEach((key,scenario) -> {
			if( scenario.getDeviceId() == deviceId ) 
				scenario.setStatus(status);
		});
	}

	public void updateAllScenarioStatusOfMode(int modeId, ScenarioStatus status){
		mapScenarioController.forEach((key,scenario) -> {
			if( scenario.getModeId() == modeId ) 
				scenario.setStatus(status);
		});
	}

	public boolean replaceOldScenarioWithNewOne(int scenarioId, Scenario newScenario) throws Exception{
		Scenario oldScenario = mapScenarioController.get(scenarioId);
		if( oldScenario == null )
			throw new NotFoundException(String.format("Found no script with id %s to update", scenarioId));
		
		newScenario.setId(oldScenario.getId());
		newScenario.setHomeId(oldScenario.getHomeId());
		newScenario.setDeviceId(oldScenario.getDeviceId());
		newScenario.setModeId(oldScenario.getModeId());
		newScenario.setTimeout(oldScenario.getTimeout());
		
		updateScenarioStatus(scenarioId, ScenarioStatus.STOP_FOREVER);
		// TODO: So far, we may put scenario.getTimeout() here
		Thread.sleep(CONDITION_CHECKING_PERIOD + DELAY);
		runScenario(newScenario);
		return true;
	}
}
