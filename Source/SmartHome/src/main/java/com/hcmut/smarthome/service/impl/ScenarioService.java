package com.hcmut.smarthome.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import static com.hcmut.smarthome.utils.ConstantUtil.*;

import com.hcmut.smarthome.dto.Action;
import com.hcmut.smarthome.dto.Condition;
import com.hcmut.smarthome.dto.ControlBlock;
import com.hcmut.smarthome.dto.ControlBlockFromTo;
import com.hcmut.smarthome.dto.ControlBlockIf;
import com.hcmut.smarthome.dto.ControlBlockIfElse;
import com.hcmut.smarthome.dto.IBlock;
import com.hcmut.smarthome.dto.Scenario;
import com.hcmut.smarthome.dto.SimpleAction;
import com.hcmut.smarthome.service.IScenarioService;

@Service
public class ScenarioService implements IScenarioService {

	private static final int SIZE_CONTROL_BLOCK_IF_ELSE = 4;
	private static final int SIZE_CONTROL_BLOCK_IF = 3;

	private JSONParser parser = new JSONParser();

	// TODO : Auto-wire here
	private DeviceService deviceService = getInstance();
	TimerService timerService = new TimerService();

	public String JSONToString() {
		throw new UnsupportedOperationException("Not supported");
	}

	public void runScenario(Scenario scenario) {
		runBlocks(scenario.getBlocks());
	}

	@SuppressWarnings("unchecked")
	public Scenario JSONToScenario(String script) throws ParseException {
		// Must do that because library can't parse the string with single quote
		JSONArray listControlBlocksOrActions = (JSONArray) parser.parse(script
				.replace("'", "\""));

		Scenario scenario = new Scenario();
		scenario.setBlocks(new ArrayList<IBlock>());
		listControlBlocksOrActions.forEach(block -> scenario.getBlocks().add(
				createBlock((JSONArray) block)));

		return scenario;
	}

	/**
	 * Create a element's block and one's contents
	 * 
	 * @param object
	 * @return
	 */
	private IBlock createBlock(JSONArray object) {
		IBlock block = null;

		if (isListOfActions(object))
			return createBlocksInsideArray(object);

		String blockName = object.get(0).toString();

		switch (blockName) {

		// CONTROL BLOCK
		// TODO: Now Hard-code fromValue and toValue
		case CONTROL_BLOCK_FROM_TO:
			ControlBlockFromTo conFromTo = new ControlBlockFromTo();
			block = conFromTo;
			conFromTo.setFromValue(new Date()); // obj(1)
			conFromTo.setToValue(new Date()); // obj(2)
			conFromTo
					.setAction((Action) createBlock((JSONArray) object.get(3)));
			break;

		case CONTROL_BLOCK_IF:
			if (isBlockIfThen(object))
				block = setupControlBlockIf(object);
			else if (isBlockIfThenElse(object))
				block = setupControlBlockIfElse(object);
			break;

		// SIMPLE ACTION
		// deviceName = object.get(1).toString()
		case TOGGLE_LIGHT:
			block = new SimpleAction(TOGGLE_LIGHT,
					t -> deviceService.toggleLight(object.get(1).toString()));
			break;

		case BUZZER_BEEP:
			block = new SimpleAction(BUZZER_BEEP,
					t -> deviceService.toggleBuzzer(object.get(1).toString()));
			break;

		case TAKE_A_SHOT:
			block = new SimpleAction(TAKE_A_SHOT,
					t -> deviceService.takeAShot(object.get(1).toString()));
			break;

		// SETUP DEVICE CONDITION
		default:
			block = setupConditions(object);
			break;
		}

		return block;
	}

	private boolean isBlockIfThen(JSONArray object) {
		return object.size() == SIZE_CONTROL_BLOCK_IF;
	}

	private boolean isBlockIfThenElse(JSONArray object) {
		return object.size() == SIZE_CONTROL_BLOCK_IF_ELSE;
	}

