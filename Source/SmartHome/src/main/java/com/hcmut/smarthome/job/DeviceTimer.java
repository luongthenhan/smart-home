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
	private boolean isTimerStarting = false;

	private Timer timer;

	public DeviceTimer() {
		super();
		timer = new Timer();
	}

	private void turnOnLightBulb() {
		LOGGER.debug("Turn on light bulb");
		deviceService.turnOnLightBulb();
		isBulbLighting = true;
	}
	
	private void turnOffLightBulb() {
		LOGGER.debug("Turn off light bulb");
		deviceService.turnOffLightBulb();
		isBulbLighting = false;
	}

	public void run(final Date fromTime, final Date toTime) {
		if (isTimerStarting) {
			LOGGER.debug("Timer has already scheduled");
			return;
		} else {
			isTimerStarting = true;
		}

		timer.schedule(new TimerTask() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				Date currTime = new Date();

				LOGGER.debug("Current: " + currTime.getHours() + ":"
						+ currTime.getMinutes());
				LOGGER.debug("From: " + fromTime.getHours() + ":"
						+ fromTime.getMinutes());
				LOGGER.debug("To: " + toTime.getHours() + ":"
						+ toTime.getMinutes());

				if (fromTime.compareTo(toTime) >= 1)
					return;

				int toHour = toTime.getHours();
				if (toTime.getHours() < fromTime.getHours())
					toHour += 24;

				if (fromTime.getHours() == currTime.getHours()) {
					if (fromTime.getMinutes() <= currTime.getMinutes()) {
						LOGGER.debug("Case 0");
						if (currTime.getHours() <= toHour
								&& currTime.getMinutes() < toTime.getMinutes()) {
							LOGGER.debug("Turn on light bulb");
							turnOnLightBulb();
						} else
							stopTheJob();
					}
				} else if (fromTime.getHours() < currTime.getHours()) {
					if (currTime.getHours() < toHour) {
						LOGGER.debug("Case 2");
						turnOnLightBulb();
					} else if (currTime.getHours() == toTime.getHours()) {
						if (currTime.getMinutes() < toTime.getMinutes()) {
							LOGGER.debug("Case 3");
							turnOnLightBulb();
						} else
							stopTheJob();
					}
				}

				LOGGER.debug("-----------------------------------");

			}

			private void stopTheJob() {
				LOGGER.debug("Stop the job");
				turnOffLightBulb();
				resetTimer();
				this.cancel();
			}

		}, 0, 10000);
	}

	private void resetTimer() {
		isTimerStarting = false;
	}
}
