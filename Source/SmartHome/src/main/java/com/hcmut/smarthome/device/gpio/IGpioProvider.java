package com.hcmut.smarthome.device.gpio;

import com.hcmut.smarthome.model.Device;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public interface IGpioProvider {

	public GpioPinDigitalOutput getGpioOutput(Device device);
	
	public GpioPinDigitalInput getGpioInputForActiveLowDevice(Device device);
	
	public GpioPinDigitalInput getGpioInputForActiveHighDevice(Device device);
	
}