	/**
	 * Create list of element's blocks inside complex one
	 * 
	 * @param objects
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private IBlock createBlocksInsideArray(JSONArray objects) {
		Action action = new Action();
		action.setName("LIST of ACTION | BLOCK");
		action.setBlocks(new ArrayList<>());
		objects.forEach(object -> action.getBlocks().add(
				createBlock((JSONArray) object)));
		return action;
	}

	private boolean isListOfActions(JSONArray object) {
		return object.get(0) instanceof JSONArray;
	}

	private IBlock setupConditions(JSONArray object) {
		IBlock block = null;
		String deviceName = object.get(0).toString();
		if (is(deviceName, LIGHT_SENSOR)) {
			block = setupCondition(object, LIGHT_SENSOR,
					deviceService.isDayLight(deviceName));
		} else if (is(deviceName, TEMPERATURE_SENSOR)) {
			block = setupCondition(object, TEMPERATURE_SENSOR,
					deviceService.getTemperature(deviceName));
		} else if (is(deviceName, GAS_SENSOR)) {
			block = setupCondition(object, GAS_SENSOR,
					deviceService.getGasThreshold(deviceName));
		} else if (is(deviceName, MOTION_SENSOR)) {
			// TODO: Not Support Motion sensor now
		} else if (is(deviceName, LIGHT)) {
			block = setupCondition(object, LIGHT,
					deviceService.isLightOn(deviceName));
		} else if (is(deviceName, BUZZER)) {
			block = setupCondition(object, BUZZER,
					deviceService.isBuzzerBeep(deviceName));
		}

		return block;
	}

	private Condition setupCondition(JSONArray object, String conditionName,
			float comparedValue) {
		Condition condition = new Condition();
		condition.setName(conditionName);
		condition.setLogicOperator(object.get(1).toString());
		condition.setValue(Float.parseFloat(object.get(2).toString()));

		switch (condition.getLogicOperator()) {
		case EQUAL:
			condition.setPredicate(t -> comparedValue == (float) t);
			break;
		case NOT_EQUAL:
			condition.setPredicate(t -> comparedValue != (float) t);
			break;
		case GREATER_OR_EQUAL:
			condition.setPredicate(t -> comparedValue >= (float) t);
			break;
		case GREATER_THAN:
			condition.setPredicate(t -> comparedValue > (float) t);
			break;
		case LESS_OR_EQUAL:
			condition.setPredicate(t -> comparedValue <= (float) t);
			break;
		case LESS_THAN:
			condition.setPredicate(t -> comparedValue < (float) t);
			break;

		default:
			System.out.println("Chua support operator "
					+ condition.getLogicOperator());
			break;
		}

		return condition;
	}

	private Condition setupCondition(JSONArray object, String conditionName,
			boolean comparedValue) {
		Condition condition = new Condition();
		condition.setName(conditionName);
		condition.setLogicOperator(object.get(1).toString());
		condition.setValue(Boolean.parseBoolean(object.get(2).toString()));

		switch (condition.getLogicOperator()) {
		case EQUAL:
			condition.setPredicate(t -> comparedValue == (boolean) t);
			break;
		case NOT_EQUAL:
			condition.setPredicate(t -> comparedValue != (boolean) t);
			break;
		default:
			System.out.println("Not support operator "
					+ condition.getLogicOperator());
			break;
		}

		return condition;
	}

	private ControlBlockIf setupControlBlockIf(JSONArray object) {
		ControlBlockIf controlBlock = new ControlBlockIf();
		controlBlock.setCondition((Condition) createBlock((JSONArray) object
				.get(1)));
		controlBlock.setAction((Action) createBlock((JSONArray) object.get(2)));
		return controlBlock;
	}

	private ControlBlockIfElse setupControlBlockIfElse(JSONArray object) {
		ControlBlockIfElse controlBlockIfElse = new ControlBlockIfElse();
		controlBlockIfElse
				.setCondition((Condition) createBlock((JSONArray) object.get(1)));
		controlBlockIfElse.setAction((Action) createBlock((JSONArray) object
				.get(2)));
		controlBlockIfElse
				.setElseAction((Action) createBlock((JSONArray) object.get(3)));
		return controlBlockIfElse;
	}

	private void runBlocks(List<IBlock> blocks) {
		for (IBlock block : blocks) {
			if (block instanceof SimpleAction) {
				SimpleAction action = (SimpleAction) block;
				action.doAction();
			} else if (block instanceof ControlBlock) {
				runControlBlock((ControlBlock) block);
			} else if (block instanceof ControlBlockFromTo) {
				timerService.schedule(new Date(), new Date(),
						t -> runBlocks(((ControlBlockFromTo) block).getAction()
								.getBlocks()));
			}
		}
	}

	private void runControlBlock(ControlBlock controlBlock) {
		if (controlBlock.getCondition().check()) {
			runBlocks(controlBlock.getAction().getBlocks());
		} else if (CONTROL_BLOCK_IF_ELSE.equals(controlBlock.getName())) {
			ControlBlockIfElse controlBlockIfElse = (ControlBlockIfElse) controlBlock;
			runBlocks(controlBlockIfElse.getElseAction().getBlocks());
		}
	}

	private DeviceService getInstance() {
		if (this.deviceService == null)
			deviceService = new DeviceService();
		return this.deviceService;
	}

	private boolean is(String blockName, String deviceType) {
		return blockName.indexOf(deviceType) == 0;
	}
}
