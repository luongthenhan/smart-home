package com.hcmut.smarthome.device.controller.impl;

import com.hcmut.smarthome.device.controller.ILightSensor;
import com.hcmut.smarthome.device.gpio.IGpioProvider;
import com.hcmut.smarthome.device.gpio.impl.GpioProviderImpl;
import com.hcmut.smarthome.model.LightSensor;
import com.pi4j.io.gpio.GpioPinDigitalInput;

public class LightSensorImpl implements ILightSensor {

	GpioPinDigitalInput lightSensorPin;
	IGpioProvider gpioProvider;

	public LightSensorImpl(LightSensor lightSensor) {
		
		gpioProvider = new GpioProviderImpl();
		lightSensorPin = gpioProvider.getGpioInputForActiveHighDevice(lightSensor);
	}

	@Override
	public boolean isNight() {
		return lightSensorPin.isHigh();
	}

}
