package com.hcmut.smarthome.device.receiver.impl;

import com.hcmut.smarthome.device.gpio.IGpioProvider;
import com.hcmut.smarthome.device.gpio.impl.GpioProviderImpl;
import com.hcmut.smarthome.device.receiver.ILightSensor;
import com.hcmut.smarthome.model.Device;
import com.pi4j.io.gpio.GpioPinDigitalInput;

public class LightSensorImpl implements ILightSensor {

	GpioPinDigitalInput lightSensorPin;
	IGpioProvider gpioProvider;

	public LightSensorImpl(Device device) {
		
		gpioProvider = new GpioProviderImpl();
		lightSensorPin = gpioProvider.getGpioInput(device);
	}

	@Override
	public boolean isNight() {
		return lightSensorPin.isHigh();
	}

}
