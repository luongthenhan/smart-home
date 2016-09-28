package com.hcmut.smarthome.device.controller.impl;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.converter.EntityToModelConverter;
import com.hcmut.smarthome.device.controller.IBuzzer;
import com.hcmut.smarthome.device.controller.ICamera;
import com.hcmut.smarthome.device.controller.IGasSensor;
import com.hcmut.smarthome.device.controller.IGeneralController;
import com.hcmut.smarthome.device.controller.ILightBulb;
import com.hcmut.smarthome.device.controller.ILightSensor;
import com.hcmut.smarthome.device.controller.IMotionSensor;
import com.hcmut.smarthome.device.controller.ITemperatureSensor;
import com.hcmut.smarthome.model.Buzzer;
import com.hcmut.smarthome.model.Camera;
import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.model.GasSensor;
import com.hcmut.smarthome.model.LightBulb;
import com.hcmut.smarthome.model.LightSensor;
import com.hcmut.smarthome.model.MotionSensor;
import com.hcmut.smarthome.model.TemperatureSensor;

import static com.hcmut.smarthome.utils.ConstantUtil.*;

@Service
public class GeneralControllerImpl implements IGeneralController {

	private static final Logger LOGGER = Logger
			.getLogger(GeneralControllerImpl.class);

	@Override
	public void turnOn(Device deviceBase) throws Exception {

		String deviceType = deviceBase.getDeviceType().getTypeName();

		switch (deviceType) {
		case LIGHT:

			turnOnLightBulb(deviceBase);
			break;

		case BUZZER:

			turnOnBuzzer(deviceBase);
			break;

		default:
			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
		}

	}

	@Override
	public void turnOff(Device deviceBase) throws Exception {

		String deviceType = deviceBase.getDeviceType().getTypeName();

		switch (deviceType) {
		case LIGHT:

			turnOffLightBulb(deviceBase);
			break;

		case BUZZER:

			turnOffBuzzer(deviceBase);
			break;

		default:
			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
		}
	}

	@Override
	public void toggle(Device deviceBase) throws Exception {
		
		String deviceType = deviceBase.getDeviceType().getTypeName();
		System.out.println("toggle function: " + deviceType);
		switch (deviceType) {
		case LIGHT:
			
			toggleLightBulb(deviceBase);
			break;

		case BUZZER:

			toggleBuzzer(deviceBase);
			break;

		default:
			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
		}
	}

	@Override
	public float getTemperature(Device deviceBase) throws Exception {
		return 35.5F;
//		String deviceType = deviceBase.getDeviceType().getTypeName();
//		float temperature = 0;
//
//		switch (deviceType) {
//		case TEMPERATURE_SENSOR:
//
//			TemperatureSensor temperatureSensor = null;
//
//			try {
//				temperatureSensor = (TemperatureSensor) deviceBase;
//			} catch (ClassCastException e) {
//				LOGGER.error(e.getMessage());
//				throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//			}
//
//			ITemperatureSensor temperatureSensorController = new TemperatureSensorImpl(
//					temperatureSensor);
//			temperature = temperatureSensorController.getTemperature();
//
//			break;
//
//		default:
//			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
//		}
//
//		return temperature;
	}

	@Override
	public boolean isNight(Device deviceBase) throws Exception {

		String deviceType = deviceBase.getDeviceType().getTypeName();
		boolean isNight = false;

		switch (deviceType) {

		case LIGHT_SENSOR:

			LightSensor lightSensor = null;

			try {
				lightSensor = (LightSensor) deviceBase;
			} catch (ClassCastException e) {
				LOGGER.error(e.getMessage());
				throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
			}

			ILightSensor lightSensorController = new LightSensorImpl(
					lightSensor);
			isNight = lightSensorController.isNight();

			break;

		default:
			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
		}

		return isNight;
	}

	@Override
	public boolean isDanger(Device deviceBase) throws Exception {

		String deviceType = deviceBase.getDeviceType().getTypeName();
		boolean isDanger = false;

		switch (deviceType) {

		case GAS_SENSOR:

			isDanger = isGasDanger(deviceBase);
			break;

		case TEMPERATURE_SENSOR:

			isDanger = isTemperatureDanger(deviceBase);
			break;

		default:
			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
		}

		return isDanger;

	}

	@Override
	public boolean hasHuman(Device deviceBase) throws Exception {

		String deviceType = deviceBase.getDeviceType().getTypeName();
		boolean hasHuman = false;

		switch (deviceType) {

		case MOTION_SENSOR:

			MotionSensor motionSensor = null;

			try {
				motionSensor = (MotionSensor) deviceBase;
			} catch (ClassCastException e) {
				LOGGER.error(e.getMessage());
				throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
			}

			IMotionSensor motionSensorController = new MotionSensorImpl(
					motionSensor);
			hasHuman = motionSensorController.hasHuman();

			break;

		default:
			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
		}

		return hasHuman;
	}

