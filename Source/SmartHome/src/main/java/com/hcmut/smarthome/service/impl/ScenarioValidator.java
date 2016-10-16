package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.GREATER_OR_EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.GREATER_THAN;
import static com.hcmut.smarthome.utils.ConstantUtil.LESS_OR_EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.LESS_THAN;
import static com.hcmut.smarthome.utils.ConstantUtil.NOT_EQUAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import javax.transaction.NotSupportedException;

import org.springframework.stereotype.Service;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.hcmut.smarthome.scenario.model.Condition;
import com.hcmut.smarthome.scenario.model.ControlBlock;
import com.hcmut.smarthome.scenario.model.ControlBlockFromTo;
import com.hcmut.smarthome.scenario.model.ControlBlockIf;
import com.hcmut.smarthome.scenario.model.ControlBlockIfElse;
import com.hcmut.smarthome.scenario.model.IBlock;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.scenario.model.SimpleAction;
import com.hcmut.smarthome.utils.ConflictConditionException;
import com.hcmut.smarthome.utils.ConstantUtil;
import com.hcmut.smarthome.utils.Pair;

/**
 * Singleton Scenario validator class
 *
 */
@Service
public class ScenarioValidator {
	
	public boolean isScriptExisted(String inputScript, List<String> existedScripts){
		for (String existedScript : existedScripts) {
			if( inputScript.contains(existedScript) || existedScript.contains(inputScript) )
				return true;
		}
		return false;
	}
	
	// TODO:
	/* FIX 
	 * 1. range ( 34 - 65 ) and range ( -inf, 30 )
	 * 2. Self conflict 
	 * 3. Script contains another one or vice versa -> return false ( check by string compare )
	 * 
	 * New
	 * 4. If else with same action
	 */	
	public boolean isScenarioValidate(Scenario inputScenario,
			List<Scenario> existedScenarios) throws NotSupportedException, ConflictConditionException {

		Stack<Condition> stackConditions = new Stack<>();
		// Find out pair of action & condition of the inputScenario to be compared to existed list
		// of scenario's blocks
		List<Pair<Condition, SimpleAction>> listActionsAndConditionsToCompare = 
				findListSimpleActionsAndRequireConditionsOfInputScenarioToCompare(inputScenario.getBlocks(), stackConditions);

		// Check for each pair
		for (Pair<Condition, SimpleAction> actionAndConditionsGroup : listActionsAndConditionsToCompare) {
			for (Scenario existedScenario : existedScenarios) {
				Condition conditionToCompare = actionAndConditionsGroup
						.getFirst();
				SimpleAction actionToCompare = actionAndConditionsGroup
						.getSecond();

				// Check existed counter action first, if any then we trace back
				// to see whether condition is matching or not
					// If counter action is found out
						// if condition to be compared is null , we make no sense to
						// call areNestedCondtionMatching
						// because this action always happen -> not valid script
					// else we need to call areNestedCondtionMatching to find out if
						// any conditions are matching -> not valid script
				if (isCounteractionExisted(actionToCompare,
						existedScenario.getBlocks())) {
					
					// Because only TreeRangeSet support remove function for not equal case
					Map<String,RangeSet<Float>> checkedRange = new HashMap<>();
					
					if (conditionToCompare == null
							|| areNestedConditionsMatching(conditionToCompare,
									existedScenario.getBlocks(), checkedRange))
						return false;
				}
			}
		}
		return true;
	}

	/**
	 * Find the list pair of simple actions and conditions of input scenario used to compare to existing list of scenario
	 * @param blocks list blocks of input scenario
	 * @param stackConditions just need to pass new Stack<>(), used to map the latest condition to latest action when trace back
	 * @return
	 * @throws NotSupportedException
	 * @throws ConflictConditionException 
	 */
	private List<Pair<Condition, SimpleAction>> findListSimpleActionsAndRequireConditionsOfInputScenarioToCompare(
			List<IBlock> blocks, Stack<Condition> stackConditions)
			throws NotSupportedException, ConflictConditionException {

		List<Pair<Condition, SimpleAction>> pairConditionAction = new ArrayList<>();

		if (blocks == null)
			return pairConditionAction;

		// In case that one block has many actions , 
		// if we pop stack ( to exclude the condition ) as soon as we encounter action
		// then all below others, if any , will not find appropriate conditions to make pair  
		for (IBlock block : blocks) {
			if (block instanceof SimpleAction) {
				Condition condition = null;
				if (stackConditions != null && !stackConditions.empty())
					condition = stackConditions.peek();
				
				pairConditionAction.add(new Pair<Condition, SimpleAction>(
						condition, (SimpleAction) block));
			}
			// Block If, IfElse or FromTo
			else if (block instanceof ControlBlock) {
				// Push If condition to stack and continue finding out in block
				// If actions
				Condition ifCondition = ((ControlBlock) block).getCondition();
				mergeInnerWithOuterCondition(ifCondition, stackConditions);
				stackConditions.push(ifCondition);
				
				pairConditionAction
						.addAll(findListSimpleActionsAndRequireConditionsOfInputScenarioToCompare(
								((ControlBlock) block).getAction().getBlocks(),
								stackConditions));

				if (block instanceof ControlBlockIfElse) {
					ControlBlockIfElse blockIfElse = (ControlBlockIfElse) block;
					// Push Else condition to stack and continue finding out in
					// block Else actions

					Condition elseCondition = getElseCondition(blockIfElse
							.getCondition());
					mergeInnerWithOuterCondition(elseCondition, stackConditions);
					stackConditions.push(elseCondition);
					pairConditionAction
							.addAll(findListSimpleActionsAndRequireConditionsOfInputScenarioToCompare(
									blockIfElse.getElseAction().getBlocks(),
									stackConditions));
				}
			}
		}
		if( !stackConditions.isEmpty() )
			stackConditions.pop();
		
		return pairConditionAction;
	}

