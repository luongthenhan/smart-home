package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.BOTH_IF_ELSE_BLOCK_YIELD_SAME_ACTION;
import static com.hcmut.smarthome.utils.ConstantUtil.EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.GREATER_OR_EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.GREATER_THAN;
import static com.hcmut.smarthome.utils.ConstantUtil.LESS_OR_EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.LESS_THAN;
import static com.hcmut.smarthome.utils.ConstantUtil.NOT_EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.SCRIPT_CONFLICT_ITSELF;

import java.time.LocalTime;
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
	
	private static final Boolean PASS_INNERMOST_CONDITION_RANGE = true;


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
	public boolean isValid(Scenario inputScenario,
			List<Scenario> existedScenarios) throws NotSupportedException, ConflictConditionException {

		Stack<Condition> stackConditions = new Stack<>();
		// Find out pair of action & condition of the inputScenario to be compared to existed list
		// of scenario's blocks
		List<Pair<Condition, SimpleAction>> listActionsAndConditionsToCompare = 
				findListSimpleActionsAndRequireConditionsOfInputScenarioToCompare(inputScenario.getBlocks(), stackConditions);

		// Check stupid script here
		if( !listActionsAndConditionsToCompare.isEmpty() ){
			for(int i = 0 ; i < listActionsAndConditionsToCompare.size(); ++i)
				for(int j = i ; j < listActionsAndConditionsToCompare.size() ; ++j){
					
					if( i == j )
						continue;
					
					SimpleAction action1 = listActionsAndConditionsToCompare.get(i).getSecond();
					SimpleAction action2 = listActionsAndConditionsToCompare.get(j).getSecond();
					
					if( action1.equals(action2)){
						Condition condition1 = listActionsAndConditionsToCompare.get(i).getFirst();
						Condition condition2 = listActionsAndConditionsToCompare.get(j).getFirst();
						
						if( condition1 != null && condition2 != null 
								&& condition1.getValueClassType() != null
								&& !condition1.getValueClassType().equals(LocalTime.class)
								&& condition2.getValueClassType() != null
								&& !condition2.getValueClassType().equals(LocalTime.class)
								&& condition1.equals(getElseCondition(condition2)))
							throw new ConflictConditionException(BOTH_IF_ELSE_BLOCK_YIELD_SAME_ACTION);
					}
				}
		}
		
		if( existedScenarios == null )
			return true;
		
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
					Map<String,Boolean> checkedRange = new HashMap<>();
					
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
	 * @param stackOuterConditions just need to pass new Stack<>(), used to map the latest condition to latest action when trace back
	 * @return
	 * @throws NotSupportedException
	 * @throws ConflictConditionException 
	 */
	private List<Pair<Condition, SimpleAction>> findListSimpleActionsAndRequireConditionsOfInputScenarioToCompare(
			List<IBlock> blocks, Stack<Condition> stackOuterConditions)
			throws NotSupportedException, ConflictConditionException {

		List<Pair<Condition, SimpleAction>> pair = new ArrayList<>();

		if (blocks == null)
			return pair;

		// In case that one block has many actions , 
		// if we pop stack ( to exclude the condition ) as soon as we encounter action
		// then all below others, if any , will not find appropriate conditions to make pair  
		for (IBlock block : blocks) {
			if (block instanceof SimpleAction) {
				Condition topOuterCondition = null;
				if (stackOuterConditions != null && !stackOuterConditions.empty())
					topOuterCondition = stackOuterConditions.peek();
				
				pair.add(new Pair<Condition, SimpleAction>(
						topOuterCondition, (SimpleAction) block));
			}
			else if ( block instanceof ControlBlockFromTo ){
				ControlBlockFromTo blocksFromTo = (ControlBlockFromTo) block;
				Condition innerFromToCondition = blocksFromTo.getCondition();
				mergeInnerWithOuterConditionFromToBlock(innerFromToCondition, stackOuterConditions);
				stackOuterConditions.push(innerFromToCondition);
				
				pair.addAll(findListSimpleActionsAndRequireConditionsOfInputScenarioToCompare(
						blocksFromTo.getAction().getBlocks(),
						stackOuterConditions));
			}
			// Block If, IfElse
			else if (block instanceof ControlBlock) {
				// Push If condition to stack and continue finding out in block
				// If actions
				Condition innerIfCondition = ((ControlBlock) block).getCondition();
				mergeInnerWithOuterConditions(innerIfCondition, stackOuterConditions);
				stackOuterConditions.push(innerIfCondition);
				
				pair.addAll(findListSimpleActionsAndRequireConditionsOfInputScenarioToCompare(
								((ControlBlock) block).getAction().getBlocks(),
								stackOuterConditions));

				if (block instanceof ControlBlockIfElse) {
					ControlBlockIfElse blockIfElse = (ControlBlockIfElse) block;
					// Push Else condition to stack and continue finding out in
					// block Else actions

					Condition innerElseCondition = getElseCondition(blockIfElse
							.getCondition());
					mergeInnerWithOuterConditions(innerElseCondition, stackOuterConditions);
					stackOuterConditions.push(innerElseCondition);
					pair.addAll(findListSimpleActionsAndRequireConditionsOfInputScenarioToCompare(
									blockIfElse.getElseAction().getBlocks(),
									stackOuterConditions));
				}
			}
		}
		if( !stackOuterConditions.isEmpty() )
			stackOuterConditions.pop();
		
		return pair;
	}

	private void mergeInnerWithOuterConditionFromToBlock(Condition innerCondition, Stack<Condition> stackOuterConditions)
			throws ConflictConditionException, NotSupportedException{
		Condition outerCondition = 
				stackOuterConditions.stream().filter(c -> c.getName().equals(innerCondition.getName())).findAny().orElse(null);
		
		if( innerCondition.getValueClassType().getClass().equals(LocalTime.class) ){
			Range<LocalTime> outerConditionRange = null;
			if( outerCondition == null || outerCondition.getRange() == null)
				outerConditionRange = Range.all();
			else outerConditionRange = outerCondition.getRange();
			Range<LocalTime> intersectionRange = null;
			try{
				intersectionRange = outerConditionRange.intersection(innerCondition.getRange());
			}
			catch(IllegalArgumentException e){
				throw new ConflictConditionException(SCRIPT_CONFLICT_ITSELF);
			}
			
			if( intersectionRange != null && intersectionRange.isEmpty() )
				throw new ConflictConditionException(SCRIPT_CONFLICT_ITSELF);
			
			innerCondition.setRange(intersectionRange);
			
		}
	}
	
	private void mergeInnerWithOuterConditions(Condition innerCondition, Stack<Condition> stackOuterConditions)
			throws ConflictConditionException, NotSupportedException {
		
		// Find the outer condition belong to the same device with current inner one
		Condition outerCondition = 
				stackOuterConditions.stream().filter(c -> c.getName().equals(innerCondition.getName())).findAny().orElse(null);
		
		if( innerCondition.getValue().getClass().equals(Boolean.class) ){
			if( outerCondition != null 
					&& innerCondition != null 
					&& !checkEqualBooleanConditions(innerCondition, outerCondition)){
				throw new ConflictConditionException(SCRIPT_CONFLICT_ITSELF);
			}
		}
		else if( innerCondition.getValue().getClass().equals(Float.class)){
			
			Range<Float> outerConditionRange = null;
			if( outerCondition == null || outerCondition.getRange() == null)
				outerConditionRange = Range.all();
			else outerConditionRange = outerCondition.getRange();
			
			float value = (float) innerCondition.getValue();
			Range<Float> intersectionRange = null;
			
			try{
				switch (innerCondition.getOperator()) {
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
			} // Exception when intersect mean that script is conflicted
			catch(IllegalArgumentException e){
				throw new ConflictConditionException(SCRIPT_CONFLICT_ITSELF);
			}
			
			// Special case : (30-35) ^ [35-40) = [35,35) so called empty range , also mean script is conflicted 
			if( intersectionRange != null && intersectionRange.isEmpty() )
				throw new ConflictConditionException(SCRIPT_CONFLICT_ITSELF);
			
			innerCondition.setRange(intersectionRange);
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
			List<IBlock> existedBlocks, Map<String,Boolean> mapRange) throws NotSupportedException, ConflictConditionException {

		if (existedBlocks == null)
			return false;

		for (IBlock block : existedBlocks) {

			if (block instanceof ControlBlockIf) {
				ControlBlockIf blockIf = (ControlBlockIf) block;

				return
						 areNestedConditionsMatching(conditionToCompare,
								blockIf.getAction().getBlocks(),mapRange)
						||  isConditionTheSameOrOverlap(conditionToCompare,
								blockIf.getCondition(),mapRange);
			} else if (block instanceof ControlBlockIfElse) {
				ControlBlockIfElse blockIfElse = (ControlBlockIfElse) block;
				Condition ifCondition = blockIfElse.getCondition();
				Condition elseCondition = getElseCondition(ifCondition);

				List<IBlock> actionIfBlock = blockIfElse.getAction().getBlocks();
				List<IBlock> actionElseBlock = blockIfElse.getElseAction().getBlocks();
				
				Map<String,Boolean> mapRangeIfBlock = mapRange;
				Map<String,Boolean> mapRangeElseBlock = new HashMap<>(mapRange);
				
				return areNestedConditionsMatching(conditionToCompare,actionIfBlock,mapRangeIfBlock) // check the condition inside current if condition
						|| areNestedConditionsMatching(conditionToCompare,actionElseBlock,mapRangeElseBlock)// check the condition inside current else condition
						|| isConditionTheSameOrOverlap(conditionToCompare, ifCondition,mapRangeIfBlock)
						|| isConditionTheSameOrOverlap(conditionToCompare, elseCondition,mapRangeElseBlock);
			} else if (block instanceof ControlBlockFromTo) {
				ControlBlockFromTo blockFromTo = (ControlBlockFromTo) block;
				// TODO : check here
				return areNestedConditionsMatching(conditionToCompare,
						blockFromTo.getAction().getBlocks(),mapRange)
						|| checkConditionRange(conditionToCompare, blockFromTo.getCondition()) ;
			}

		}

		return false;
	}

	private boolean checkConditionRange(Condition conditionToCompare, Condition existedCondition){
		// Basic case
		if (conditionToCompare == null || existedCondition == null)
			return false;
		
		Range conditionRange = conditionToCompare.getRange();
		Range existedConditionRange = existedCondition.getRange();
		if( conditionRange != null  && existedConditionRange != null
				&& conditionToCompare.getValueClassType() != null
				&& conditionToCompare.getValueClassType().equals(LocalTime.class)
				&& conditionToCompare.getValueClassType().equals(existedCondition.getValueClassType())){
			Range intersection = null;
			try{
			 intersection = conditionRange.intersection(existedConditionRange);
			}
			catch(IllegalArgumentException e){
				return true;
			}
			
			
			if( intersection != null && intersection.isEmpty() )
				return false;
			else return true;
		}
		
		return false;
	}
	
	private Condition getElseCondition(Condition ifCondition)
			throws NotSupportedException {

		Condition elseCondition = new Condition();
		elseCondition.setName(ifCondition.getName());
		elseCondition.setValue(ifCondition.getValue());
		elseCondition.setValueClassType(ifCondition.getValueClassType());

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

	private boolean isConditionTheSameOrOverlap(Condition conditionToCompare, Condition existedCondition, Map<String, Boolean> mapRange)
			throws ConflictConditionException {
		// Basic case
		if (conditionToCompare == null || existedCondition == null)
			return false;

		// Different class type of value
		if (conditionToCompare.getValueClassType() != existedCondition
				.getValueClassType()){
			if( LocalTime.class.equals(conditionToCompare.getValueClassType()) 
					|| LocalTime.class.equals(existedCondition.getValueClassType()) )
					return true;
			return false;
		}
			

		// If two conditions belong to different kind of devices, don't need to check
		if (!conditionToCompare.getName().equals(existedCondition.getName()))
			return false;

		// Boolean value -> check equal : name , operator , value
		if (conditionToCompare.getValue().getClass().equals(Boolean.class)) {
			return checkEqualBooleanConditions(conditionToCompare,
					existedCondition);
		}
		// Float value -> check range
		else if (conditionToCompare.getValue().getClass().equals(Float.class)) {
			return checkOverlapRange(conditionToCompare, existedCondition,
					mapRange);
		}

		return true;
	}

	private boolean checkOverlapRange(Condition conditionToCompare, Condition existedCondition, Map<String, Boolean> mapRange) {
		// Tricky here , if the deepest condition is range condition and it can pass 
		// through the CHECK RANGE , don't need to check with outer condition any more
		if (canPassInnermostConditionRange(conditionToCompare, mapRange))
			return false;
		
		Range<Float> comparedRange = conditionToCompare.getRange();
		Range<Float> existedRange = existedCondition.getRange();
		RangeSet<Float> existedRangeSet = initExistedRangeSet(existedRange);

		try {
			RangeSet<Float> intersection = findIntersection(conditionToCompare,
					comparedRange, existedRangeSet);

			// If empty , it means two conditions are not overlapped
			if (intersection.isEmpty()) {
				// Tricky here , if the innermost condition is range condition
				// and it can pass through the CHECK RANGE , don't need to check
				// with outer condition any more
				mapRange.put(conditionToCompare.getName(), PASS_INNERMOST_CONDITION_RANGE);
				return false;
			} else
				return true;

		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Something wrong");
		}
	}

	private RangeSet<Float> findIntersection(Condition conditionToCompare,
			Range<Float> comparedRange, RangeSet<Float> existedRangeSet) {
		float value = (float) conditionToCompare.getValue();
		RangeSet<Float> intersection = null;
		
		switch (conditionToCompare.getOperator()) {
			case EQUAL:
				comparedRange = (comparedRange == null ? Range.singleton(value) : comparedRange);
				intersection = existedRangeSet.subRangeSet(comparedRange);
				break;
			case NOT_EQUAL:
				comparedRange = (comparedRange == null ? Range.singleton(value) : comparedRange);
				existedRangeSet.remove(comparedRange);
				intersection = existedRangeSet;
				break;
			case GREATER_OR_EQUAL:
				comparedRange = (comparedRange == null ? Range.atLeast(value) : comparedRange);
				intersection = existedRangeSet.subRangeSet(comparedRange);
				break;
			case GREATER_THAN:
				comparedRange = (comparedRange == null ? Range.greaterThan(value) : comparedRange);
				intersection = existedRangeSet.subRangeSet(comparedRange);
				break;
			case LESS_OR_EQUAL:
				comparedRange = (comparedRange == null ? Range.atMost(value) : comparedRange);
				intersection = existedRangeSet.subRangeSet(comparedRange);
				break;
			case LESS_THAN:
				comparedRange = (comparedRange == null ? Range.lessThan(value) : comparedRange);
				intersection = existedRangeSet.subRangeSet(comparedRange);
				break;
		}
		return intersection;
	}

	private RangeSet<Float> initExistedRangeSet(
			Range<Float> existedConditionRange) {
		RangeSet<Float> checkedRange = TreeRangeSet.create();
		
		if (existedConditionRange == null) {
			checkedRange.add(Range.all());
		} else {
			checkedRange.add(existedConditionRange);
		}
		return checkedRange;
	}

	private boolean canPassInnermostConditionRange(
			Condition conditionToCompare, Map<String, Boolean> mapRange) {
		Boolean value = mapRange.get(conditionToCompare.getName());
		return value != null && value.equals(PASS_INNERMOST_CONDITION_RANGE);
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
