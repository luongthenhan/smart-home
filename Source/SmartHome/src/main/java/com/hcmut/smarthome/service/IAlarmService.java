package com.hcmut.smarthome.service;

public interface IAlarmService {
	
	/**
	 * Ring the alarm
	 */
	public void ring();
	
	/**
	 * Stop ringing the alarm
	 */
	public void stopRinging();
	
	/**
	 * Check the alarm is ringing or not
	 * @return : if the alarm is ringing, return true
	 * 				otherwise, return false
	 */
	public boolean isRinging();
	
}
