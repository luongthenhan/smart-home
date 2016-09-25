package com.hcmut.smarthome.converter;

import com.hcmut.smarthome.entity.Device;
import com.hcmut.smarthome.model.Buzzer;
import com.hcmut.smarthome.model.Camera;
import com.hcmut.smarthome.model.DeviceBase;
import com.hcmut.smarthome.model.GasSensor;
import com.hcmut.smarthome.model.LightBulb;
import com.hcmut.smarthome.model.LightSensor;
import com.hcmut.smarthome.model.MotionSensor;
import com.hcmut.smarthome.model.TemperatureSensor;
import static com.hcmut.smarthome.utils.ConstantUtil.*;

public class EntityToModelConverter {

	private LightBulb convertToLightBulbModel(Device device) {
		return new LightBulb(device);
	}

	private Buzzer convertToBuzzerModel(Device device) {
		return new Buzzer(device);
	}

	private Camera convertToCameraModel(Device device) {
		return new Camera(device);
	}

	private LightSensor convertToLightSensorModel(Device device) {
		return new LightSensor(device);
	}

	private MotionSensor convertToMotionSensorModel(Device device) {
		return new MotionSensor(device);
	}

	private TemperatureSensor convertToTemperatureSensorModel(Device device) {
		return new TemperatureSensor(device);
	}

	private GasSensor convertToGasSensorModel(Device device) {
		return new GasSensor(device);
	}
	
	public DeviceBase convertToModel(Device device) {
		
		String deviceType = device.getDeviceType().getTypeName();
		DeviceBase model;
		
		switch (deviceType) {
		case LIGHT:
			model = convertToLightBulbModel(device);
			break;
		
		case BUZZER:
			model = convertToBuzzerModel(device);
			break;
			
		case CAMERA:
			model = convertToCameraModel(device);
			break;
			
		case GAS_SENSOR:
			model = convertToGasSensorModel(device);
			break;
			
		case LIGHT_SENSOR:
			model = convertToLightSensorModel(device);
			break;
			
		case TEMPERATURE_SENSOR:
			model = convertToTemperatureSensorModel(device);
			break;
		
		case MOTION_SENSOR:
			model = convertToMotionSensorModel(device);
			break;

		default:
			model = CANNOT_CONVERT_ENTITY_TO_MODEL;
			break;
		}
		
		return model;
		
	}

}
