package com.hcmut.smarthome.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.hcmut.smarthome.service.IAlarmService;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

@Service
public class AlarmServiceImpl implements IAlarmService {

	private GpioController gpioController;
	private GpioPinDigitalOutput alarmPin;

	@PostConstruct
	public void setupAlarm() {
		// Create GPIO controller
		gpioController = GpioFactory.getInstance();
		// provision gpio pin #00 as an output pin and turn on
		alarmPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00,
				"alarm", PinState.HIGH);
		// configure the pin shutdown behavior; these settings will be
		// automatically applied to the pin when the application is terminated
		alarmPin.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);
	}

	@Override
	public void ring() {
		if (!isRinging()) {
			alarmPin.setState(PinState.LOW);
		}
	}

	@Override
	public void stopRinging() {
		if(isRinging()) {
			alarmPin.setState(PinState.HIGH);
		}
	}
	
	@Override
	public boolean isRinging() {
		return alarmPin.isLow();
	}

}
