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
import com.hcmut.smarthome.service.IHomeService;

@Service
public class ScenarioRunner {
	private static final boolean RUNNING = true;
	private static final boolean STOPPING = false;
	
	private Map<Integer, Boolean> mapScenarioController = new HashMap<>();
	
	@Autowired
	private IHomeService homeService;
	
	public void runScenario(Scenario scenario) {
		if (scenario == null || scenario.getId() == null
				|| scenario.getHomeId() <= 0)
			return;

		// Mark scenario as running
		mapScenarioController.put(scenario.getId(), RUNNING);
		runBlocks(scenario.getBlocks(), scenario.getId(), scenario.getHomeId());
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// Check state is still running or not
				boolean state = mapScenarioController.get(scenario.getId());
				if (state == RUNNING)
					runBlocks(scenario.getBlocks(), scenario.getId(), scenario.getHomeId());
				else if (state == STOPPING)
					this.cancel();

			}
		}, 0, TIMEOUT_CHECK_CONDITION);
		
	}

	public void stopForeverScenario(int id) {
		if (mapScenarioController.containsKey(id))
			mapScenarioController.put(id, STOPPING);
	}
	
	public void stopScenario(int id) {
		return;
	}
	
	/**
	 * Run a list of blocks
	 * 
	 * @param blocks
	 */
	private void runBlocks(List<IBlock> blocks, int scenarioId, int homeId) {
		for (IBlock block : blocks) {
			if (block instanceof SimpleAction) {

				// TODO: Improve performance
				// TODO: Improve performance
				// Only do action when home is enabled
				if (homeService.isEnabled(homeId)) {
					SimpleAction action = (SimpleAction) block;
					action.doAction();
				}

			} else if (block instanceof ControlBlock) {
				runControlBlock((ControlBlock) block, scenarioId, homeId);
				// TODO: Move timeout to each model
			} 
		}
	}

	/**
	 * Run one control block such as: If-Then or If-Then-Else
	 * 
	 * @param controlBlock
	 */
	private void runControlBlock(ControlBlock controlBlock, int scenarioId,
			int homeId) {
		if (controlBlock.getCondition().check()) {
			runBlocks(controlBlock.getAction().getBlocks(), scenarioId, homeId);
		} else if (CONTROL_BLOCK_IF_ELSE.equals(controlBlock.getName())) {
			ControlBlockIfElse controlBlockIfElse = (ControlBlockIfElse) controlBlock;
			runBlocks(controlBlockIfElse.getElseAction().getBlocks(),
					scenarioId, homeId);
		}
		else if ( controlBlock.getClass().equals(ControlBlockFromTo.class) ){
			ControlBlockFromTo controlBlockFromTo = (ControlBlockFromTo) controlBlock;
			if( controlBlockFromTo.getCondition().getRange().contains(LocalTime.now()) )
				runBlocks(controlBlockFromTo.getAction().getBlocks(),
						scenarioId, homeId);
		}
	}
	
}
