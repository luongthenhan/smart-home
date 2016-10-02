package com.hcmut.smarthome.device.controller.impl;

import com.hcmut.smarthome.device.controller.IBuzzer;
import com.hcmut.smarthome.device.gpio.GpioProvider;
import com.hcmut.smarthome.model.Buzzer;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

public class BuzzerImpl implements IBuzzer {

	GpioPinDigitalOutput buzzerPin;
	
	public BuzzerImpl(Buzzer buzzer) {
		
		buzzerPin = GpioProvider.getGpioOutput(buzzer);
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
