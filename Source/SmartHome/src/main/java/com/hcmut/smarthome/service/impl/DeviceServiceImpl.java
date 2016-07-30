package com.hcmut.smarthome.service.impl;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.service.IDeviceService;
import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.enums.AWB;
import com.hopding.jrpicam.enums.DRC;
import com.hopding.jrpicam.enums.Encoding;
import com.hopding.jrpicam.enums.Exposure;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;
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

	@PostConstruct
	public void setUpDevice() {
		LOGGER.debug("Initialize DeviceUtil");

		try {
		// create gpio controller
		gpioController = GpioFactory.getInstance();

		// provision gpio pin #02 as an output pin and turn on
		lightBulbPin = gpioController.provisionDigitalOutputPin(
				RaspiPin.GPIO_02, "led", PinState.HIGH);
		// set shutdown state for this pin
		lightBulbPin.setShutdownOptions(true, PinState.LOW);

		// set folder to take picture with picamera
		piCamera = new RPiCamera("/home/pi/hcmut/smarthome/picture");
		piCamera.turnOffPreview();
		piCamera.setAWB(AWB.AUTO);
		piCamera.setDRC(DRC.OFF);
		piCamera.setExposure(Exposure.AUTO);
		piCamera.setEncoding(Encoding.PNG);
		piCamera.setWidth(150);
		piCamera.setHeight(150);
		piCamera.setContrast(0);
		piCamera.setQuality(5);
		piCamera.setSharpness(0);
		piCamera.setTimeout(200);
		LOGGER.debug("Finish setup camera");
		
		} catch(FailedToRunRaspistillException e) {
			LOGGER.error(e.getMessage());
		}
	}

	@Override
	public void toggleLightBulb() {
		lightBulbPin.toggle();
		System.out.println("Led Toggle");
	}

	@Override
	public void turnOnLightBulb() {
		// If light bulb is off, turn it on
		if (lightBulbPin.isHigh()) {
			lightBulbPin.setState(PinState.LOW);
		}
	}

	@Override
	public void turnOffLightBulb() {
		// If light bulb is on, turn it off
		if (lightBulbPin.isLow()) {
			lightBulbPin.setState(PinState.HIGH);
		}
	}

	@Override
	public boolean isLightOn() {
		return lightBulbPin.isLow();
	}

	@Override
	public void capturePicture() {
		try {
			LOGGER.debug("Take picture in service");
			piCamera.takeStill("VinhGay.png");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage());
		}
	}
}
