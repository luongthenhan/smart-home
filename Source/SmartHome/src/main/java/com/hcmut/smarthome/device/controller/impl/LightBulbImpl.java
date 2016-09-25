package com.hcmut.smarthome.device.controller.impl;

import com.hcmut.smarthome.device.controller.ILightBulb;
import com.hcmut.smarthome.device.gpio.IGpioProvider;
import com.hcmut.smarthome.device.gpio.impl.GpioProviderImpl;
import com.hcmut.smarthome.model.LightBulb;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

public class LightBulbImpl implements ILightBulb {

	GpioPinDigitalOutput lightBulbPin;
	IGpioProvider gpioProvider;

	public LightBulbImpl(LightBulb lightBulb) {

		gpioProvider = new GpioProviderImpl();
		lightBulbPin = gpioProvider.getGpioOutput(lightBulb);
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
