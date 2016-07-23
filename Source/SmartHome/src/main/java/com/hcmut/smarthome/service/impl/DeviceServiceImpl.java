package com.hcmut.smarthome.service.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.service.IDeviceService;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

@Service
public class DeviceServiceImpl implements IDeviceService {

	private GpioController gpioController;
	private GpioPinDigitalOutput ledPin;

	private static final Logger LOGGER = Logger
			.getLogger(DeviceServiceImpl.class);

	private DeviceServiceImpl() {
		super();
		LOGGER.error("Initialize DeviceUtil");

		// create gpio controller
		gpioController = GpioFactory.getInstance();

		// provision gpio pin #02 as an output pin and turn on
		ledPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02,
				"led", PinState.HIGH);

		// set shutdown state for this pin
		ledPin.setShutdownOptions(true, PinState.LOW);
	}

	@Override
	public void toggleLED() {
		LOGGER.error("Toggle LED");
		ledPin.toggle();
	}
}
