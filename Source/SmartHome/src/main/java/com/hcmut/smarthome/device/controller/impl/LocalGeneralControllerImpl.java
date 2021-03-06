//package com.hcmut.smarthome.device.controller.impl;
//
//import static com.hcmut.smarthome.utils.ConstantUtil.BUZZER;
//import static com.hcmut.smarthome.utils.ConstantUtil.CAMERA;
//import static com.hcmut.smarthome.utils.ConstantUtil.DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//import static com.hcmut.smarthome.utils.ConstantUtil.DEVICE_CANNOT_PERFORM_THIS_ACTION;
//import static com.hcmut.smarthome.utils.ConstantUtil.GAS_SENSOR;
//import static com.hcmut.smarthome.utils.ConstantUtil.LIGHT;
//import static com.hcmut.smarthome.utils.ConstantUtil.LIGHT_SENSOR;
//import static com.hcmut.smarthome.utils.ConstantUtil.MOTION_SENSOR;
//import static com.hcmut.smarthome.utils.ConstantUtil.TEMPERATURE_SENSOR;
//
//import java.awt.image.BufferedImage;
//
//import org.apache.log4j.Logger;
//import org.springframework.stereotype.Service;
//
//import com.hcmut.smarthome.device.controller.IGeneralController;
//import com.hcmut.smarthome.model.Buzzer;
//import com.hcmut.smarthome.model.Camera;
//import com.hcmut.smarthome.model.Device;
//import com.hcmut.smarthome.model.GasSensor;
//import com.hcmut.smarthome.model.LightBulb;
//import com.hcmut.smarthome.model.LightSensor;
//import com.hcmut.smarthome.model.MotionSensor;
//import com.hcmut.smarthome.model.TemperatureSensor;
//
//@Service(value="local")
//public class LocalGeneralControllerImpl implements IGeneralController {
//
//	private static final Logger LOGGER = Logger
//			.getLogger(LocalGeneralControllerImpl.class);
//
//	@Override
//	public void turnOn(Device deviceBase) throws Exception {
//		String deviceType = deviceBase.getDeviceType().getName();
//
//		switch (deviceType) {
//		case LIGHT:
//
//			turnOnLightBulb(deviceBase);
//			break;
//
//		case BUZZER:
//
//			turnOnBuzzer(deviceBase);
//			break;
//
//		default:
//			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
//		}
//
//	}
//
//	@Override
//	public void turnOff(Device deviceBase) throws Exception {
//
//		String deviceType = deviceBase.getDeviceType().getName();
//
//		switch (deviceType) {
//		case LIGHT:
//
//			turnOffLightBulb(deviceBase);
//			break;
//
//		case BUZZER:
//
//			turnOffBuzzer(deviceBase);
//			break;
//
//		default:
//			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
//		}
//	}
//
//	@Override
//	public void toggle(Device deviceBase) throws Exception {
//		
//		String deviceType = deviceBase.getDeviceType().getName();
//		
//		switch (deviceType) {
//		case LIGHT:
//			
//			toggleLightBulb(deviceBase);
//			break;
//
//		case BUZZER:
//
//			toggleBuzzer(deviceBase);
//			break;
//
//		default:
//			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
//		}
//	}
//	
//	@Override
//	public float getTemperature(Device deviceBase) throws Exception {
//		
//		String deviceType = deviceBase.getDeviceType().getName();
//		float temperature = 29;
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
//			LOGGER.debug(String.format("Get temperature from %s: %s",deviceBase.getName(),temperature));
//			return temperature;
//			
//			// ITemperatureSensor temperatureSensorController = new TemperatureSensorImpl(
//			//		temperatureSensor);
//			
//			//temperature = temperatureSensorController.getTemperature();
//
//
//		default:
//			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
//		}
//		
//
//	}
//
//	
//	@Override
//	public boolean isNight(Device deviceBase) throws Exception {
//
//		String deviceType = deviceBase.getDeviceType().getName();
//		boolean isNight = false;
//
//		switch (deviceType) {
//
//		case LIGHT_SENSOR:
//
//			LightSensor lightSensor = null;
//
//			try {
//				lightSensor = (LightSensor) deviceBase;
//				
//			} catch (ClassCastException e) {
//				LOGGER.error(e.getMessage());
//				throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//			}
//
//			LOGGER.debug("Is night: " + deviceBase.getName());
//			return isNight;
//			//ILightSensor lightSensorController = new LightSensorImpl(
//			//		lightSensor);
//			//isNight = lightSensorController.isNight();
//
//			//break;
//
//		default:
//			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
//		}
//	}
//
//	@Override
//	public boolean isDanger(Device deviceBase) throws Exception {
//
//		String deviceType = deviceBase.getDeviceType().getName();
//		boolean isDanger = false;
//
//		switch (deviceType) {
//
//		case GAS_SENSOR:
//
//			isDanger = isGasDanger(deviceBase);
//			break;
//
//		case TEMPERATURE_SENSOR:
//
//			isDanger = isTemperatureDanger(deviceBase);
//			break;
//
//		default:
//			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
//		}
//		
//		LOGGER.debug("Is danger: " + deviceBase.getName());
//
//		return isDanger;
//
//	}
//
//	@Override
//	public boolean hasHuman(Device deviceBase) throws Exception {
//
//		String deviceType = deviceBase.getDeviceType().getName();
//		boolean hasHuman = false;
//
//		switch (deviceType) {
//
//		case MOTION_SENSOR:
//
//			MotionSensor motionSensor = null;
//
//			try {
//				motionSensor = (MotionSensor) deviceBase;
//			} catch (ClassCastException e) {
//				LOGGER.error(e.getMessage());
//				throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//			}
//
//			//IMotionSensor motionSensorController = new MotionSensorImpl(
//			//		motionSensor);
//			//hasHuman = motionSensorController.hasHuman();
//			break;
//
//		default:
//			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
//		}
//		
//		LOGGER.debug("Has human: " + deviceBase.getName());
//
//		return hasHuman;
//	}
//
//	@Override
//	public boolean isOn(Device deviceBase) throws Exception {
//
//		String deviceType = deviceBase.getDeviceType().getName();
//		boolean isOn = false;
//
//		switch (deviceType) {
//
//		case LIGHT:
//
//			isOn = isLightBulbOn(deviceBase);
//			break;
//
//		case BUZZER:
//
//			isOn = isBuzzerOn(deviceBase);
//			break;
//
//		default:
//			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
//		}
//		
//		LOGGER.debug("Is on: " + deviceBase.getName());
//
//		return isOn;
//	}
//
//	@Override
//	public BufferedImage takeAPhoto(Device deviceBase) throws Exception {
//
//		String deviceType = deviceBase.getDeviceType().getName();
//		BufferedImage bufferedImage = null;
//
//		switch (deviceType) {
//
//		case CAMERA:
//
//			Camera camera = null;
//
//			try {
//				camera = (Camera) deviceBase;
//			} catch (ClassCastException e) {
//				LOGGER.error(e.getMessage());
//				throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//			}
//
//			//ICamera cameraController = new CameraImpl(camera);
//			//bufferedImage = cameraController.takeAPhoto();
//
//			break;
//
//		default:
//			throw DEVICE_CANNOT_PERFORM_THIS_ACTION;
//		}
//		
//		LOGGER.debug("Take a photo " + deviceBase.getName());
//
//		return bufferedImage;
//	}
//
//	private void turnOnBuzzer(Device deviceBase) throws Exception {
//
//		Buzzer buzzer = null;
//
//		try {
//			buzzer = (Buzzer) deviceBase;
//		} catch (ClassCastException e) {
//			LOGGER.error(e.getMessage());
//			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//		}
//
//		//IBuzzer buzzerController = new BuzzerImpl(buzzer);
//		//buzzerController.turnOn();
//		LOGGER.debug("Turn on buzzer " + deviceBase.getName());
//		
//	}
//
//	private void turnOnLightBulb(Device deviceBase) throws Exception {
//		LightBulb lightBulb = null;
//
//		try {
//			lightBulb = (LightBulb) deviceBase;
//		} catch (ClassCastException e) {
//			LOGGER.error(e.getMessage());
//			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//		}
//		//ILightBulb lightBulbController = new LightBulbImpl(lightBulb);
//		//lightBulbController.turnOn();
//		LOGGER.debug("Turn on light bulb " + deviceBase.getName());
//	}
//
//	private void turnOffBuzzer(Device deviceBase) throws Exception {
//
//		Buzzer buzzer = null;
//
//		try {
//			buzzer = (Buzzer) deviceBase;
//		} catch (ClassCastException e) {
//			LOGGER.error(e.getMessage());
//			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//		}
//
//		//IBuzzer buzzerController = new BuzzerImpl(buzzer);
//		//buzzerController.turnOff();
//		LOGGER.debug("Turn off buzzer " + deviceBase.getName());
//	}
//
//	private void turnOffLightBulb(Device deviceBase) throws Exception {
//
//		LightBulb lightBulb = null;
//
//		try {
//			lightBulb = (LightBulb) deviceBase;
//		} catch (ClassCastException e) {
//			LOGGER.error(e.getMessage());
//			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//		}
////
//		//ILightBulb lightBulbController = new LightBulbImpl(lightBulb);
//		//lightBulbController.turnOff();
//		LOGGER.debug("Turn off light bulb " + deviceBase.getName());
//	}
//
//	private void toggleBuzzer(Device deviceBase) throws Exception {
//
//		Buzzer buzzer = null;
//
//		try {
//			buzzer = (Buzzer) deviceBase;
//		} catch (ClassCastException e) {
//			LOGGER.error(e.getMessage());
//			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//		}
//		//IBuzzer buzzerController = new BuzzerImpl(buzzer);
//		//buzzerController.toggle();
//		LOGGER.debug("Toggle buzzer " + deviceBase.getName());
//	}
//
//	private void toggleLightBulb(Device deviceBase) throws Exception {
//
//		LightBulb lightBulb = null;
//
//		try {
//			lightBulb = (LightBulb) deviceBase;
//		} catch (ClassCastException e) {
//			LOGGER.error(e.getMessage());
//			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//		}
//
//		//ILightBulb lightBulbController = new LightBulbImpl(lightBulb);
//		//lightBulbController.toggle();
//		LOGGER.debug("Toggle light " + deviceBase.getName());
//	}
//
//	private boolean isTemperatureDanger(Device deviceBase) throws Exception {
//
//		boolean isDanger = false;
//		TemperatureSensor temperatureSensor = null;
//
//		try {
//			temperatureSensor = (TemperatureSensor) deviceBase;
//		} catch (ClassCastException e) {
//			LOGGER.error(e.getMessage());
//			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//		}
//
////		ITemperatureSensor temperatureSensorController = new TemperatureSensorImpl(
////				temperatureSensor);
////		isDanger = temperatureSensorController.isDanger();
//		LOGGER.debug("Is temperature danger: " + deviceBase.getName());
//		return isDanger;
//	}
//
//	private boolean isGasDanger(Device deviceBase) throws Exception {
//
//		boolean isDanger = false;
//		GasSensor gasSensor = null;
//
//		try {
//			gasSensor = (GasSensor) deviceBase;
//		} catch (ClassCastException e) {
//			LOGGER.error(e.getMessage());
//			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//		}
//
////		IGasSensor gasSensorController = new GasSensorImpl(gasSensor);
////		isDanger = gasSensorController.isDanger();
//		LOGGER.debug("Is gas danger: " + deviceBase.getName());
//		return isDanger;
//	}
//
//	private boolean isBuzzerOn(Device deviceBase) throws Exception {
//
//		boolean isOn = false;
//		Buzzer buzzer = null;
//
//		try {
//			buzzer = (Buzzer) deviceBase;
//		} catch (ClassCastException e) {
//			LOGGER.error(e.getMessage());
//			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//		}
//
////		IBuzzer buzzerController = new BuzzerImpl(buzzer);
////		isOn = buzzerController.isOn();
//		LOGGER.debug("Is buzzer on: " + deviceBase.getName());
//		return isOn;
//	}
//
//	private boolean isLightBulbOn(Device deviceBase) throws Exception {
//
//		boolean isOn = false;
//		LightBulb lightBulb = null;
//
//		try {
//			lightBulb = (LightBulb) deviceBase;
//		} catch (ClassCastException e) {
//			LOGGER.error(e.getMessage());
//			throw DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL;
//		}
//
////		ILightBulb lightBulbController = new LightBulbImpl(lightBulb);
////		isOn = lightBulbController.isOn();
//		LOGGER.debug("Is light bulb on: " + deviceBase.getName());
//		return isOn;
//	}
//}
