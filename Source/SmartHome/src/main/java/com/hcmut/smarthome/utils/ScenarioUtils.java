package com.hcmut.smarthome.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hcmut.smarthome.scenario.model.ControlBlock;
import com.hcmut.smarthome.scenario.model.ControlBlockFromTo;
import com.hcmut.smarthome.scenario.model.ControlBlockIfElse;
import com.hcmut.smarthome.scenario.model.IBlock;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.scenario.model.SimpleAction;

public class ScenarioUtils {
	
	private static final String CAN_NOT_GET_LIST_DEVICE_ID_FROM_EMPTY_SCENARIO = "Can't get list device id from empty scenario";
	
	public static Set<Integer> getListDeviceIdInScenario(Scenario scenario) throws Exception {
		if( scenario == null )
			throw new Exception(CAN_NOT_GET_LIST_DEVICE_ID_FROM_EMPTY_SCENARIO);
		return getListDeviceIdInScenario(scenario.getBlocks());
	}
	
	private static Set<Integer> getListDeviceIdInScenario(List<IBlock> blocks) {

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
}
