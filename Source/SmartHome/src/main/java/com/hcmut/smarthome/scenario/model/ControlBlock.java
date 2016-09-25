package com.hcmut.smarthome.scenario.model;

public abstract class ControlBlock implements IBlock{
	protected String name;
	protected Condition condition;
	protected Action action;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}
