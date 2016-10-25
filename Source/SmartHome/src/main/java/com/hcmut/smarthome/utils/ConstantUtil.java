package com.hcmut.smarthome.utils;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import com.hcmut.smarthome.model.Device;

public class ConstantUtil {

	// CONTROL BLOCK NAME
	public static final String CONTROL_BLOCK_IF = "If";
	public static final String CONTROL_BLOCK_IF_ELSE = "IfElse";
	public static final String CONTROL_BLOCK_FROM_TO = "FromTo";
	public static final String CONTROL_BLOCK_REPEAT = "Repeat";

	// ACTION NAME

	// For output device
	public static final String TURN_ON = "TurnOn";
	public static final String TURN_OFF = "TurnOff";
	public static final String TOGGLE = "Toggle";

	// Camera
	public static final String TAKE_PICTURE = "TakePicture";

	// General
	public static final String SEND_EMAIL = "SendEmail";

	// For input device
	// Temperature sensor
	public static final String GET_TEMPERATURE = "GetTemperature";

	// Temperature sensor, gas sensor
	public static final boolean IS_DANGER = true;

	// Light sensor
	public static final boolean IS_NIGHT = true;

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
	public static final String TEMPERATURE_SENSOR = "Temperature Sensor";
	public static final String GAS_SENSOR = "Gas Sensor";
	public static final String MOTION_SENSOR = "Motion Sensor";
	public static final String LIGHT_SENSOR = "Light Sensor";

	public static final int SIZE_CONTROL_BLOCK_IF_ELSE = 4;
	public static final int SIZE_CONTROL_BLOCK_IF = 3;

	// Returned status and exception
	public static final Device CANNOT_CONVERT_ENTITY_TO_MODEL = null;
	public static final Exception DEVICE_BASE_CANNOT_CAST_TO_CORRECT_MODEL = new Exception(
			"Device base cannot cast to correct model");
	public static final Exception DEVICE_CANNOT_PERFORM_THIS_ACTION = new Exception(
			"Device cannot perform this action");
	public static final int ADD_UNSUCCESSFULLY = -1;
	public static final String BOTH_IF_ELSE_BLOCK_YIELD_SAME_ACTION = "Both block If and Else yield the same action";
	public static final String SCRIPT_CONFLICT_ITSELF = "Script conflict itself";

	// Hard code
	public static final int HOME_ID = 1;
	public static final int VALID_USER_ID = 2;
	public static final int CONDITION_CHECKING_PERIOD = 5000;
	public static final ZoneId DEFAULT_ZONE_ID = ZoneOffset.of("+07:00");

	// List gpio
	public static List<Integer> ALL_GPIO = Arrays.asList(3, 5, 7, 8, 10, 11,
			12, 13, 15, 16, 18, 19, 21, 22, 23, 24, 26, 27, 28, 29, 31, 32, 33,
			35, 36, 37, 38, 40);
	
	public static List<Integer> ALWAYS_AVAILABLE_GPIO = Arrays.asList(7);
	
	// sign up
	public static int ERROR_WHEN_ADD_USER = -1;
	public static int USERNAME_ALREADY_EXISTED = -2;
	public static int EMAIL_ALREADY_EXISTED = -3;
	
	// not login request
	public static List<String> NO_LOGIN_REQUESTS = Arrays.asList("signup", "activation");
}
