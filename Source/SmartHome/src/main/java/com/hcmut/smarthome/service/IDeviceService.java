package com.hcmut.smarthome.service;

public interface IDeviceService {

	/**
	 * Toggle light bulb
	 */
	public void toggleLightBulb();
	
	public void turnOnLightBulb();
	
	public void turnOffLightBulb();
	
	public boolean isLightOn();
	
}
