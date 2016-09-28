package com.hcmut.smarthome.model;

import com.hcmut.smarthome.entity.DeviceEntity;


public class MotionSensor extends Device {
	private static final long serialVersionUID = 1L;
	private boolean hasHuman;
	
	public MotionSensor(Device device) {
		super(device);
		this.hasHuman = false;
	}
	
	public MotionSensor(DeviceEntity device) {
		super(device);
		this.hasHuman = false;
	}

	public boolean isHasHuman() {
		return hasHuman;
	}

	public void setHasHuman(boolean hasHuman) {
		this.hasHuman = hasHuman;
	}
	
}
