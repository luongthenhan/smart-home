package com.hcmut.smarthome.device.controller.impl;

import com.hcmut.smarthome.device.controller.IGasSensor;
import com.hcmut.smarthome.device.gpio.IGpioProvider;
import com.hcmut.smarthome.device.gpio.impl.GpioProviderImpl;
import com.hcmut.smarthome.model.GasSensor;
import com.pi4j.io.gpio.GpioPinDigitalInput;

public class GasSensorImpl implements IGasSensor {

	GpioPinDigitalInput gasSensorPin;
	IGpioProvider gpioProvider;

	public GasSensorImpl(GasSensor gasSensor) {

		gpioProvider = new GpioProviderImpl();
		gasSensorPin = gpioProvider.getGpioInputForActiveLowDevice(gasSensor);
	}

	@Override
	public boolean isDanger() {
		
		return gasSensorPin.isLow();
	}

}
