package com.hcmut.smarthome.device.controller;

import java.awt.image.BufferedImage;

import com.hcmut.smarthome.model.DeviceBase;

public interface IGeneralController {
	
	public void turnOn(DeviceBase deviceBase) throws Exception;
	
	public void turnOff(DeviceBase deviceBase) throws Exception;
	
	public void toggle(DeviceBase deviceBase) throws Exception;
	
	public BufferedImage takeAPhoto(DeviceBase deviceBase) throws Exception;
	
	public float getTemperature(DeviceBase deviceBase) throws Exception;
	
	public boolean isNight(DeviceBase deviceBase) throws Exception;
	
	public boolean isDanger(DeviceBase deviceBase) throws Exception;
	
	public boolean hasHuman(DeviceBase deviceBase) throws Exception;
	
	public boolean isOn(DeviceBase deviceBase) throws Exception;

}
