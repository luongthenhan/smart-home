package com.hcmut.smarthome.service.impl;

import org.springframework.stereotype.Service;

@Service
public class DeviceService {
	private boolean isLightOn = true;
	private boolean isBuzzerBeep = true;
	private boolean isDayLight = true;
	
	public void toggleLight(String deviceName){
		if( isLightOn )
			System.out.println("Turn " + deviceName + " on .....");
		else System.out.println("Turn " + deviceName + " off .....");
		isLightOn = !isLightOn;
	}
	
	public boolean isLightOn(String deviceName){
		System.out.println("Check " + deviceName + " is on...");
		return isLightOn;
	}
	
	public boolean isBuzzerBeep(String deviceName){
		System.out.println("Check " + deviceName + " is beep...");
		return isBuzzerBeep;
	}
	
	public void toggleBuzzer(String deviceName){
		if( isBuzzerBeep )
			System.out.println(deviceName + " beep .....");
		else System.out.println( deviceName + " is silent .....");
		isBuzzerBeep = !isBuzzerBeep;
		
	}
	
	public boolean isDayLight(String deviceName){
		System.out.println("Check " + deviceName + " is day light...");
		return isDayLight;
	}
	
	public float getLightIntensity(String deviceName){
		System.out.println("Get light intensity from " + deviceName);
		return 35.5F;
	}
	
	public float getTemperature(String deviceName){
		System.out.println("Get temperature from " + deviceName);
		return 35.5F;
	}
	
	public float getGasThreshold(String deviceName){
		System.out.println("Get gas threshold from " + deviceName);
		return 0.95F;
	}

	public void takeAShot(String deviceName) {
		System.out.println("Take a shot from " + deviceName);
	}
	
}
