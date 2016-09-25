package com.hcmut.smarthome.scenario.model;

import java.util.Date;

import com.hcmut.smarthome.utils.ConstantUtil;

public class ControlBlockFromTo implements IBlock, ICondition{
	private String name;
	private Date fromValue;
	private Date toValue;
	private Action action;
	
	public ControlBlockFromTo() {
		setName(ConstantUtil.CONTROL_BLOCK_FROM_TO);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public Date getFromValue() {
		return fromValue;
	}

	public void setFromValue(Date fromValue) {
		this.fromValue = fromValue;
	}

	public Date getToValue() {
		return toValue;
	}

	public void setToValue(Date toValue) {
		this.toValue = toValue;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		return false;
	}
}
