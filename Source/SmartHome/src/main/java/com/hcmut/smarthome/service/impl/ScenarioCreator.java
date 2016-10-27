package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.BUZZER;
import static com.hcmut.smarthome.utils.ConstantUtil.CONTROL_BLOCK_FROM_TO;
import static com.hcmut.smarthome.utils.ConstantUtil.CONTROL_BLOCK_IF;
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
import static com.hcmut.smarthome.utils.ConstantUtil.TAKE_PICTURE;
import static com.hcmut.smarthome.utils.ConstantUtil.TEMPERATURE_SENSOR;
import static com.hcmut.smarthome.utils.ConstantUtil.TOGGLE;
import static com.hcmut.smarthome.utils.ConstantUtil.TURN_OFF;
import static com.hcmut.smarthome.utils.ConstantUtil.TURN_ON;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.transaction.NotSupportedException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.collect.Range;
import com.hcmut.smarthome.device.controller.IGeneralController;
import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.scenario.model.Action;
import com.hcmut.smarthome.scenario.model.Condition;
import com.hcmut.smarthome.scenario.model.ControlBlockFromTo;
import com.hcmut.smarthome.scenario.model.ControlBlockIf;
import com.hcmut.smarthome.scenario.model.ControlBlockIfElse;
import com.hcmut.smarthome.scenario.model.IBlock;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.scenario.model.SimpleAction;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.utils.ConflictConditionException;
import com.hcmut.smarthome.utils.ConstantUtil;
import com.hcmut.smarthome.utils.NotFoundException;

@Service
public class ScenarioCreator {
	private final static Logger LOGGER = Logger.getLogger(ScenarioCreator.class);
	
	private final static List<Scenario> ITSELF = null;
	
	private JSONParser parser = new JSONParser();
	
	@Autowired
	@Qualifier(value="local")
	private IGeneralController deviceController;
	
	@Autowired
	private ScenarioConflictValidator scenarioConflictValidator;
	
	@Autowired
	private IDeviceService deviceService;
	
	// TODO: Change parameter from String to Script ( for assigning id to
		// scenario after return)
	@SuppressWarnings("unchecked")
	public Scenario from(String script) throws ParseException, NotSupportedException, ConflictConditionException{
		// Must do that because library can't parse the string with single quote
		JSONArray listControlBlocksOrActions = (JSONArray) parser.parse(script.replace("'", "\""));

		Scenario scenario = new Scenario();
		scenario.setBlocks(new ArrayList<IBlock>());
		listControlBlocksOrActions.forEach(block -> scenario.getBlocks().add(createBlock((JSONArray) block)));

		if( scenarioConflictValidator.isNotConflicted(scenario, ITSELF) )
			return scenario;
		else throw new ConflictConditionException("Can't create scenario because of self-conflicting");
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
		case CONTROL_BLOCK_FROM_TO:
			ControlBlockFromTo conFromTo = new ControlBlockFromTo();
			LocalTime t1,t2;

			try {
				t1 = LocalTime.parse(object.get(1).toString());
				t2 = LocalTime.parse(object.get(2).toString());
			} catch (DateTimeParseException e) {
				throw new DateTimeParseException("Cannot parse the time",
						object.toString(), 0);
			}

			Range<LocalTime> r = Range.closed(t1, t2);
			Condition<LocalTime> c = new Condition<>();
			c.setName("TIME");
			c.setRange(r);
			c.setValueClassType(LocalTime.class);
			conFromTo.setCondition(c);

			conFromTo
					.setAction((Action) createBlock((JSONArray) object.get(3)));
			block = conFromTo;
			break;

		case CONTROL_BLOCK_IF:
			if (isBlockIfThen(object))
				block = setupControlBlockIf(object);
			else if (isBlockIfThenElse(object))
				block = setupControlBlockIfElse(object);
			break;

		// deviceName = object.get(1).toString()
		// TODO: Now hard code homeId
		case TURN_ON:
			Supplier<Void> turnOn = () -> {
				try {
					deviceController.turnOn(deviceService.getDevice(ConstantUtil.HOME_ID,Integer.valueOf(object.get(1).toString())));
				} catch (Exception e) {
					LOGGER.debug("Error: " + e.getMessage());
				}
				return null;
			};
			block = setupSimpleAction(TURN_ON, object.get(1).toString(),
					turnOn, Void.class);
			break;

		case TURN_OFF:
			Supplier<Void> turnOff = () -> {
				try {
					deviceController.turnOff(deviceService.getDevice(
							ConstantUtil.HOME_ID,
							Integer.valueOf(object.get(1).toString())));
				} catch (Exception e) {
					LOGGER.debug("Error: " + e.getMessage());
				}
				return null;
			};
			block = setupSimpleAction(TURN_OFF, object.get(1).toString(),
					turnOff, Void.class);
			break;

		case TOGGLE:
			Supplier<Void> toggle = () -> {
				try {
					deviceController.toggle(deviceService.getDevice(
							ConstantUtil.HOME_ID,
							Integer.valueOf(object.get(1).toString())));
				} catch (Exception e) {
					LOGGER.debug("Error: " + e.getMessage());
				}
				return null;
			};
			block = setupSimpleAction(TOGGLE, object.get(1).toString(), toggle,
					Void.class);
			break;

		case TAKE_PICTURE:
			Supplier<Object> takePicture = () -> {
				Object picture = null;
				try {
					picture = deviceController.takeAPhoto(deviceService
							.getDevice(ConstantUtil.HOME_ID,
									Integer.valueOf(object.get(1).toString())));
				} catch (Exception e) {
					LOGGER.debug(e.getMessage());
				}
				return picture;
			};
			block = setupSimpleAction(TAKE_PICTURE, object.get(1).toString(),
					takePicture, Object.class);
			break;

		// SETUP DEVICE CONDITION
		default:
			try {
				block = setupCondition(object);
			} catch (NotFoundException e) {
				LOGGER.error(e.getMessage());
			}
			break;
		}

