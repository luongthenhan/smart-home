package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import javax.transaction.NotSupportedException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.device.controller.IGeneralController;
import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.model.Pair;
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

	private static final int TIMEOUT_CHECK_CONDITION = 2000;

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
		// TODO: Now Hard-code fromValue and toValue . Also support range value. Maybe we consider a range as condition
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
		case TURN_ON:
			Supplier<Void> turnOn = () -> null;
			block = setupSimpleAction(TURN_ON, object.get(1).toString(), turnOn, Void.class);
			break;
			
		case TURN_OFF:
			Supplier<Void> turnOff = () -> null;
			block = setupSimpleAction(TURN_OFF, object.get(1).toString(), turnOff, Void.class);
			break;
			
		case TOGGLE:
			Supplier<Void> toggle = () -> {
				try {
					deviceController.toggle(deviceService.getDevice(ConstantUtil.HOME_ID,
							Integer.valueOf(object.get(1).toString())));
				} catch (Exception e) {
					System.out.println("Error: " + e.getMessage());
				}
				return null;
			};
			block = setupSimpleAction(TOGGLE, object.get(1).toString(), toggle, Void.class);
			break;

		case TAKE_PICTURE:
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
			block = setupSimpleAction(TAKE_PICTURE, object.get(1).toString(),takePicture, Object.class);
			break;

		// SETUP DEVICE CONDITION
		default:
			block = setupCondition(object);
			break;
		}

		return block;
	}

	/**
	 * Check whether a JSON object is control block If-Then or not
	 * @param object
	 * @return
	 */
	private boolean isBlockIfThen(JSONArray object) {
		return object.size() == SIZE_CONTROL_BLOCK_IF;
	}

	/**
	 * Check whether a JSON object is control block If-Then-Else or not
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
	 * @param object
	 * @return
	 */
	private boolean isListOfActions(JSONArray object) {
		return object.get(0) instanceof JSONArray;
	}

	/**
	 * Setup one simple action
	 * @param actionName
	 * @param actionExpression
	 * @param actionExpressionType
	 * @return
	 */
	private IBlock setupSimpleAction(String actionName , String deviceId, Supplier<?> actionExpression, Class<?> actionExpressionType){
		SimpleAction simpleAction = new SimpleAction();
		simpleAction.setName(actionName);
		simpleAction.setDeviceId(Integer.valueOf(deviceId));
		simpleAction.setAction(t -> simpleAction.setValue(actionExpressionType.cast(actionExpression.get())));
		return simpleAction;
	}
	
	/**
	 * Setup one condition given JSON object
	 * @param object
	 * @return
	 */
	private IBlock setupCondition(JSONArray object) {
		IBlock block = null;
		// TODO : UNcomment here , ensure device not null when pass to deviceController
		//int deviceId = Integer.valueOf(object.get(0).toString());
		//Device device = deviceService.getDevice(ConstantUtil.HOME_ID, deviceId);
		//String deviceTypeName = device.getDeviceType().getTypeName();
		Supplier<Object> LHSExpression = () -> null;
		
		Device device = null;
		String deviceTypeName = object.get(0).toString();
		
		// Check device type 
		// Then set up the method to be checked ( as condition )for each device 
		if (is(deviceTypeName, LIGHT_SENSOR)) {
			LHSExpression = () -> {
				Boolean result = null;
				try {
					//result = deviceController.isNight(device);
				} catch (Exception e) {
					System.out.println("Error: setupConditions + isNight");
				}
				return result;
			};
			block = setupLHSAndRHSCondition(object, LIGHT_SENSOR, LHSExpression, Boolean.class);
		} else if (is(deviceTypeName, TEMPERATURE_SENSOR)) {
			LHSExpression = () -> {
				Float temp = null;
				try {
					//temp = deviceController.getTemperature(device);
				} catch (Exception e) {

				}
				return temp;
			};
			block = setupLHSAndRHSCondition(object, TEMPERATURE_SENSOR, LHSExpression, Float.class);
		} 
		else if (is(deviceTypeName, GAS_SENSOR)) {
			LHSExpression = () -> {
				Boolean result = null;
				try {
					//result = deviceController.isDanger(device);
				} catch (Exception e) {
					System.out.println("Error: setupConditions + isDanger");
				}
				return result;
			};
			block = setupLHSAndRHSCondition(object, GAS_SENSOR, LHSExpression, Boolean.class );
		} else if (is(deviceTypeName, MOTION_SENSOR)) {
			LHSExpression = () -> {
				Boolean result = null;
				try {
					//result = deviceController.hasHuman(device);
				} catch (Exception e) {
					System.out.println("Error: setupConditions + hasHuman");
				}
				return result;
			};
			block = setupLHSAndRHSCondition(object, MOTION_SENSOR, LHSExpression, Boolean.class );
		} else if (is(deviceTypeName, LIGHT)) {
			LHSExpression = () -> {
				Boolean result = null;
				try {
					//result = deviceController.isOn(device);
				} catch (Exception e) {
					System.out.println("Error: setupConditions + isOn: light");
				}
				return result;
			};
			block = setupLHSAndRHSCondition(object, LIGHT, LHSExpression , Boolean.class);
		} else if (is(deviceTypeName, BUZZER)) {
			LHSExpression = () -> {
				Boolean result = null;
				try {
					//result = deviceController.isOn(device);
				} catch (Exception e) {
					System.out.println("Error: setupConditions + isOn: buzzer");
				}
				return result;
			};
			block = setupLHSAndRHSCondition(object, BUZZER, LHSExpression , Boolean.class);
		}

		return block;
	}
	

	/**
	 * Setup the LHS and RHS of one condition based on the given JSON object
	 * @param object the JSON object as condition
	 * @param conditionName 
	 * @param LHSExpression the left hand side of expression, which will be evaluated when check the condition is true or false
	 * @param LHSExpressionType the left hand side of expression return type
	 * @return
	 */
	private Condition setupLHSAndRHSCondition(JSONArray object, String conditionName,
			Supplier<Object> LHSExpression, Class<?> LHSExpressionType) {
		Condition condition = new Condition();
		//TODO Rename variable name of condition, now it store deviceId
		condition.setName(object.get(0).toString());
		condition.setLogicOperator(object.get(1).toString());
		
		if( LHSExpressionType.equals(Boolean.class) ){
			condition.setValue(Boolean.valueOf(object.get(2).toString()));
		}else if (LHSExpressionType.equals(Float.class)){
			condition.setValue(Float.valueOf(object.get(2).toString()));
		}else condition.setValue(object.get(2));
		
		
		switch (condition.getLogicOperator()) {
		case EQUAL:
			condition.setPredicate(t -> LHSExpression.get() == condition.getValue());
			break;
		case NOT_EQUAL:
			condition.setPredicate(t -> LHSExpression.get() != condition.getValue());
			break;
		case GREATER_OR_EQUAL:
			condition.setPredicate(t -> (float)LHSExpression.get() >= (float)condition.getValue());
			break;
		case GREATER_THAN:
			condition.setPredicate(t -> (float)LHSExpression.get() > (float)condition.getValue());
			break;
		case LESS_OR_EQUAL:
			condition.setPredicate(t -> (float)LHSExpression.get() <= (float)condition.getValue());
			break;
		case LESS_THAN:
			condition.setPredicate(t -> (float)LHSExpression.get() < (float)condition.getValue());
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

	/**
	 * Run a list of blocks
	 * @param blocks
	 */
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
				}, 0 , TIMEOUT_CHECK_CONDITION);
				// TODO: Move timeout to each model
				
			} else if (block instanceof ControlBlockFromTo) {
				timerService.schedule(new Date(), new Date(),
						t -> runBlocks(((ControlBlockFromTo) block).getAction()
								.getBlocks()));
			}
		}
	}

	/**
	 * Run one control block such as: If-Then or If-Then-Else
	 * @param controlBlock
	 */
	private void runControlBlock(ControlBlock controlBlock) {
		if (controlBlock.getCondition().check()) {
			runBlocks(controlBlock.getAction().getBlocks());
		} else if (CONTROL_BLOCK_IF_ELSE.equals(controlBlock.getName())) {
			ControlBlockIfElse controlBlockIfElse = (ControlBlockIfElse) controlBlock;
			runBlocks(controlBlockIfElse.getElseAction().getBlocks());
		}
	}

	/**
	 * Check whether a device belong to determined deviceType or not
	 * @param device
	 * @param deviceType
	 * @return
	 */
	private boolean is(String device, String deviceType) {
		return device.indexOf(deviceType) == 0;
	}
	
	/**
	 * SCENARIO VALIDATION
	 * 
	 */
	
	@Override
	public boolean isScenarioValidate(Scenario inputScenario, List<Scenario> existedScenarios) throws NotSupportedException{
		
		Stack<Condition> stackConditions = new Stack<>();
		// Find out pair of action & condition to be compared among existed list of scenario's blocks 
		List<Pair<Condition,SimpleAction>> listActionsAndConditionsToCompare = 
				findOutListSimpleActionsAndRequireConditions(inputScenario.getBlocks(),stackConditions); 
		
		// Check for each pair 
		for( Pair<Condition,SimpleAction> actionAndConditionsGroup : listActionsAndConditionsToCompare ){
			for (Scenario existedScenario : existedScenarios) {
				Condition conditionToCompare = actionAndConditionsGroup.getFirst();
				SimpleAction actionToCompare = actionAndConditionsGroup.getSecond();
				
				// TODO: In case existed block have only simple actions , we do not care now
				// check existed counter action first, if any then we trace back to see whether condition is
				// matching or not
				// If counter action is found out 
					// if condition to be compared is null , we make no sense to call areNestedCondtionMatching
					// because this action always happen -> not valid script 
					// else we need to call areNestedCondtionMatching to find out if any conditions are matching
					// -> not valid script
				if( isCounteractionExisted( actionToCompare, existedScenario.getBlocks())) {
					if( conditionToCompare == null 
							||  areNestedConditionsMatching(conditionToCompare, existedScenario.getBlocks()) )
						return false;
				}
			}
		}
		return true;
	}
	
	
	private List<Pair<Condition,SimpleAction>> findOutListSimpleActionsAndRequireConditions(List<IBlock> blocks, Stack<Condition> stackConditions) throws NotSupportedException{
		
		List<Pair<Condition,SimpleAction>> pairConditionAction = new ArrayList<>();
		
		if( blocks == null )
			return pairConditionAction;
		
		for (IBlock block : blocks) {
			if (block instanceof SimpleAction) {
				Condition condition = null;
				if( stackConditions != null && !stackConditions.empty() )
					condition = stackConditions.pop();
					
				pairConditionAction.add(new Pair<Condition, SimpleAction>(condition, (SimpleAction)block));
			} 
			// Block If, IfElse or FromTo
			else if( block instanceof ControlBlock ){
				// Push If condition to stack and continue finding out in block If actions 
				stackConditions.push(((ControlBlock)block).getCondition());
				pairConditionAction.addAll(findOutListSimpleActionsAndRequireConditions(((ControlBlock)block).getAction().getBlocks(),stackConditions));
				
				if ( block instanceof ControlBlockIfElse){
					ControlBlockIfElse blockIfElse = (ControlBlockIfElse) block;
					// Push Else condition to stack and continue finding out in block Else actions
					
					stackConditions.push( getElseCondition(blockIfElse.getCondition()) );
					pairConditionAction.addAll(findOutListSimpleActionsAndRequireConditions(blockIfElse.getElseAction().getBlocks(),stackConditions));
				}
			}
		}
		
		return pairConditionAction;
	}
	
	private boolean isCounteractionExisted(SimpleAction actionToCompare,List<IBlock> existedBlocks) {
		
		if( existedBlocks == null )
			return false;
		
		for (IBlock block : existedBlocks) {
			boolean result = false;
			
			if( block instanceof SimpleAction ){
				return isCounteraction(actionToCompare, (SimpleAction)block);
			}
			else if ( block instanceof ControlBlockIfElse ){
				ControlBlockIfElse blockIfElse = (ControlBlockIfElse) block;
				result = isCounteractionExisted(actionToCompare, blockIfElse.getAction().getBlocks())
						|| isCounteractionExisted(actionToCompare, blockIfElse.getElseAction().getBlocks());
			}
			// Block If or Block From To
			else if (block instanceof ControlBlock){
				result = isCounteractionExisted(actionToCompare, ((ControlBlock)block).getAction().getBlocks());
			}
			
			if( result )
				return result;
		}
		
		return false;
	}
	
	private boolean isCounteraction(SimpleAction toCompare,
			SimpleAction another) {
		
		if( toCompare.getDeviceId() != another.getDeviceId() )
			return false;
		
		switch (toCompare.getName()) {
			case ConstantUtil.TURN_ON:
				return ConstantUtil.TURN_OFF.equals(another.getName());
	
			case ConstantUtil.TURN_OFF:
				return ConstantUtil.TURN_ON.equals(another.getName());
	
			default:
				// TODO: Add more counter-action if any
				break;
		}
		
		return false;
	}

	/**
	 * Check whether the nested conditions inside existedBlock are match to or conflict with the given condition or not
	 * @param conditionToCompare
	 * @param existedBlocks
	 * @return
	 * @throws NotSupportedException
	 */
	private boolean areNestedConditionsMatching(Condition conditionToCompare, List<IBlock> existedBlocks) throws NotSupportedException {
		
		if( existedBlocks  == null )
			return false;
		
		for (IBlock block : existedBlocks) {
			
			if( block instanceof ControlBlockIf ){
				ControlBlockIf blockIf = (ControlBlockIf) block;
				
				return isConditionTheSame(conditionToCompare, blockIf.getCondition())
						|| areNestedConditionsMatching(conditionToCompare, blockIf.getAction().getBlocks());
			}
			else if ( block instanceof ControlBlockIfElse ){
				ControlBlockIfElse blockIfElse = (ControlBlockIfElse) block;
				Condition ifCondition = blockIfElse.getCondition();
				Condition elseCondition = getElseCondition(ifCondition);
				
				return isConditionTheSame(conditionToCompare, ifCondition)
						|| isConditionTheSame(conditionToCompare, elseCondition) 
						|| areNestedConditionsMatching(conditionToCompare, blockIfElse.getAction().getBlocks())
						|| areNestedConditionsMatching(conditionToCompare, blockIfElse.getElseAction().getBlocks());
			}
			else if ( block instanceof ControlBlockFromTo ){
				ControlBlockFromTo blockFromTo = (ControlBlockFromTo) block;
				
				return areNestedConditionsMatching(conditionToCompare, blockFromTo.getAction().getBlocks());
			}
			
		}
		
		return false;
	}

	private Condition getElseCondition(Condition ifCondition) throws NotSupportedException {
		
		Condition elseCondition = new Condition();
		elseCondition.setName(ifCondition.getName());
		elseCondition.setValue(ifCondition.getValue());
		
		switch (ifCondition.getLogicOperator()) {
		case EQUAL:
			elseCondition.setLogicOperator(NOT_EQUAL);
			break;
		case NOT_EQUAL:
			elseCondition.setLogicOperator(EQUAL);
			break;
		case GREATER_OR_EQUAL:
			elseCondition.setLogicOperator(LESS_THAN);
			break;
		case GREATER_THAN:
			elseCondition.setLogicOperator(LESS_OR_EQUAL);
			break;
		case LESS_OR_EQUAL:
			elseCondition.setLogicOperator(GREATER_THAN);
			break;
		case LESS_THAN:
			elseCondition.setLogicOperator(GREATER_OR_EQUAL);
			break;

		default:
			throw new NotSupportedException("Not support: " + ifCondition.getLogicOperator() );
		}
		
		return elseCondition;
	}

	// TODO: Check range value, not just equal case
	private boolean isConditionTheSame(Condition toCompare,
			Condition another) {
		
		// if either of condition is null -> false
		// otherwise
			// if operator is equal or not equal , just need only to check same of three value : name, operator , value
			// otherwise
				// check range value to see whether overlapping is occurred or not
		
		return toCompare.getName().equals(another.getName()) 
				&& toCompare.getLogicOperator().equals(another.getLogicOperator())
				&& toCompare.getValue().equals(another.getValue());
	}

	
	
	
	
}
