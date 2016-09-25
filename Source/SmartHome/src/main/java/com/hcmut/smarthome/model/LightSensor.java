package com.hcmut.smarthome.model;


public class LightSensor extends Device {
	
	private static final long serialVersionUID = 1L;
	public boolean isNight;
	
	public LightSensor(Device device) {
		super(device);
		this.isNight = false;
	}

	public boolean isNight() {
		return isNight;
	}

	public void setNight(boolean isNight) {
		this.isNight = isNight;
	}
	
}