		return block;
	}

	/**
	 * Check whether a JSON object is control block If-Then or not
	 * 
	 * @param object
	 * @return
	 */
	private boolean isBlockIfThen(JSONArray object) {
		return object.size() == SIZE_CONTROL_BLOCK_IF;
	}

	/**
	 * Check whether a JSON object is control block If-Then-Else or not
	 * 
	 * @param object
	 * @return
	 */
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

	/**
	 * Check whether a JSON Array contain a list of JSON object or not
	 * 
	 * @param object
	 * @return
	 */
	private boolean isListOfActions(JSONArray object) {
		return object.get(0) instanceof JSONArray;
	}

	/**
	 * Setup one simple action
	 * 
	 * @param actionName
	 * @param actionExpression
	 * @param actionExpressionType
	 * @return
	 */
	private IBlock setupSimpleAction(String actionName, String deviceId,
			Supplier<?> actionExpression, Class<?> actionExpressionType) {
		SimpleAction simpleAction = new SimpleAction();
		simpleAction.setName(actionName);
		simpleAction.setDeviceId(Integer.valueOf(deviceId));
		simpleAction.setAction(t -> simpleAction.setValue(actionExpressionType.cast(actionExpression.get())));
		return simpleAction;
	}

	/**
	 * Setup one condition given JSON object
	 * 
	 * @param object
	 * @return
	 * @throws NotFoundException 
	 */
	private IBlock setupCondition(JSONArray object) throws NotFoundException {
		IBlock block = null;
		// TODO : UNcomment here , ensure device not null when pass to
		// deviceController
		int deviceId = Integer.valueOf(object.get(0).toString());
		Device device = deviceService.getDevice(ConstantUtil.HOME_ID, deviceId);
		String deviceTypeName = device.getDeviceType().getName();
		
//		Device device = null;
//		String deviceTypeName = object.get(0).toString();
		
		Supplier<Object> LHSExpression = () -> null;

		// Check device type
		// Then set up the method to be checked ( as condition )for each device
		if (is(deviceTypeName, LIGHT_SENSOR)) {
			LHSExpression = () -> {
				Boolean result = null;
				try {
					result = deviceController.isNight(device);
				} catch (Exception e) {
					LOGGER.debug("Error: setupConditions + isNight");
				}
				return result;
			};
			block = setupLHSAndRHSCondition(object, LIGHT_SENSOR,
					LHSExpression, Boolean.class);
		} else if (is(deviceTypeName, TEMPERATURE_SENSOR)) {
			LHSExpression = () -> {
				Float temp = null;
				try {
					temp = deviceController.getTemperature(device);
				} catch (Exception e) {

				}
				return temp;
			};
			block = setupLHSAndRHSCondition(object, TEMPERATURE_SENSOR,
					LHSExpression, Float.class);
		} else if (is(deviceTypeName, GAS_SENSOR)) {
			LHSExpression = () -> {
				Boolean result = null;
				try {
					result = deviceController.isDanger(device);
				} catch (Exception e) {
					LOGGER.debug("Error: setupConditions + isDanger");
				}
				return result;
			};
			block = setupLHSAndRHSCondition(object, GAS_SENSOR, LHSExpression,
					Boolean.class);
		} else if (is(deviceTypeName, MOTION_SENSOR)) {
			LHSExpression = () -> {
				Boolean result = null;
				try {
					result = deviceController.hasHuman(device);
				} catch (Exception e) {
					LOGGER.debug("Error: setupConditions + hasHuman");
				}
				return result;
			};
			block = setupLHSAndRHSCondition(object, MOTION_SENSOR,
					LHSExpression, Boolean.class);
		} else if (is(deviceTypeName, LIGHT)) {
			LHSExpression = () -> {
				Boolean result = null;
				try {
					result = deviceController.isOn(device);
				} catch (Exception e) {
					LOGGER.debug("Error: setupConditions + isOn: light");
				}
				return result;
			};
			block = setupLHSAndRHSCondition(object, LIGHT, LHSExpression,
					Boolean.class);
		} else if (is(deviceTypeName, BUZZER)) {
			LHSExpression = () -> {
				Boolean result = null;
				try {
					result = deviceController.isOn(device);
				} catch (Exception e) {
					LOGGER.debug("Error: setupConditions + isOn: buzzer");
				}
				return result;
			};
			block = setupLHSAndRHSCondition(object, BUZZER, LHSExpression,
					Boolean.class);
		}

		return block;
	}

	/**
	 * Setup the LHS and RHS of one condition based on the given JSON object
	 * 
	 * @param object
	 *            the JSON object as condition
	 * @param conditionName
	 * @param LHSExpression
	 *            the left hand side of expression, which will be evaluated when
	 *            check the condition is true or false
	 * @param LHSExpressionType
	 *            the left hand side of expression return type
	 * @return
	 */
	private Condition setupLHSAndRHSCondition(JSONArray object,
			String conditionName, Supplier<Object> LHSExpression,
			Class<?> LHSExpressionType) {
		Condition condition = new Condition();
		// TODO Rename variable name of condition, now it store deviceId
		condition.setName(object.get(0).toString());
		condition.setOperator(object.get(1).toString());

		if (LHSExpressionType.equals(Boolean.class)) {
			condition.setValue(Boolean.valueOf(object.get(2).toString()));
			condition.setValueClassType(Boolean.class);
		} else if (LHSExpressionType.equals(Float.class)) {
			condition.setValue(Float.valueOf(object.get(2).toString()));
			condition.setValueClassType(Float.class);
		} else
			condition.setValue(object.get(2));

		switch (condition.getOperator()) {
		case EQUAL:
			condition.setPredicate(t -> LHSExpression.get() == condition
					.getValue());
			break;
		case NOT_EQUAL:
			condition.setPredicate(t -> LHSExpression.get() != condition
					.getValue());
			break;
		case GREATER_OR_EQUAL:
			condition.setPredicate(t -> (float) LHSExpression.get() >= (float) condition
							.getValue());
			break;
		case GREATER_THAN:
			condition.setPredicate(t -> (float) LHSExpression.get() > (float) condition
							.getValue());
			break;
		case LESS_OR_EQUAL:
			condition.setPredicate(t -> (float) LHSExpression.get() <= (float) condition
							.getValue());
			break;
		case LESS_THAN:
			condition.setPredicate(t -> (float) LHSExpression.get() < (float) condition
							.getValue());
			break;

		default:
			LOGGER.debug("Not support operator "
					+ condition.getOperator());
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

	

	/**
	 * Check whether a device belong to determined deviceType or not
	 * 
	 * @param device
	 * @param deviceType
	 * @return
	 */
	private boolean is(String device, String deviceType) {
		return device.indexOf(deviceType) == 0;
	}
	
	public IGeneralController getDeviceController() {
		return deviceController;
	}

	public void setDeviceController(IGeneralController deviceController) {
		this.deviceController = deviceController;
	}

	public IDeviceService getDeviceService() {
		return deviceService;
	}

	public void setDeviceService(IDeviceService deviceService) {
		this.deviceService = deviceService;
	}
	
}
