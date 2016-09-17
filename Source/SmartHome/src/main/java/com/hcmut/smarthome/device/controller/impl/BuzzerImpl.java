package com.hcmut.smarthome.device.controller.impl;

import com.hcmut.smarthome.device.controller.IBuzzer;
import com.hcmut.smarthome.device.gpio.IGpioProvider;
import com.hcmut.smarthome.device.gpio.impl.GpioProviderImpl;
import com.hcmut.smarthome.model.Device;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

public class BuzzerImpl implements IBuzzer {

	GpioPinDigitalOutput buzzerPin;
	IGpioProvider gpioProvider;
	
	public BuzzerImpl(Device device) {
		
		gpioProvider = new GpioProviderImpl();
		buzzerPin = gpioProvider.getGpioOutput(device);
	}

	@Override
	public void turnOn() {
		buzzerPin.setState(PinState.LOW);
	}

	@Override
	public void turnOff() {
		buzzerPin.setState(PinState.HIGH);
	}

}