	private void mergeInnerWithOuterCondition(Condition condition, Stack<Condition> stackConditions)
			throws ConflictConditionException, NotSupportedException {
		
		// Trace stack condition to find out 
		Condition outerCondition = 
				stackConditions.stream().filter(c -> c.getName().equals(condition.getName())).findAny().orElse(null);
		
		if( condition.getValue().getClass().equals(Boolean.class) ){
			if( outerCondition != null 
					&& condition != null 
					&& !checkEqualBooleanConditions(condition, outerCondition)){
				throw new ConflictConditionException("Script conflict itself");
			}
		}
		else if( condition.getValue().getClass().equals(Float.class)){
			
			Range<Float> outerConditionRange = null;
			if( outerCondition == null || outerCondition.getRange() == null)
				outerConditionRange = Range.all();
			else outerConditionRange = outerCondition.getRange();
			
			float value = (float) condition.getValue();
			Range<Float> intersectionRange = null;
			
			try{
			switch (condition.getOperator()) {
			case GREATER_OR_EQUAL:
				intersectionRange = outerConditionRange.intersection(Range.atLeast(value));
				break;
			case GREATER_THAN:
				intersectionRange = outerConditionRange.intersection(Range.greaterThan(value));
				break;
			case LESS_OR_EQUAL:
				intersectionRange = outerConditionRange.intersection(Range.atMost(value));
				break;
			case LESS_THAN:
				intersectionRange = outerConditionRange.intersection(Range.lessThan(value));
				break;
			}
			}catch(IllegalArgumentException e){
				throw new ConflictConditionException("Script conflict itself");
			}

			condition.setRange(intersectionRange);
		}
		else {
			throw new NotSupportedException("Have not supported this type of value yet");
		}
	}

	/**
	 * The same as checking ( equals(c1,c2) || !equals(c1,reverse(c2)) )
	 * @param c1
	 * @param c2
	 * @return
	 */
	private boolean checkEqualBooleanConditions(Condition c1, Condition c2){
		
		// Not boolean , no need to check
		if( !c1.getValue().getClass().equals(Boolean.class) 
				|| !c2.getValue().getClass().equals(Boolean.class) )
			return false;
		
		// Not same subject , no need to check
		if( !c1.getName().equals(c2.getName()) )
			return false;
		
		// Same relational operator , check whether both value are the same or not 
		if( c1.getOperator().equals(ConstantUtil.EQUAL)
				&& c2.getOperator().equals(ConstantUtil.EQUAL)){
			return c1.getValue().equals(c2.getValue());
		}
		else if( c1.getOperator().equals(ConstantUtil.NOT_EQUAL)
				&& c2.getOperator().equals(ConstantUtil.NOT_EQUAL)){
			return c1.getValue().equals(c2.getValue());
		}
		// Not the same operator , check value not equal or not
		else if(c1.getOperator().equals(ConstantUtil.EQUAL) 
				&& c2.getOperator().equals(ConstantUtil.NOT_EQUAL)){
			return !Objects.equals(c1.getValue(), c2.getValue());
		}
		else if( c2.getOperator().equals(ConstantUtil.EQUAL) 
				&& c1.getOperator().equals(ConstantUtil.NOT_EQUAL)){
			return !Objects.equals(c1.getValue(), c2.getValue());
		}
		
		return true;
	}
	
