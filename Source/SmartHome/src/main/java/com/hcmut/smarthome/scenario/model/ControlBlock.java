package com.hcmut.smarthome.scenario.model;

public abstract class ControlBlock<C extends Comparable<? extends Object>> implements IBlock{
	protected String name;
	protected Condition<C> condition;
	protected Action action;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public Condition<C> getCondition() {
		return condition;
	}

	public void setCondition(Condition<C> condition) {
		this.condition = condition;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}
