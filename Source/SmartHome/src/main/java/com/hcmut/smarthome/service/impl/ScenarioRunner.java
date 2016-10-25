package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.CONTROL_BLOCK_IF_ELSE;
import static com.hcmut.smarthome.utils.ConstantUtil.TIMEOUT_CHECK_CONDITION;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.scenario.model.ControlBlock;
import com.hcmut.smarthome.scenario.model.ControlBlockFromTo;
import com.hcmut.smarthome.scenario.model.ControlBlockIfElse;
import com.hcmut.smarthome.scenario.model.IBlock;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.scenario.model.SimpleAction;
import com.hcmut.smarthome.scenario.model.Scenario.ScenarioStatus;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IHomeService;

@Service
public class ScenarioRunner {
	
	private Map<Integer, Scenario> mapScenarioController = new HashMap<>();
	
	@Autowired
	private IHomeService homeService;

	@Autowired
	private IDeviceService deviceService;
	
	public void run(Integer t){
		Timer timer = new Timer();
		System.out.println(timer.toString());
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// Check state is still running or not
				System.out.println("Goes here - timertask in runScenario " + t );
//				ScenarioStatus status = mapScenarioController.get(scenario.getId()).getStatus();
//				
//				switch (status) {
//					case RUNNING:
//						runBlocks(scenario.getBlocks(), scenario.getDeviceId(), scenario.getDeviceId());
//						break;
//					case STOPPING:
//						// Just skip
//						break;
//					case STOP_FOREVER:
//						this.cancel(); 
//						break;
//					default:
//						break;
//				}

			}
		}, 0, TIMEOUT_CHECK_CONDITION);
	}
	
	public void runScenario( Scenario scenario) {
		if (scenario == null || scenario.getId() == null 
				|| scenario.getHomeId() <= 0
				|| scenario.getDeviceId() <= 0)
			return;

		// Mark scenario as running
		scenario.setStatus(ScenarioStatus.RUNNING);
		mapScenarioController.put(scenario.getId(), scenario);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// Check state is still running or not
				System.out.println("Goes here - timertask in runScenario " + scenario.getId());
				ScenarioStatus status = mapScenarioController.get(scenario.getId()).getStatus();
				
				switch (status) {
					case RUNNING:
						runBlocks(scenario.getBlocks(), scenario.getDeviceId(), scenario.getHomeId());
						break;
					case STOPPING:
						// Just skip
						break;
					case STOP_FOREVER:
						this.cancel(); 
						break;
					default:
						break;
				}

			}
		}, 0, TIMEOUT_CHECK_CONDITION);
		
	}

	/**
	 * Run a list of blocks
	 * 
	 * @param blocks
	 */
	private void runBlocks(List<IBlock> blocks, int deviceId, int homeId) {
		for (IBlock block : blocks) {
			if (block instanceof SimpleAction) {

				// TODO: Improve performance
				// Only do action when home is enabled || device is enabled
				if ( homeService.isEnabled(homeId) && deviceService.isDeviceEnabled(deviceId)) {
					SimpleAction action = (SimpleAction) block;
					action.doAction();
				}

			} else if (block instanceof ControlBlock) {
				runControlBlock((ControlBlock) block, deviceId, homeId);
				// TODO: Move timeout to each model
			} 
		}
	}

	/**
	 * Run one control block such as: If-Then or If-Then-Else
	 * 
	 * @param controlBlock
	 */
	private void runControlBlock(ControlBlock controlBlock, int deviceId, int homeId) {
		if ( controlBlock.getClass().equals(ControlBlockFromTo.class) ){
			ControlBlockFromTo controlBlockFromTo = (ControlBlockFromTo) controlBlock;
			if( controlBlockFromTo.getCondition().getRange().contains(LocalTime.now()) )
				runBlocks(controlBlockFromTo.getAction().getBlocks(),
						deviceId, homeId);
		}
		else if (controlBlock.getCondition().check()) {
			runBlocks(controlBlock.getAction().getBlocks(), deviceId, homeId);
		} else if (CONTROL_BLOCK_IF_ELSE.equals(controlBlock.getName())) {
			ControlBlockIfElse controlBlockIfElse = (ControlBlockIfElse) controlBlock;
			runBlocks(controlBlockIfElse.getElseAction().getBlocks(),
					deviceId, homeId);
		}
		
	}

	public void stopForeverScenario(int scenarioId) {
		if (mapScenarioController.containsKey(scenarioId)){
			mapScenarioController.get(scenarioId).setStatus(ScenarioStatus.STOP_FOREVER);
		}
	}
	
	public void stopScenario(int scenarioId) {
		if (mapScenarioController.containsKey(scenarioId))
			mapScenarioController.get(scenarioId).setStatus(ScenarioStatus.STOPPING);
	}
	
	public void stopForeverScenarioInHome(int homeId) {
		mapScenarioController.forEach((key,scenario) -> {
			if( scenario.getHomeId() == homeId ) 
				scenario.setStatus(ScenarioStatus.STOP_FOREVER);
		});
	}

	public void stopForeverScenarioInDevice(int deviceId) {
		mapScenarioController.forEach((key,scenario) -> {
			if( scenario.getDeviceId() == deviceId ) 
				scenario.setStatus(ScenarioStatus.STOP_FOREVER);
		});
	}

	public void stopScenarioInDevice(int deviceId) {
		mapScenarioController.forEach((key,scenario) -> {
			if( scenario.getDeviceId() == deviceId ) 
				scenario.setStatus(ScenarioStatus.STOPPING);
		});
	}

	public void stopScenarioInHome(int homeId) {
		mapScenarioController.forEach((key,scenario) -> {
			if( scenario.getHomeId() == homeId ) 
				scenario.setStatus(ScenarioStatus.STOPPING);
		});
	}
	
	public void stopScenarioForeverInMode(int modeId) {
		mapScenarioController.forEach((key,scenario) -> {
			if( scenario.getModeId() == modeId ) 
				scenario.setStatus(ScenarioStatus.STOP_FOREVER);
		});
	}
	
	public void stopScenarioInMode(int modeId) {
		mapScenarioController.forEach((key,scenario) -> {
			if( scenario.getModeId() == modeId ) 
				scenario.setStatus(ScenarioStatus.STOPPING);
		});
	}
}
