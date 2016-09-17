package com.hcmut.smarthome.device.receiver.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.hcmut.smarthome.device.receiver.ITemperatureSensor;
import com.hcmut.smarthome.model.Device;

public class TemperatureSensorImpl implements ITemperatureSensor {
	
	private static final Logger LOGGER = Logger.getLogger(TemperatureSensorImpl.class);
	
	private StringBuilder temperatureFilePath;
	
	public TemperatureSensorImpl(Device device) {
		
		temperatureFilePath = new StringBuilder();
		
		temperatureFilePath.append("/sys/bus/w1/devices/");
		temperatureFilePath.append(device.getCode());
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
}
