package com.hcmut.smarthome.converter;

import com.hcmut.smarthome.entity.Device;
import com.hcmut.smarthome.model.Buzzer;
import com.hcmut.smarthome.model.Camera;
import com.hcmut.smarthome.model.GasSensor;
import com.hcmut.smarthome.model.LightBulb;
import com.hcmut.smarthome.model.LightSensor;
import com.hcmut.smarthome.model.MotionSensor;
import com.hcmut.smarthome.model.TemperatureSensor;

public class EntityToModelConverter {

	public LightBulb convertToLightBulbModel(Device device) {
		return new LightBulb(device);
	}

	public Buzzer convertToBuzzerModel(Device device) {
		return new Buzzer(device);
	}

	public Camera convertToCameraModel(Device device) {
		return new Camera(device);
	}

	public LightSensor convertToLightSensorModel(Device device) {
		return new LightSensor(device);
	}

	public MotionSensor convertToMotionSensorModel(Device device) {
		return new MotionSensor(device);
	}

	public TemperatureSensor convertToTemperatureSensorModel(Device device) {
		return new TemperatureSensor(device);
	}

	public GasSensor convertToGasSensorModel(Device device) {
		return new GasSensor(device);
	}

}
