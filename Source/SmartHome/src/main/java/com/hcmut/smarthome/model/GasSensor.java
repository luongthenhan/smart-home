package com.hcmut.smarthome.model;

import com.hcmut.smarthome.entity.Device;

public class GasSensor extends DeviceBase {
	
	private boolean isDanger;
	
	public GasSensor(Device device) {
		this.id = device.getId();
		this.name = device.getName();
		this.description = device.getDescription();
		this.location = device.getLocation();
		this.gpio = device.getGPIOinfo();
		this.status = device.getStatus();
		this.enabled = device.isEnabled();
		this.code = device.getCode();
		this.deviceType = device.getDeviceType().getTypeName();
		this.isDanger = false;
	}

	public boolean isDanger() {
		return isDanger;
	}

	public void setDanger(boolean isDanger) {
		this.isDanger = isDanger;
	}
	
}
