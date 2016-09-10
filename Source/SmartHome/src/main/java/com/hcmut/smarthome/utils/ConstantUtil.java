package com.hcmut.smarthome.utils;

public class ConstantUtil {
	
	// CONTROL BLOCK NAME
	public static final String CONTROL_BLOCK_IF = "If";
	public static final String CONTROL_BLOCK_IF_ELSE = "IfElse";
	public static final String CONTROL_BLOCK_FROM_TO = "FromTo";
	public static final String CONTROL_BLOCK_REPEAT = "Repeat";

	// ACTION NAME
	public static final String TOGGLE_LIGHT = "ToggleLight";
	public static final String BUZZER_BEEP = "ToggleBuzzer";
	public static final String TAKE_A_SHOT = "TakeAShot";
	public static final String SEND_EMAIL = "SendEmail";
	
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
	public static final String LIGHT  = "Light";
	public static final String BUZZER  = "Buzzer";
	public static final String CAMERA  = "Camera";
	public static final String TEMPERATURE_SENSOR  = "TemperatureSensor";
	public static final String GAS_SENSOR  = "GasSensor";
	public static final String MOTION_SENSOR  = "MotionSensor";
	public static final String LIGHT_SENSOR  = "LightSensor";
}
