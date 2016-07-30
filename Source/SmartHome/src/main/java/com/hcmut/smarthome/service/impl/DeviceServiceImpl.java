package com.hcmut.smarthome.service.impl;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.service.IDeviceService;
import com.hopding.jrpicam.RPiCamera;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class DeviceServiceImpl implements IDeviceService {

	private static GpioController gpioController;
	private static GpioPinDigitalOutput lightBulbPin;
	private static RPiCamera piCamera;

	private static final Logger LOGGER = Logger
			.getLogger(DeviceServiceImpl.class);

	static {
		System.out.println("Static here");
		LOGGER.debug("Initialize DeviceUtil");

		// create gpio controller
		gpioController = GpioFactory.getInstance();

		// provision gpio pin #02 as an output pin and turn on
		lightBulbPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02,
				"led", PinState.HIGH);
		// set shutdown state for this pin
		lightBulbPin.setShutdownOptions(true, PinState.LOW);
		
		// set folder to take picture with picamera
		//piCamera = new RPiCamera("/home/pi/hcmut/smarthome/picture");
	}

	@Override
	public void toggleLightBulb() {
		lightBulbPin.toggle();
		System.out.println("Led Toggle");
	}

	@Override
	public void turnOnLightBulb() {
		// If light bulb is off, turn it on
		if(lightBulbPin.isHigh()) {
			lightBulbPin.setState(PinState.LOW);
		}
	}

	@Override
	public void turnOffLightBulb() {
		// If light bulb is on, turn it off
		if(lightBulbPin.isLow()) {
			lightBulbPin.setState(PinState.HIGH);			
		}
	}

	@Override
	public boolean isLightOn() {
		return lightBulbPin.isLow();
		
	}
}
