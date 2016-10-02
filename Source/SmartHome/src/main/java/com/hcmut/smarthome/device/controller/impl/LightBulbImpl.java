package com.hcmut.smarthome.device.controller.impl;

import com.hcmut.smarthome.device.controller.ILightBulb;
import com.hcmut.smarthome.device.gpio.GpioProvider;
import com.hcmut.smarthome.model.LightBulb;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

public class LightBulbImpl implements ILightBulb {

	GpioPinDigitalOutput lightBulbPin;

	public LightBulbImpl(LightBulb lightBulb) {

		lightBulbPin = GpioProvider.getGpioOutput(lightBulb);
	}

	@Override
	public void turnOn() {
		lightBulbPin.setState(PinState.LOW);
	}

	@Override
	public void turnOff() {
		lightBulbPin.setState(PinState.HIGH);
	}

	@Override
	public void toggle() {
		lightBulbPin.toggle();
	}

	@Override
	public boolean isOn() {
		return lightBulbPin.isLow();
	}

}