	@Override
	public boolean isOn(Device deviceBase) throws Exception {

		String deviceType = deviceBase.getDeviceType().getTypeName();
		boolean isOn = false;

		switch (deviceType) {

		case LIGHT:

			isOn = isLightBulbOn(deviceBase);
			break;

		case BUZZER:

			isOn = isBuzzerOn(deviceBase);
			break;

		default:
			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
		}

		return isOn;
	}

	@Override
	public BufferedImage takeAPhoto(Device deviceBase) throws Exception {

		String deviceType = deviceBase.getDeviceType().getTypeName();
		BufferedImage bufferedImage = null;

		switch (deviceType) {

		case CAMERA:

			Camera camera = null;

			try {
				camera = (Camera) deviceBase;
			} catch (ClassCastException e) {
				LOGGER.error(e.getMessage());
				throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
			}

			ICamera cameraController = new CameraImpl(camera);
			bufferedImage = cameraController.takeAPhoto();

			break;

		default:
			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
		}

		return bufferedImage;
	}

	private void turnOnBuzzer(Device deviceBase) throws Exception {

		Buzzer buzzer = null;

		try {
			buzzer = (Buzzer) deviceBase;
		} catch (ClassCastException e) {
			LOGGER.error(e.getMessage());
			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
		}

		IBuzzer buzzerController = new BuzzerImpl(buzzer);
		buzzerController.turnOn();
	}

	private void turnOnLightBulb(Device deviceBase) throws Exception {

		LightBulb lightBulb = null;

		try {
			lightBulb = (LightBulb) deviceBase;
		} catch (ClassCastException e) {
			LOGGER.error(e.getMessage());
			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
		}

		ILightBulb lightBulbController = new LightBulbImpl(lightBulb);
		lightBulbController.turnOn();
	}

	private void turnOffBuzzer(Device deviceBase) throws Exception {

		Buzzer buzzer = null;

		try {
			buzzer = (Buzzer) deviceBase;
		} catch (ClassCastException e) {
			LOGGER.error(e.getMessage());
			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
		}

		IBuzzer buzzerController = new BuzzerImpl(buzzer);
		buzzerController.turnOff();
	}

	private void turnOffLightBulb(Device deviceBase) throws Exception {

		LightBulb lightBulb = null;

		try {
			lightBulb = (LightBulb) deviceBase;
		} catch (ClassCastException e) {
			LOGGER.error(e.getMessage());
			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
		}

		ILightBulb lightBulbController = new LightBulbImpl(lightBulb);
		lightBulbController.turnOff();
	}

	private void toggleBuzzer(Device deviceBase) throws Exception {

		Buzzer buzzer = null;

		try {
			buzzer = (Buzzer) deviceBase;
		} catch (ClassCastException e) {
			LOGGER.error(e.getMessage());
			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
		}

		IBuzzer buzzerController = new BuzzerImpl(buzzer);
		buzzerController.toggle();
	}

	private void toggleLightBulb(Device deviceBase) throws Exception {

		LightBulb lightBulb = null;

		try {
			lightBulb = (LightBulb)(new EntityToModelConverter()).convertToModel(deviceBase);
		} catch (ClassCastException e) {
			LOGGER.error(e.getMessage());
			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
		}

		System.out.println("Light is activated");
		//ILightBulb lightBulbController = new LightBulbImpl(lightBulb);
		//lightBulbController.toggle();
	}

	private boolean isTemperatureDanger(Device deviceBase) throws Exception {

		boolean isDanger = false;
		TemperatureSensor temperatureSensor = null;

		try {
			temperatureSensor = (TemperatureSensor) deviceBase;
		} catch (ClassCastException e) {
			LOGGER.error(e.getMessage());
			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
		}

		ITemperatureSensor temperatureSensorController = new TemperatureSensorImpl(
				temperatureSensor);
		isDanger = temperatureSensorController.isDanger();

		return isDanger;
	}

	private boolean isGasDanger(Device deviceBase) throws Exception {

		boolean isDanger = false;
		GasSensor gasSensor = null;

		try {
			gasSensor = (GasSensor) deviceBase;
		} catch (ClassCastException e) {
			LOGGER.error(e.getMessage());
			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
		}

		IGasSensor gasSensorController = new GasSensorImpl(gasSensor);
		isDanger = gasSensorController.isDanger();

		return isDanger;
	}

	private boolean isBuzzerOn(Device deviceBase) throws Exception {

		boolean isOn = false;
		Buzzer buzzer = null;

		try {
			buzzer = (Buzzer) deviceBase;
		} catch (ClassCastException e) {
			LOGGER.error(e.getMessage());
			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
		}

		IBuzzer buzzerController = new BuzzerImpl(buzzer);
		isOn = buzzerController.isOn();

		return isOn;
	}

	private boolean isLightBulbOn(Device deviceBase) throws Exception {

		boolean isOn = false;
		LightBulb lightBulb = null;

		try {
			lightBulb = (LightBulb) deviceBase;
		} catch (ClassCastException e) {
			LOGGER.error(e.getMessage());
			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
		}

		ILightBulb lightBulbController = new LightBulbImpl(lightBulb);
		isOn = lightBulbController.isOn();

		return isOn;
	}
}
