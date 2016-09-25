package com.hcmut.smarthome.model;

import com.hcmut.smarthome.entity.Device;

public class MotionSensor extends DeviceBase {
	
	private boolean hasHuman;
	
	public MotionSensor(Device device) {
		this.id = device.getId();
		this.name = device.getName();
		this.description = device.getDescription();
		this.location = device.getLocation();
		this.gpio = device.getGPIOinfo();
		this.status = device.getStatus();
		this.enabled = device.isEnabled();
		this.code = device.getCode();
		this.deviceType = device.getDeviceType().getTypeName();
		this.hasHuman = false;
	}

	public boolean isHasHuman() {
		return hasHuman;
	}

	public void setHasHuman(boolean hasHuman) {
		this.hasHuman = hasHuman;
	}
	
}
