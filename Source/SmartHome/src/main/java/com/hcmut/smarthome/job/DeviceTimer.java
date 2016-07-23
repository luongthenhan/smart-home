package com.hcmut.smarthome.job;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.service.IDeviceService;

@Service
public class DeviceTimer {

	@Autowired
	private IDeviceService deviceService;

	private static final Logger LOGGER = Logger.getLogger(DeviceTimer.class);

	private boolean isBulbLighting = false;

	public DeviceTimer() {
		super();
	}

	private void toggleBulb() {
		LOGGER.info("Prepare to toggle");
		if (!isBulbLighting) {
			LOGGER.info("Toggle Now");
			deviceService.toggleLED();
			isBulbLighting = !isBulbLighting;
		}
	}

	public void run(final Date fromTime, final Date toTime) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Date currTime = new Date();

				if (currTime.getHours() == fromTime.getHours()
						&& currTime.getMinutes() >= fromTime.getMinutes()) {
					LOGGER.debug("Case 1");
					toggleBulb();
				} else if (currTime.getHours() == toTime.getHours()
						&& currTime.getMinutes() <= toTime.getMinutes()) {
					LOGGER.debug("Case 2");
					toggleBulb();
				} else if (currTime.getHours() > fromTime.getHours()
						&& currTime.getHours() < toTime.getHours()) {
					LOGGER.debug("Case 3");
					toggleBulb();
				} else {
					LOGGER.debug("Case 4");
					isBulbLighting = false;
					toggleBulb();
				}
			}
		}, 0, 60000);
	}
}
