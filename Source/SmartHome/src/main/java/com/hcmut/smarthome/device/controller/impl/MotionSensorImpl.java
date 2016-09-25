package com.hcmut.smarthome.device.controller.impl;

import com.hcmut.smarthome.device.controller.IMotionSensor;
import com.hcmut.smarthome.device.gpio.IGpioProvider;
import com.hcmut.smarthome.device.gpio.impl.GpioProviderImpl;
import com.hcmut.smarthome.model.MotionSensor;
import com.pi4j.io.gpio.GpioPinDigitalInput;

public class MotionSensorImpl implements IMotionSensor {

	GpioPinDigitalInput motionSensorPin;
	IGpioProvider gpioProvider;

	public MotionSensorImpl(MotionSensor motionSensor) {

		gpioProvider = new GpioProviderImpl();
		motionSensorPin = gpioProvider.getGpioInputForActiveHighDevice(motionSensor);
	}

	@Override
	public boolean hasHuman() {
		return motionSensorPin.isHigh();
	}

}
