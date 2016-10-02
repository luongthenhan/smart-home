package com.hcmut.smarthome.device.controller.impl;

import com.hcmut.smarthome.device.controller.IMotionSensor;
import com.hcmut.smarthome.device.gpio.GpioProvider;
import com.hcmut.smarthome.model.MotionSensor;
import com.pi4j.io.gpio.GpioPinDigitalInput;

public class MotionSensorImpl implements IMotionSensor {

	GpioPinDigitalInput motionSensorPin;

	public MotionSensorImpl(MotionSensor motionSensor) {

		motionSensorPin = GpioProvider.getGpioInputForActiveHighDevice(motionSensor);
	}

	@Override
	public boolean hasHuman() {
		return motionSensorPin.isHigh();
	}

}
