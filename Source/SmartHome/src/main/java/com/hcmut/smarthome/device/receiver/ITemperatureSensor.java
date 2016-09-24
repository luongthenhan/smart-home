package com.hcmut.smarthome.device.receiver;


public interface ITemperatureSensor {
	
	public float getTemperature();
	public boolean isDanger();
}
