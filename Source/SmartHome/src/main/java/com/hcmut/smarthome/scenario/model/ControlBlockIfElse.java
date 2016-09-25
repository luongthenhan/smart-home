package com.hcmut.smarthome.scenario.model;

import com.hcmut.smarthome.utils.ConstantUtil;

public class ControlBlockIfElse extends ControlBlock {
	protected Action elseAction;

	public ControlBlockIfElse(){
		setName(ConstantUtil.CONTROL_BLOCK_IF_ELSE);
	}
	
	public Action getElseAction() {
		return elseAction;
	}

	public void setElseAction(Action elseAction) {
		this.elseAction = elseAction;
	}
}
