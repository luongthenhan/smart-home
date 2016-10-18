package com.hcmut.smarthome.scenario.model;

import java.time.LocalTime;

import com.hcmut.smarthome.utils.ConstantUtil;

public class ControlBlockFromTo extends ControlBlock<LocalTime>{
	public ControlBlockFromTo() {
		setName(ConstantUtil.CONTROL_BLOCK_FROM_TO);
	}
}
