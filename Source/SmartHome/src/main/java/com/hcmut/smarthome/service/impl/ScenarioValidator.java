package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.GREATER_OR_EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.GREATER_THAN;
import static com.hcmut.smarthome.utils.ConstantUtil.LESS_OR_EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.LESS_THAN;
import static com.hcmut.smarthome.utils.ConstantUtil.NOT_EQUAL;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.transaction.NotSupportedException;

import org.springframework.stereotype.Service;

import com.hcmut.smarthome.scenario.model.Condition;
import com.hcmut.smarthome.scenario.model.ControlBlock;
import com.hcmut.smarthome.scenario.model.ControlBlockFromTo;
import com.hcmut.smarthome.scenario.model.ControlBlockIf;
import com.hcmut.smarthome.scenario.model.ControlBlockIfElse;
import com.hcmut.smarthome.scenario.model.IBlock;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.scenario.model.SimpleAction;
import com.hcmut.smarthome.utils.ConstantUtil;
import com.hcmut.smarthome.utils.Pair;

/**
 * Singleton Scenario validator class
 *
 */
@Service
public class ScenarioValidator {
	
	public boolean isScenarioValidate(Scenario inputScenario,
			List<Scenario> existedScenarios) throws NotSupportedException {

		Stack<Condition> stackConditions = new Stack<>();
		// Find out pair of action & condition to be compared among existed list
		// of scenario's blocks
		List<Pair<Condition, SimpleAction>> listActionsAndConditionsToCompare = findOutListSimpleActionsAndRequireConditions(
				inputScenario.getBlocks(), stackConditions);

		// Check for each pair
		for (Pair<Condition, SimpleAction> actionAndConditionsGroup : listActionsAndConditionsToCompare) {
			for (Scenario existedScenario : existedScenarios) {
				Condition conditionToCompare = actionAndConditionsGroup
						.getFirst();
				SimpleAction actionToCompare = actionAndConditionsGroup
						.getSecond();

				// TODO: In case existed block have only simple actions , we do
				// not care now
				// check existed counter action first, if any then we trace back
				// to see whether condition is
				// matching or not
				// If counter action is found out
				// if condition to be compared is null , we make no sense to
				// call areNestedCondtionMatching
				// because this action always happen -> not valid script
				// else we need to call areNestedCondtionMatching to find out if
				// any conditions are matching
				// -> not valid script
				if (isCounteractionExisted(actionToCompare,
						existedScenario.getBlocks())) {
					if (conditionToCompare == null
							|| areNestedConditionsMatching(conditionToCompare,
									existedScenario.getBlocks()))
						return false;
				}
			}
		}
		return true;
	}

	private List<Pair<Condition, SimpleAction>> findOutListSimpleActionsAndRequireConditions(
			List<IBlock> blocks, Stack<Condition> stackConditions)
			throws NotSupportedException {

		List<Pair<Condition, SimpleAction>> pairConditionAction = new ArrayList<>();

		if (blocks == null)
			return pairConditionAction;

		for (IBlock block : blocks) {
			if (block instanceof SimpleAction) {
				Condition condition = null;
				if (stackConditions != null && !stackConditions.empty())
					condition = stackConditions.pop();

				pairConditionAction.add(new Pair<Condition, SimpleAction>(
						condition, (SimpleAction) block));
			}
			// Block If, IfElse or FromTo
			else if (block instanceof ControlBlock) {
				// Push If condition to stack and continue finding out in block
				// If actions
				stackConditions.push(((ControlBlock) block).getCondition());
				pairConditionAction
						.addAll(findOutListSimpleActionsAndRequireConditions(
								((ControlBlock) block).getAction().getBlocks(),
								stackConditions));

				if (block instanceof ControlBlockIfElse) {
					ControlBlockIfElse blockIfElse = (ControlBlockIfElse) block;
					// Push Else condition to stack and continue finding out in
					// block Else actions

					stackConditions.push(getElseCondition(blockIfElse
							.getCondition()));
					pairConditionAction
							.addAll(findOutListSimpleActionsAndRequireConditions(
									blockIfElse.getElseAction().getBlocks(),
									stackConditions));
				}
			}
		}

		return pairConditionAction;
	}

	private boolean isCounteractionExisted(SimpleAction actionToCompare,
			List<IBlock> existedBlocks) {

		if (existedBlocks == null)
			return false;

		for (IBlock block : existedBlocks) {
			boolean result = false;

			if (block instanceof SimpleAction) {
				return isCounteraction(actionToCompare, (SimpleAction) block);
			} else if (block instanceof ControlBlockIfElse) {
				ControlBlockIfElse blockIfElse = (ControlBlockIfElse) block;
				result = isCounteractionExisted(actionToCompare, blockIfElse
						.getAction().getBlocks())
						|| isCounteractionExisted(actionToCompare, blockIfElse
								.getElseAction().getBlocks());
			}
			// Block If or Block From To
			else if (block instanceof ControlBlock) {
				result = isCounteractionExisted(actionToCompare,
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
	 */
	private boolean areNestedConditionsMatching(Condition conditionToCompare,
			List<IBlock> existedBlocks) throws NotSupportedException {

		if (existedBlocks == null)
			return false;

		for (IBlock block : existedBlocks) {

			if (block instanceof ControlBlockIf) {
				ControlBlockIf blockIf = (ControlBlockIf) block;

				return isConditionTheSame(conditionToCompare,
						blockIf.getCondition())
						|| areNestedConditionsMatching(conditionToCompare,
								blockIf.getAction().getBlocks());
			} else if (block instanceof ControlBlockIfElse) {
				ControlBlockIfElse blockIfElse = (ControlBlockIfElse) block;
				Condition ifCondition = blockIfElse.getCondition();
				Condition elseCondition = getElseCondition(ifCondition);

				return isConditionTheSame(conditionToCompare, ifCondition)
						|| isConditionTheSame(conditionToCompare, elseCondition)
						|| areNestedConditionsMatching(conditionToCompare,
								blockIfElse.getAction().getBlocks())
						|| areNestedConditionsMatching(conditionToCompare,
								blockIfElse.getElseAction().getBlocks());
			} else if (block instanceof ControlBlockFromTo) {
				ControlBlockFromTo blockFromTo = (ControlBlockFromTo) block;

				return areNestedConditionsMatching(conditionToCompare,
						blockFromTo.getAction().getBlocks());
			}

		}

		return false;
	}

	private Condition getElseCondition(Condition ifCondition)
			throws NotSupportedException {

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
			throw new NotSupportedException("Not support: "
					+ ifCondition.getLogicOperator());
		}

		return elseCondition;
	}

	// TODO: Check range value, not just equal case
	private boolean isConditionTheSame(Condition toCompare, Condition another) {

		// if either of condition is null -> false
		// otherwise
		// if operator is equal or not equal , just need only to check same of
		// three value : name, operator , value
		// otherwise
		// check range value to see whether overlapping is occurred or not

		return toCompare.getName().equals(another.getName())
				&& toCompare.getLogicOperator().equals(
						another.getLogicOperator())
				&& toCompare.getValue().equals(another.getValue());
	}
}
