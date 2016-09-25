package com.hcmut.smarthome.utils;

import com.hcmut.smarthome.model.DeviceBase;

public class ConstantUtil {

	// CONTROL BLOCK NAME
	public static final String CONTROL_BLOCK_IF = "If";
	public static final String CONTROL_BLOCK_IF_ELSE = "IfElse";
	public static final String CONTROL_BLOCK_FROM_TO = "FromTo";
	public static final String CONTROL_BLOCK_REPEAT = "Repeat";

	// ACTION NAME

	// For output device
	// Light bulb
	public static final String TURN_ON_LIGHT = "TurnOnLight";
	public static final String TURN_OFF_LIGHT = "TurnOffLight";
	public static final String TOGGLE_LIGHT = "ToggleLight";

	// Buzzer
	public static final String TURN_ON_BUZZER = "TurnOnBuzzer";
	public static final String TURN_OFF_BUZZER = "TurnOffBuzzer";
	public static final String TOGGLE_BUZZER = "ToggleBuzzer";

	// Camera
	public static final String TAKE_A_SHOT = "TakeAShot";

	// General
	public static final String SEND_EMAIL = "SendEmail";

	// For input device
	// Temperature sensor
	public static final String GET_TEMPERATURE = "GetTemperature";

	// Temperature sensor, gas sensor
	public static final String IS_DANGER = "IsDanger";

	// Light sensor
	public static final String IS_NIGHT = "IsNight";

	// Motion sensor
	public static final String HAS_HUMAN = "HasHuman";

	// LOGIC OPERATOR
	public static final String EQUAL = "=";
	public static final String NOT_EQUAL = "!=";
	public static final String GREATER_OR_EQUAL = ">=";
	public static final String LESS_OR_EQUAL = "<=";
	public static final String GREATER_THAN = ">";
	public static final String LESS_THAN = "<";

	// DEVICE PREFIX NAME
	// TODO: Get them in DB later
	// Light and LightSensor have same prefix .. take care
	public static final String LIGHT = "Light";
	public static final String BUZZER = "Buzzer";
	public static final String CAMERA = "Camera";
	public static final String TEMPERATURE_SENSOR = "TemperatureSensor";
	public static final String GAS_SENSOR = "GasSensor";
	public static final String MOTION_SENSOR = "MotionSensor";
	public static final String LIGHT_SENSOR = "LightSensor";

	// Returned status and exception
	public static final DeviceBase CANNOT_CONVERT_ENTITY_TO_MODEL = null;
	public static final Exception DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL = new Exception(
			"Device base cannot cast to correct model");
	public static final Exception DEVICE_CANNOT_PERFORM_THIS_ACTION = new Exception(
			"Device cannot perform this action");

}
