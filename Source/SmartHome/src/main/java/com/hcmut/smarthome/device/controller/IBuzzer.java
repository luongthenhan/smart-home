package com.hcmut.smarthome.device.controller;

public interface IBuzzer {
	
	public void turnOn();
	public void turnOff();
	public void toggle();
	public boolean isOn();

}
