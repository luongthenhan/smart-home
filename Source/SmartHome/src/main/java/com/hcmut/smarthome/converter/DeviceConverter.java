package com.hcmut.smarthome.converter;

import static com.hcmut.smarthome.utils.ConstantUtil.BUZZER;
import static com.hcmut.smarthome.utils.ConstantUtil.CAMERA;
import static com.hcmut.smarthome.utils.ConstantUtil.CANNOT_CONVERT_ENTITY_TO_MODEL;
import static com.hcmut.smarthome.utils.ConstantUtil.GAS_SENSOR;
import static com.hcmut.smarthome.utils.ConstantUtil.LIGHT;
import static com.hcmut.smarthome.utils.ConstantUtil.LIGHT_SENSOR;
import static com.hcmut.smarthome.utils.ConstantUtil.MOTION_SENSOR;
import static com.hcmut.smarthome.utils.ConstantUtil.TEMPERATURE_SENSOR;

import java.util.ArrayList;
import java.util.List;

import com.hcmut.smarthome.entity.DeviceEntity;
import com.hcmut.smarthome.model.Buzzer;
import com.hcmut.smarthome.model.Camera;
import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.model.GasSensor;
import com.hcmut.smarthome.model.LightBulb;
import com.hcmut.smarthome.model.LightSensor;
import com.hcmut.smarthome.model.MotionSensor;
import com.hcmut.smarthome.model.TemperatureSensor;

public class DeviceConverter {

	public static List<Device> toListModel(List<DeviceEntity> deviceEntities){
		
		List<Device> devices = new ArrayList<Device>();
		
		for (DeviceEntity deviceEntity : deviceEntities) {
			devices.add(toModel(deviceEntity));
		}
		
		return devices;
	}
	
	public static Device toModel(DeviceEntity deviceEntity) {
		
		String deviceType = deviceEntity.getDeviceType().getTypeName();
		Device model;
		
		switch (deviceType) {
		case LIGHT:
			model = convertToLightBulbModel(deviceEntity);
			break;
		
		case BUZZER:
			model = convertToBuzzerModel(deviceEntity);
			break;
			
		case CAMERA:
			model = convertToCameraModel(deviceEntity);
			break;
			
		case GAS_SENSOR:
			model = convertToGasSensorModel(deviceEntity);
			break;
			
		case LIGHT_SENSOR:
			model = convertToLightSensorModel(deviceEntity);
			break;
			
		case TEMPERATURE_SENSOR:
			model = convertToTemperatureSensorModel(deviceEntity);
			break;
		
		case MOTION_SENSOR:
			model = convertToMotionSensorModel(deviceEntity);
			break;

		default:
			model = CANNOT_CONVERT_ENTITY_TO_MODEL;
			break;
		}
		
		return model;
		
	}
	
	private static LightBulb convertToLightBulbModel(DeviceEntity device) {
		return new LightBulb(device);
	}

	private static Buzzer convertToBuzzerModel(DeviceEntity device) {
		return new Buzzer(device);
	}

	private static Camera convertToCameraModel(DeviceEntity device) {
		return new Camera(device);
	}

	private static LightSensor convertToLightSensorModel(DeviceEntity device) {
		return new LightSensor(device);
	}

	private static MotionSensor convertToMotionSensorModel(DeviceEntity device) {
		return new MotionSensor(device);
	}

	private static TemperatureSensor convertToTemperatureSensorModel(DeviceEntity device) {
		return new TemperatureSensor(device);
	}

	private static GasSensor convertToGasSensorModel(DeviceEntity device) {
		return new GasSensor(device);
	}
	
}
