package com.hcmut.smarthome.model;


public class Buzzer extends Device {
	
	private static final long serialVersionUID = 1L;
	private boolean isOn;
	
	public Buzzer(Device device) {
		super(device);
		this.isOn = false;
	}

	public boolean isOn() {
		return isOn;
	}

	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}
	
}