	private boolean isCounteractionExisted(SimpleAction comparedSimpleAction,
			List<IBlock> existedBlocks) {

		if (existedBlocks == null)
			return false;

		for (IBlock block : existedBlocks) {
			boolean result = false;

			if (block instanceof SimpleAction) {
				return isCounteraction(comparedSimpleAction, (SimpleAction) block);
			} else if (block instanceof ControlBlockIfElse) {
				ControlBlockIfElse blockIfElse = (ControlBlockIfElse) block;
				List<IBlock> actionBlockIf = blockIfElse.getAction().getBlocks();
				List<IBlock> actionBlockElse = blockIfElse.getElseAction().getBlocks();
				
				result = isCounteractionExisted(comparedSimpleAction, actionBlockIf)
						|| isCounteractionExisted(comparedSimpleAction, actionBlockElse);
			}
			// Block If or Block From To
			else if (block instanceof ControlBlock) {
				result = isCounteractionExisted(comparedSimpleAction,
						((ControlBlock) block).getAction().getBlocks());
			}

			if (result)
				return result;
		}

		return false;
	}

	private boolean isCounteraction(SimpleAction toCompare, SimpleAction another) {

		if (toCompare.getDeviceId() != another.getDeviceId())
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
	 * Check whether the nested conditions inside existedBlock are match to or
	 * conflict with the given condition or not
	 * 
	 * @param conditionToCompare
	 * @param existedBlocks
	 * @return
	 * @throws NotSupportedException
	 * @throws ConflictConditionException 
	 */
	private boolean areNestedConditionsMatching(Condition conditionToCompare,
			List<IBlock> existedBlocks, Map<String,RangeSet<Float>> mapRange) throws NotSupportedException, ConflictConditionException {

		if (existedBlocks == null)
			return false;

		for (IBlock block : existedBlocks) {

			if (block instanceof ControlBlockIf) {
				ControlBlockIf blockIf = (ControlBlockIf) block;

				return isConditionTheSameOrOverlap(conditionToCompare,
						blockIf.getCondition(),mapRange)
						|| areNestedConditionsMatching(conditionToCompare,
								blockIf.getAction().getBlocks(),mapRange);
			} else if (block instanceof ControlBlockIfElse) {
				ControlBlockIfElse blockIfElse = (ControlBlockIfElse) block;
				Condition ifCondition = blockIfElse.getCondition();
				Condition elseCondition = getElseCondition(ifCondition);

				List<IBlock> actionIfBlock = blockIfElse.getAction().getBlocks();
				List<IBlock> actionElseBlock = blockIfElse.getElseAction().getBlocks();
				
				Map<String,RangeSet<Float>> mapRangeIfBlock = mapRange;
				Map<String,RangeSet<Float>> mapRangeElseBlock = new HashMap<>(mapRange);
				
				return isConditionTheSameOrOverlap(conditionToCompare, ifCondition,mapRangeIfBlock)
						|| isConditionTheSameOrOverlap(conditionToCompare, elseCondition,mapRangeElseBlock)
						|| areNestedConditionsMatching(conditionToCompare,actionIfBlock,mapRangeIfBlock) // check the condition inside current if condition
						|| areNestedConditionsMatching(conditionToCompare,actionElseBlock,mapRangeElseBlock);// check the condition inside current else condition
			} else if (block instanceof ControlBlockFromTo) {
				ControlBlockFromTo blockFromTo = (ControlBlockFromTo) block;

				return areNestedConditionsMatching(conditionToCompare,
						blockFromTo.getAction().getBlocks(),mapRange);
			}

		}

		return false;
	}

	private Condition getElseCondition(Condition ifCondition)
			throws NotSupportedException {

		Condition elseCondition = new Condition();
		elseCondition.setName(ifCondition.getName());
		elseCondition.setValue(ifCondition.getValue());

		switch (ifCondition.getOperator()) {
		case EQUAL:
			elseCondition.setOperator(NOT_EQUAL);
			break;
		case NOT_EQUAL:
			elseCondition.setOperator(EQUAL);
			break;
		case GREATER_OR_EQUAL:
			elseCondition.setOperator(LESS_THAN);
			break;
		case GREATER_THAN:
			elseCondition.setOperator(LESS_OR_EQUAL);
			break;
		case LESS_OR_EQUAL:
			elseCondition.setOperator(GREATER_THAN);
			break;
		case LESS_THAN:
			elseCondition.setOperator(GREATER_OR_EQUAL);
			break;

		default:
			throw new NotSupportedException("Not support: "
					+ ifCondition.getOperator());
		}

		return elseCondition;
	}

	private boolean isConditionTheSameOrOverlap(Condition conditionToCompare, Condition existedCondition, Map<String,RangeSet<Float>> mapRange) throws ConflictConditionException {
		// Basic case
		if( conditionToCompare == null || existedCondition == null )
			return false;
		
		// Different class type of value 
		if( conditionToCompare.getValue().getClass() != existedCondition.getValue().getClass()  )
			return false;
		
		// If two conditions belong to different kind of devices, don't need to check
		if( !conditionToCompare.getName().equals(existedCondition.getName()) )
			return false;
		
		// Boolean value -> check equal : name , operator , value
		if( conditionToCompare.getValue().getClass().equals(Boolean.class) ){
			return checkEqualBooleanConditions(conditionToCompare,existedCondition);
		}
		// Float value -> check range
		else if ( conditionToCompare.getValue().getClass().equals(Float.class)  ){
			String deviceId = conditionToCompare.getName();
			float value = (float) conditionToCompare.getValue();
			float anotherValue = (float) existedCondition.getValue();
			RangeSet<Float> oldRange = mapRange.get(deviceId); 
			if( oldRange == null ){
				oldRange = TreeRangeSet.create();
				oldRange.add(Range.all());
				mapRange.put(deviceId, oldRange);
			}
			
			try{
				switch (existedCondition.getOperator()) {
				case EQUAL: 
					oldRange = oldRange.subRangeSet(Range.singleton(anotherValue));
					break;
				case NOT_EQUAL:
					oldRange.remove(Range.singleton(anotherValue));
					break;
				case GREATER_OR_EQUAL:
					oldRange = oldRange.subRangeSet(Range.atLeast(anotherValue));
					break;
				case GREATER_THAN:
					oldRange = oldRange.subRangeSet(Range.greaterThan(anotherValue));
					break;
				case LESS_OR_EQUAL:
					oldRange = oldRange.subRangeSet(Range.atMost(anotherValue)); 
					break;
				case LESS_THAN:
					oldRange = oldRange.subRangeSet(Range.lessThan(anotherValue));
					break;
					
				}
				
				RangeSet<Float> checkedRange = TreeRangeSet.create(oldRange);
				
				Range<Float> comparedConditionRange = conditionToCompare.getRange();
				
				switch (conditionToCompare.getOperator()) {
				case EQUAL: 
					comparedConditionRange = (comparedConditionRange == null ? Range.singleton(value) : comparedConditionRange);
					checkedRange = checkedRange.subRangeSet(comparedConditionRange);
					break;
				case NOT_EQUAL:
					comparedConditionRange = (comparedConditionRange == null ? Range.singleton(value) : comparedConditionRange);
					checkedRange.remove(comparedConditionRange);
					break;
				case GREATER_OR_EQUAL:
					comparedConditionRange = (comparedConditionRange == null ? Range.atLeast(value) : comparedConditionRange);
					checkedRange = checkedRange.subRangeSet(comparedConditionRange);
					break;
				case GREATER_THAN:
					comparedConditionRange = (comparedConditionRange == null ? Range.greaterThan(value) : comparedConditionRange);
					checkedRange = checkedRange.subRangeSet(comparedConditionRange);
					break;
				case LESS_OR_EQUAL:
					comparedConditionRange = (comparedConditionRange == null ? Range.atMost(value) : comparedConditionRange);
					checkedRange = checkedRange.subRangeSet(comparedConditionRange); 
					break;
				case LESS_THAN:
					comparedConditionRange = (comparedConditionRange == null ? Range.lessThan(value) : comparedConditionRange);
					checkedRange = checkedRange.subRangeSet(comparedConditionRange);
					break;
					
				}
				
				// If empty , it means two conditions are not overlapped 
				if( checkedRange.isEmpty() )
					return false;
				else return true;
					
			} catch (IllegalArgumentException e){
				throw new IllegalArgumentException("Something wrong");
			}
		}
		
		return true;
	}
	
	
	public static void main(String[] args) {
		RangeSet<Integer> rs1 = TreeRangeSet.create();
		rs1.add(Range.atLeast(35));
		rs1.add(Range.closed(10, 20));
		RangeSet<Integer> rs2 = rs1.subRangeSet(Range.atMost(69));
		
		rs2 = rs2.subRangeSet(Range.closed(48, 69));
		
		//RangeSet<Integer> rs2 = ImmutableRangeSet.of(Range.atLeast(35));
		
		//rs1.remove(Range.singleton(30));
		
		//rs2 = rs1.subRangeSet(Range.atLeast(35));
		//rs1.removeAll(rs2);
		//System.out.println(rs2);
		Map<String,String> map = new HashMap<>();
		String a = map.get("h");
		if( a == null ){
			a = "AAA";
			map.put("h", a);
		}
		System.out.println(a.equals(map.get("h")));
		a = "BBB";
		System.out.println(map.get("h"));
		
	}
}
