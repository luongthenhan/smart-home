package com.hcmut.smarthome.model;

import java.util.Calendar;

public class LightBulb extends Device {
	
	private Calendar fromTime;
	private Calendar toTime;
	
	public Calendar getFromTime() {
		return fromTime;
	}
	public void setFromTime(Calendar fromTime) {
		this.fromTime = fromTime;
	}
	public Calendar getToTime() {
		return toTime;
	}
	public void setToTime(Calendar toTime) {
		this.toTime = toTime;
	}
	

}
