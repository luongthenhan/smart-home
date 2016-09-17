package com.hcmut.smarthome.device.receiver.impl;

import com.hcmut.smarthome.device.gpio.IGpioProvider;
import com.hcmut.smarthome.device.gpio.impl.GpioProviderImpl;
import com.hcmut.smarthome.device.receiver.IGasSensor;
import com.hcmut.smarthome.model.Device;
import com.pi4j.io.gpio.GpioPinDigitalInput;

public class GasSensorImpl implements IGasSensor {

	GpioPinDigitalInput gasSensorPin;
	IGpioProvider gpioProvider;

	public GasSensorImpl(Device device) {

		gpioProvider = new GpioProviderImpl();
		gasSensorPin = gpioProvider.getGpioInput(device);
	}

	@Override
	public boolean isDanger() {
		
		return gasSensorPin.isLow();
	}

}
