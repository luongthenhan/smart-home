package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.BUZZER;
import static com.hcmut.smarthome.utils.ConstantUtil.CONTROL_BLOCK_FROM_TO;
import static com.hcmut.smarthome.utils.ConstantUtil.CONTROL_BLOCK_IF;
import static com.hcmut.smarthome.utils.ConstantUtil.CONTROL_BLOCK_IF_ELSE;
import static com.hcmut.smarthome.utils.ConstantUtil.EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.GAS_SENSOR;
import static com.hcmut.smarthome.utils.ConstantUtil.GREATER_OR_EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.GREATER_THAN;
import static com.hcmut.smarthome.utils.ConstantUtil.LESS_OR_EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.LESS_THAN;
import static com.hcmut.smarthome.utils.ConstantUtil.LIGHT;
import static com.hcmut.smarthome.utils.ConstantUtil.LIGHT_SENSOR;
import static com.hcmut.smarthome.utils.ConstantUtil.MOTION_SENSOR;
import static com.hcmut.smarthome.utils.ConstantUtil.NOT_EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.SIZE_CONTROL_BLOCK_IF;
import static com.hcmut.smarthome.utils.ConstantUtil.SIZE_CONTROL_BLOCK_IF_ELSE;
import static com.hcmut.smarthome.utils.ConstantUtil.TAKE_A_SHOT;
import static com.hcmut.smarthome.utils.ConstantUtil.TEMPERATURE_SENSOR;
import static com.hcmut.smarthome.utils.ConstantUtil.TOGGLE_BUZZER;
import static com.hcmut.smarthome.utils.ConstantUtil.TOGGLE_LIGHT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.device.controller.IGeneralController;
import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.scenario.model.Action;
import com.hcmut.smarthome.scenario.model.Condition;
import com.hcmut.smarthome.scenario.model.ControlBlock;
import com.hcmut.smarthome.scenario.model.ControlBlockFromTo;
import com.hcmut.smarthome.scenario.model.ControlBlockIf;
import com.hcmut.smarthome.scenario.model.ControlBlockIfElse;
import com.hcmut.smarthome.scenario.model.IBlock;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.scenario.model.SimpleAction;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.ConstantUtil;

@Service
public class ScenarioService implements IScenarioService {

	private JSONParser parser = new JSONParser();

	@Autowired
	private IGeneralController deviceController;
	
	@Autowired
	private IDeviceService deviceService;
	
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

		// deviceName = object.get(1).toString()
		//TODO: Now hard code homeId
		case TOGGLE_LIGHT:
			Supplier<Void> toggleLight = () -> {
				try {
					deviceController.toggle(deviceService.getDevice(ConstantUtil.HOME_ID,
							Integer.valueOf(object.get(1).toString())));
				} catch (Exception e) {
					System.out.println("Error: " + e.getMessage());
				}
				return null;
			};
			block = setupSimpleAction(TOGGLE_LIGHT, toggleLight, Void.class);
			break;

		case TOGGLE_BUZZER:
			Supplier<Void> toggleBuzzer = () -> {
				try {
					deviceController.toggle(deviceService.getDevice(ConstantUtil.HOME_ID,
							Integer.valueOf(object.get(1).toString())));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				return null;
			};
			block = setupSimpleAction(TOGGLE_BUZZER, toggleBuzzer, Void.class);
			break;

		case TAKE_A_SHOT:
			Supplier<Object> takePicture = () -> {
				Object picture = null;
				try {
					picture = deviceController.takeAPhoto(deviceService
							.getDevice(ConstantUtil.HOME_ID,
									Integer.valueOf(object.get(1).toString())));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				return picture;
			};
			block = setupSimpleAction(TAKE_A_SHOT, takePicture, Object.class);
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

	private IBlock setupSimpleAction(String actionName , Supplier<?> method, Class<?> methodReturnType){
		SimpleAction simpleAction = new SimpleAction();
		simpleAction.setName(actionName);
		simpleAction.setAction(t -> simpleAction.setValue(methodReturnType.cast(method.get())));
		return simpleAction;
	}
	
	private IBlock setupConditions(JSONArray object) {
		IBlock block = null;
		int deviceId = Integer.valueOf(object.get(0).toString());
		Device device = deviceService.getDevice(ConstantUtil.HOME_ID, deviceId);
		String deviceTypeName = device.getDeviceType().getTypeName();
		Supplier<Object> method;
		
//		if (is(deviceTypeName, LIGHT_SENSOR)) {
//			method = () -> deviceController.isNight(device);
//			block = setupCondition(object, LIGHT_SENSOR, method, Boolean.class);
//		} else 
			if (is(deviceTypeName, TEMPERATURE_SENSOR)) {
			method = () -> {
				Float temp = null;
				try {
					temp = deviceController.getTemperature(device);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				return temp;};
			block = setupCondition(object, TEMPERATURE_SENSOR, method, Float.class);
		} 
//		else if (is(deviceTypeName, GAS_SENSOR)) {
//			method = () -> deviceService.getGasThreshold(deviceTypeName);
//			block = setupCondition(object, GAS_SENSOR, method, Float.class );
//		} else if (is(deviceTypeName, MOTION_SENSOR)) {
//			// TODO: Not Support Motion sensor now
//		} else if (is(deviceTypeName, LIGHT)) {
//			method = () -> deviceService.isLightOn(deviceTypeName);
//			block = setupCondition(object, LIGHT, method , Boolean.class);
//		} else if (is(deviceTypeName, BUZZER)) {
//			method = () -> deviceService.isBuzzerBeep(deviceTypeName);
//			block = setupCondition(object, BUZZER, method , Boolean.class);
//		}

		return block;
	}
	


	private Condition setupCondition(JSONArray object, String conditionName,
			Supplier<Object> method, Class<?> methodReturnType) {
		Condition condition = new Condition();
		condition.setName(conditionName);
		condition.setLogicOperator(object.get(1).toString());
		
		if( methodReturnType.equals(Boolean.class) ){
			condition.setValue(Boolean.valueOf(object.get(2).toString()));
		}else if (methodReturnType.equals(Float.class)){
			condition.setValue(Float.valueOf(object.get(2).toString()));
		}else condition.setValue(object.get(2));
		
		
		switch (condition.getLogicOperator()) {
		case EQUAL:
			condition.setPredicate(t -> method.get() == condition.getValue());
			break;
		case NOT_EQUAL:
			condition.setPredicate(t -> method.get() != condition.getValue());
			break;
		case GREATER_OR_EQUAL:
			condition.setPredicate(t -> (float)method.get() >= (float)condition.getValue());
			break;
		case GREATER_THAN:
			condition.setPredicate(t -> (float)method.get() > (float)condition.getValue());
			break;
		case LESS_OR_EQUAL:
			condition.setPredicate(t -> (float)method.get() <= (float)condition.getValue());
			break;
		case LESS_THAN:
			condition.setPredicate(t -> (float)method.get() < (float)condition.getValue());
			break;

		default:
			System.out.println("Chua support operator "
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
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
						
					@Override
					public void run() {
						runControlBlock((ControlBlock) block);
						
					}
				}, 0 , 2000);
				
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

	private boolean is(String blockName, String deviceType) {
		return blockName.indexOf(deviceType) == 0;
	}
}
