package com.hcmut.smarthome.device.receiver.impl;

import com.hcmut.smarthome.device.gpio.IGpioProvider;
import com.hcmut.smarthome.device.gpio.impl.GpioProviderImpl;
import com.hcmut.smarthome.device.receiver.IMotionSensor;
import com.hcmut.smarthome.model.Device;
import com.pi4j.io.gpio.GpioPinDigitalInput;

public class MotionSensorImpl implements IMotionSensor {

	GpioPinDigitalInput motionSensorPin;
	IGpioProvider gpioProvider;

	public MotionSensorImpl(Device device) {

		gpioProvider = new GpioProviderImpl();
		motionSensorPin = gpioProvider.getGpioInput(device);
	}

	@Override
	public boolean hasHuman() {
		return motionSensorPin.isHigh();
	}

}
