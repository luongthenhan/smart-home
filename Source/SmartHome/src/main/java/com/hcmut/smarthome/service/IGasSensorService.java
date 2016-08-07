package com.hcmut.smarthome.service;

public interface IGasSensorService {
	
	/**
	 * Start gas sensor
	 */
	public void start();
	
	/**
	 * Check the atmosphere has gas or not
	 * @return : if the atmosphere has gas, return true
	 * 				otherwise, return false
	 */
	public boolean isWarning();
}
