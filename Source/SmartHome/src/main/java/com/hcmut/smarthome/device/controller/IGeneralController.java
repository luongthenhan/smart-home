package com.hcmut.smarthome.device.controller;

import java.awt.image.BufferedImage;

import com.hcmut.smarthome.model.Device;

public interface IGeneralController {
	
	public void turnOn(Device deviceBase) throws Exception;
	
	public void turnOff(Device deviceBase) throws Exception;
	
	public void toggle(Device deviceBase) throws Exception;
	
	public BufferedImage takeAPhoto(Device deviceBase) throws Exception;
	
	public float getTemperature(Device deviceBase) throws Exception;
	
	public boolean isNight(Device deviceBase) throws Exception;
	
	public boolean isDanger(Device deviceBase) throws Exception;
	
	public boolean hasHuman(Device deviceBase) throws Exception;
	
	public boolean isOn(Device deviceBase) throws Exception;

}
