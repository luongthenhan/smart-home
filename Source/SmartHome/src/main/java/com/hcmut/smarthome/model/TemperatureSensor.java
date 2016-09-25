package com.hcmut.smarthome.model;

import com.hcmut.smarthome.entity.Device;

public class TemperatureSensor extends DeviceBase {
	
	private float temperature;
	private boolean isDanger;
	
	public TemperatureSensor(Device device) {
		this.id = device.getId();
		this.name = device.getName();
		this.description = device.getDescription();
		this.location = device.getLocation();
		this.gpio = device.getGPIOinfo();
		this.status = device.getStatus();
		this.enabled = device.isEnabled();
		this.code = device.getCode();
		this.deviceType = device.getDeviceType().getTypeName();
		this.temperature = 0;
		this.isDanger = false;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public boolean isDanger() {
		return isDanger;
	}

	public void setDanger(boolean isDanger) {
		this.isDanger = isDanger;
	}
	
}
