package com.hcmut.smarthome.device.controller.impl;

import com.hcmut.smarthome.device.controller.IBuzzer;
import com.hcmut.smarthome.device.gpio.IGpioProvider;
import com.hcmut.smarthome.device.gpio.impl.GpioProviderImpl;
import com.hcmut.smarthome.model.Buzzer;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

public class BuzzerImpl implements IBuzzer {

	GpioPinDigitalOutput buzzerPin;
	IGpioProvider gpioProvider;
	
	public BuzzerImpl(Buzzer buzzer) {
		
		gpioProvider = new GpioProviderImpl();
		buzzerPin = gpioProvider.getGpioOutput(buzzer);
	}

	@Override
	public void turnOn() {
		buzzerPin.setState(PinState.LOW);
	}

	@Override
	public void turnOff() {
		buzzerPin.setState(PinState.HIGH);
	}

	@Override
	public void toggle() {
		buzzerPin.toggle();
	}

	@Override
	public boolean isOn() {
		return buzzerPin.isLow();
	}

}
