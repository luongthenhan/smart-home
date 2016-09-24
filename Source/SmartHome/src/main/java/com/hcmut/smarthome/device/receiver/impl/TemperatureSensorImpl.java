package com.hcmut.smarthome.device.receiver.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.hcmut.smarthome.device.receiver.ITemperatureSensor;
import com.hcmut.smarthome.model.TemperatureSensor;

public class TemperatureSensorImpl implements ITemperatureSensor {
	
	private final static Logger LOGGER = Logger.getLogger(TemperatureSensorImpl.class);
	
	private final static float DANGER_TEMPERATURE = 50;
	
	private StringBuilder temperatureFilePath;
	
	public TemperatureSensorImpl(TemperatureSensor temperatureSensor) {
		
		temperatureFilePath = new StringBuilder();
		
		temperatureFilePath.append("/sys/bus/w1/devices/");
		temperatureFilePath.append(temperatureSensor.getCode());
		temperatureFilePath.append("/w1_slave");
		
	}

	@Override
	public float getTemperature() {
		
		BufferedReader temperatureBufferedReader = null;
		float temperature = 0;
		
		try {
			
			temperatureBufferedReader = new BufferedReader(new FileReader(temperatureFilePath.toString()));
			
			// Ignore the first line
			temperatureBufferedReader.readLine();
			
			String secondLine = temperatureBufferedReader.readLine();
			String[] secondLineWords = secondLine.split(" ");
			temperature = Float.parseFloat(secondLineWords[9].substring(2)) / 1000;
			
		}
		catch(IOException e) {
			LOGGER.error(e.getMessage());
		}
		catch(NullPointerException e) {
			LOGGER.error(e.getMessage());
		}
		catch(ArrayIndexOutOfBoundsException e) {
			LOGGER.error(e.getMessage());
		}
		finally {
			try {
				if(temperatureBufferedReader != null) {
					temperatureBufferedReader.close();
				}
			}
			catch(IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
		
		return temperature;
	}

	@Override
	public boolean isDanger() {
		
		if(getTemperature() >= DANGER_TEMPERATURE) {
			return true;
		}
		
		return false;
	}
}
