package com.hcmut.smarthome.device.gpio;

import com.hcmut.smarthome.model.DeviceBase;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public interface IGpioProvider {

	public GpioPinDigitalOutput getGpioOutput(DeviceBase device);
	
	public GpioPinDigitalInput getGpioInputForActiveLowDevice(DeviceBase device);
	
	public GpioPinDigitalInput getGpioInputForActiveHighDevice(DeviceBase device);
	
}
