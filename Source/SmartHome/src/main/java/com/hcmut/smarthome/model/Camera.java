package com.hcmut.smarthome.model;

import com.hcmut.smarthome.entity.DeviceEntity;


public class Camera extends Device {
	private static final long serialVersionUID = 1L;
	
	private final static int DEFAULT_WIDTH = 650;
	private final static int DEFAULT_HEIGHT = 650;
	private final static int DEFAULT_QUALITY = 75;
	private final static int DEFAULT_TIMEOUT = 1000;
	private final static int DEFAULT_SHARPNESS = 0;
	private final static int DEFAULT_CONTRAST = 0;
	private final static int DEFAULT_BRIGHTNESS = 50;
	
	private int width;
	private int height;
	private int quality;
	private int timeout;
	private int sharpness;
	private int contrast;
	private int brightness;
	
	public Camera(Device device) {
		super(device);
		
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;
		quality = DEFAULT_QUALITY;
		timeout = DEFAULT_TIMEOUT;
		sharpness = DEFAULT_SHARPNESS;
		contrast = DEFAULT_CONTRAST;
		brightness = DEFAULT_BRIGHTNESS;
		
	}
	
	public Camera(DeviceEntity device) {
		super(device);
		
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;
		quality = DEFAULT_QUALITY;
		timeout = DEFAULT_TIMEOUT;
		sharpness = DEFAULT_SHARPNESS;
		contrast = DEFAULT_CONTRAST;
		brightness = DEFAULT_BRIGHTNESS;
		
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public int getSharpness() {
		return sharpness;
	}

	public void setSharpness(int sharpness) {
		this.sharpness = sharpness;
	}

	public int getContrast() {
		return contrast;
	}

	public void setContrast(int contrast) {
		this.contrast = contrast;
	}

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
}
