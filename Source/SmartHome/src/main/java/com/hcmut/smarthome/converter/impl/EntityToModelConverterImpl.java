package com.hcmut.smarthome.converter.impl;

import com.hcmut.smarthome.converter.IEntityToModelConverter;
import com.hcmut.smarthome.entity.Device;
import com.hcmut.smarthome.model.Buzzer;
import com.hcmut.smarthome.model.Camera;
import com.hcmut.smarthome.model.GasSensor;
import com.hcmut.smarthome.model.LightBulb;
import com.hcmut.smarthome.model.LightSensor;
import com.hcmut.smarthome.model.MotionSensor;
import com.hcmut.smarthome.model.TemperatureSensor;

public class EntityToModelConverterImpl implements IEntityToModelConverter {

	@Override
	public LightBulb convertToLightBulbModel(Device device) {
		return new LightBulb(device);
	}

	@Override
	public Buzzer convertToBuzzerModel(Device device) {
		return new Buzzer(device);
	}

	@Override
	public Camera convertToCameraModel(Device device) {
		return new Camera(device);
	}

	@Override
	public LightSensor convertToLightSensorModel(Device device) {
		return new LightSensor(device);
	}

	@Override
	public MotionSensor convertToMotionSensorModel(Device device) {
		return new MotionSensor(device);
	}

	@Override
	public TemperatureSensor convertToTemperatureSensorModel(Device device) {
		return new TemperatureSensor(device);
	}

	@Override
	public GasSensor convertToGasSensorModel(Device device) {
		return new GasSensor(device);
	}

}
