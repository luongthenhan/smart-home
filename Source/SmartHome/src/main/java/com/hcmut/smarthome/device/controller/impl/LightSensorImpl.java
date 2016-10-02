package com.hcmut.smarthome.device.controller.impl;

import com.hcmut.smarthome.device.controller.ILightSensor;
import com.hcmut.smarthome.device.gpio.GpioProvider;
import com.hcmut.smarthome.model.LightSensor;
import com.pi4j.io.gpio.GpioPinDigitalInput;

public class LightSensorImpl implements ILightSensor {

	GpioPinDigitalInput lightSensorPin;

	public LightSensorImpl(LightSensor lightSensor) {
		
		lightSensorPin = GpioProvider.getGpioInputForActiveHighDevice(lightSensor);
	}

	@Override
	public boolean isNight() {
		return lightSensorPin.isHigh();
	}

}
