package com.hcmut.smarthome.device.controller.impl;

import com.hcmut.smarthome.device.controller.IGasSensor;
import com.hcmut.smarthome.device.gpio.GpioProvider;
import com.hcmut.smarthome.model.GasSensor;
import com.pi4j.io.gpio.GpioPinDigitalInput;

public class GasSensorImpl implements IGasSensor {

	GpioPinDigitalInput gasSensorPin;

	public GasSensorImpl(GasSensor gasSensor) {

		gasSensorPin = GpioProvider.getGpioInputForActiveLowDevice(gasSensor);
	}

	@Override
	public boolean isDanger() {
		
		return gasSensorPin.isLow();
	}

}
