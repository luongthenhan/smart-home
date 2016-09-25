package com.hcmut.smarthome.model;


public class TemperatureSensor extends Device {
	
	private static final long serialVersionUID = 1L;
	private float temperature;
	private boolean isDanger;
	
	public TemperatureSensor(Device device) {
		super(device);
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
