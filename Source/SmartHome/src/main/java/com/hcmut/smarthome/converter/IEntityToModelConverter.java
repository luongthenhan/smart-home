package com.hcmut.smarthome.converter;

import com.hcmut.smarthome.entity.Device;
import com.hcmut.smarthome.model.Buzzer;
import com.hcmut.smarthome.model.Camera;
import com.hcmut.smarthome.model.GasSensor;
import com.hcmut.smarthome.model.LightBulb;
import com.hcmut.smarthome.model.LightSensor;
import com.hcmut.smarthome.model.MotionSensor;
import com.hcmut.smarthome.model.TemperatureSensor;

public interface IEntityToModelConverter {
	
	public LightBulb convertToLightBulbModel(Device device);
	
	public Buzzer convertToBuzzerModel(Device device);
	
	public Camera convertToCameraModel(Device device);
	
	public LightSensor convertToLightSensorModel(Device device);
	
	public MotionSensor convertToMotionSensorModel(Device device);
	
	public TemperatureSensor convertToTemperatureSensorModel(Device device);
	
	public GasSensor convertToGasSensorModel(Device device);
	
}
