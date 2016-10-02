package com.hcmut.smarthome.scenario.model;

import java.util.Date;

import com.hcmut.smarthome.utils.ConstantUtil;

public class ControlBlockFromTo extends ControlBlock{
	private Date fromValue;
	private Date toValue;
	
	public ControlBlockFromTo() {
		setName(ConstantUtil.CONTROL_BLOCK_FROM_TO);
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

	// Not support condition here
	@Override
	public Condition getCondition() {
		return null;
	}

	// Not support condition here
	@Override
	public void setCondition(Condition condition) {
		return;
	}
}
