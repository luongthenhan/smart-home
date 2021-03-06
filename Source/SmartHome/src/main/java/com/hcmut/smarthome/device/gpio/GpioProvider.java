package com.hcmut.smarthome.device.gpio;

import java.util.HashMap;

import com.hcmut.smarthome.model.Device;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class GpioProvider {

	private static GpioController gpioController = GpioFactory.getInstance();

	private static HashMap<Integer, GpioPinDigitalOutput> outputPinMap = new HashMap<Integer, GpioPinDigitalOutput>();

	private static HashMap<Integer, GpioPinDigitalInput> inputPinMap = new HashMap<Integer, GpioPinDigitalInput>();

	public static GpioPinDigitalOutput getGpioOutput(Device device) {

		int devicePin = device.getGPIO();

		if (outputPinMap.containsKey(devicePin)) {
			return outputPinMap.get(devicePin);
		}

		GpioPinDigitalOutput outputPin;

		Pin p4jPin = convertRaspberryPinToP4jPin(devicePin);

		outputPin = gpioController.provisionDigitalOutputPin(p4jPin,
				device.getName(), PinState.HIGH);

		outputPin
				.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);

		outputPinMap.put(devicePin, outputPin);

		return outputPin;
	}

	public static GpioPinDigitalInput getGpioInputForActiveLowDevice(
			Device device) {

		int devicePin = device.getGPIO();

		if (inputPinMap.containsKey(devicePin)) {
			return inputPinMap.get(devicePin);
		}

		GpioPinDigitalInput inputPin;

		Pin p4jPin = convertRaspberryPinToP4jPin(devicePin);

		inputPin = gpioController.provisionDigitalInputPin(p4jPin,
				device.getName());

		inputPin.setPullResistance(PinPullResistance.PULL_UP);
		inputPin.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);

		inputPinMap.put(devicePin, inputPin);

		return inputPin;
	}

	public static GpioPinDigitalInput getGpioInputForActiveHighDevice(
			Device device) {
		
		int devicePin = device.getGPIO();

		if (inputPinMap.containsKey(devicePin)) {
			return inputPinMap.get(devicePin);
		}

		GpioPinDigitalInput inputPin;

		Pin p4jPin = convertRaspberryPinToP4jPin(devicePin);

		inputPin = gpioController.provisionDigitalInputPin(p4jPin,
				device.getName());

		inputPin.setPullResistance(PinPullResistance.PULL_DOWN);
		inputPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
		
		inputPinMap.put(devicePin, inputPin);

		return inputPin;
	}

	private static Pin convertRaspberryPinToP4jPin(int raspberryPin) {

		Pin p4jPin;

		switch (raspberryPin) {
		case 3:
			p4jPin = RaspiPin.GPIO_08;
			break;

		case 5:
			p4jPin = RaspiPin.GPIO_09;
			break;

		case 7:
			p4jPin = RaspiPin.GPIO_07;
			break;

		case 8:
			p4jPin = RaspiPin.GPIO_15;
			break;

		case 10:
			p4jPin = RaspiPin.GPIO_16;
			break;

		case 11:
			p4jPin = RaspiPin.GPIO_00;
			break;

		case 12:
			p4jPin = RaspiPin.GPIO_01;
			break;

		case 13:
			p4jPin = RaspiPin.GPIO_02;
			break;

		case 15:
			p4jPin = RaspiPin.GPIO_03;
			break;

		case 16:
			p4jPin = RaspiPin.GPIO_04;
			break;

		case 18:
			p4jPin = RaspiPin.GPIO_05;
			break;

		case 19:
			p4jPin = RaspiPin.GPIO_12;
			break;

		case 21:
			p4jPin = RaspiPin.GPIO_13;
			break;

		case 22:
			p4jPin = RaspiPin.GPIO_06;
			break;

		case 23:
			p4jPin = RaspiPin.GPIO_14;
			break;

		case 24:
			p4jPin = RaspiPin.GPIO_10;
			break;

		case 26:
			p4jPin = RaspiPin.GPIO_11;
			break;

		case 27:
			p4jPin = RaspiPin.GPIO_30;
			break;

		case 28:
			p4jPin = RaspiPin.GPIO_31;
			break;

		case 29:
			p4jPin = RaspiPin.GPIO_21;
			break;

		case 31:
			p4jPin = RaspiPin.GPIO_22;
			break;

		case 32:
			p4jPin = RaspiPin.GPIO_26;
			break;

		case 33:
			p4jPin = RaspiPin.GPIO_23;
			break;

		case 35:
			p4jPin = RaspiPin.GPIO_24;
			break;

		case 36:
			p4jPin = RaspiPin.GPIO_27;
			break;

		case 37:
			p4jPin = RaspiPin.GPIO_25;
			break;

		case 38:
			p4jPin = RaspiPin.GPIO_28;
			break;

		case 40:
			p4jPin = RaspiPin.GPIO_29;
			break;

		default:
			p4jPin = RaspiPin.GPIO_00;
			break;
		}

		return p4jPin;
	}

}
