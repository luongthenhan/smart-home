package com.hcmut.smarthome.device.receiver.impl;

import com.hcmut.smarthome.device.gpio.IGpioProvider;
import com.hcmut.smarthome.device.gpio.impl.GpioProviderImpl;
import com.hcmut.smarthome.device.receiver.IGasSensor;
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
