package com.hcmut.smarthome.controller;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hcmut.smarthome.job.DeviceTimer;
import com.hcmut.smarthome.service.IDeviceService;

/**
 * Handles requests for the application home page.
 */
@Component("homeController")
public class HomeController {

	private static final Logger LOGGER = Logger.getLogger(HomeController.class);

	@Autowired
	private DeviceTimer deviceTimer;

	@Autowired
	private IDeviceService deviceService;

	private Date turnOnBulbFromTime;
	private Date turnOnBulbToTime;

	private boolean isLightOn;

	@PostConstruct
	public void setUp() {
		isLightOn = deviceService.isLightOn();
	}

	public void save() {
		LOGGER.debug("Click save");
		deviceTimer.run(turnOnBulbFromTime, turnOnBulbToTime);
	}

	public void toggleLED() {
		LOGGER.debug("Toggle LED");
		isLightOn = !isLightOn;
		deviceService.toggleLightBulb();
	}

	public Date getTurnOnBulbFromTime() {
		return turnOnBulbFromTime;
	}

	public void setTurnOnBulbFromTime(Date turnOnBulbFromTime) {
		this.turnOnBulbFromTime = turnOnBulbFromTime;
	}

	public Date getTurnOnBulbToTime() {
		return turnOnBulbToTime;
	}

	public void setTurnOnBulbToTime(Date turnOnBulbToTime) {
		this.turnOnBulbToTime = turnOnBulbToTime;
	}

	public boolean getIsLightOn() {
		return isLightOn;
	}

	public void setIsLightOn(boolean isLightOn) {
		this.isLightOn = isLightOn;
	}

}
