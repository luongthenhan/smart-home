package com.hcmut.smarthome.model;

import com.hcmut.smarthome.entity.Device;

public class LightSensor extends DeviceBase {
	
	public boolean isNight;
	
	public LightSensor(Device device) {
		this.id = device.getId();
		this.name = device.getName();
		this.description = device.getDescription();
		this.location = device.getLocation();
		this.gpio = device.getGPIOinfo();
		this.status = device.getStatus();
		this.enabled = device.isEnabled();
		this.code = device.getCode();
		this.isNight = false;
	}

	public boolean isNight() {
		return isNight;
	}

	public void setNight(boolean isNight) {
		this.isNight = isNight;
	}
	
}
