package com.hcmut.smarthome.scenario.model;

import java.time.LocalDateTime;

import com.hcmut.smarthome.utils.ConstantUtil;

public class ControlBlockFromTo extends ControlBlock<LocalDateTime>{
	public ControlBlockFromTo() {
		setName(ConstantUtil.CONTROL_BLOCK_FROM_TO);
	}
}
