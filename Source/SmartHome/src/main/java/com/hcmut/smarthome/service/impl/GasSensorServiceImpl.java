package com.hcmut.smarthome.service.impl;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.service.IAlarmService;
import com.hcmut.smarthome.service.IGasSensorService;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

@Service
public class GasSensorServiceImpl implements IGasSensorService {
	
	private static final Logger LOGGER = Logger.getLogger(GasSensorServiceImpl.class);

	private GpioController gpioController;
	private GpioPinDigitalInput gasSensorPin;
	
	@Autowired
	IAlarmService alarmService;

	@PostConstruct
	public void setupGasSensor() {
		// Create GPIO controller
		gpioController = GpioFactory.getInstance();
		// provision gpio pin #03 as an output pin and turn on
		gasSensorPin = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_03,
				"gas_sensor", PinPullResistance.PULL_DOWN);
		// configure the pin shutdown behavior; these settings will be
		// automatically applied to the pin when the application is terminated
		gasSensorPin.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);
	}

	@Override
	public void start() {
		LOGGER.debug("Start gas sensor:" + gasSensorPin.getState());
		gasSensorPin.addListener(new GpioPinListenerDigital() {
			
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					GpioPinDigitalStateChangeEvent event) {
				LOGGER.debug("State changed: " + event.getState());
				if(isWarning(event)) {
					alarmService.ring();
				}
				else {
					alarmService.stopRinging();
				}
			}
		});
	}

	@Override
	public boolean isWarning() {
		return gasSensorPin.isLow();
	}
	
	private boolean isWarning(GpioPinDigitalStateChangeEvent event) {
		if(event.getState().isLow()) {
			return true;
		}
		
		return false;
	}

}
