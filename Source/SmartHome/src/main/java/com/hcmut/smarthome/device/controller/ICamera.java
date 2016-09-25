package com.hcmut.smarthome.device.controller;

import java.awt.image.BufferedImage;


public interface ICamera {
	
	public BufferedImage takeAPhoto();
	
	/**
	 * Set width in pixel, default is 650
	 * @param width
	 */
	public void setWidth(int width);
	
	/**
	 * Set height in pixels, default is 650
	 * @param height
	 */
	public void setHeight(int height);
	
	/**
	 * Set quality (0 -> 100), default is 75
	 * @param quality
	 */
	public void setQuality(int quality);
	
	/**
	 * Set timeout in milisecond
	 * @param timeout
	 */
	public void setTimeout(int timeout);
	
	/**
	 * Set sharpness (-100 -> 100), default is 0
	 * @param sharpness
	 */
	public void setSharpness(int sharpness);
	
	/**
	 * Set contrast (-100 -> 100), default is 0
	 * @param contrast
	 */
	public void setContrast(int contrast);
	
	/**
	 * Set brightness (0 -> 100), default is 50
	 * @param brightness
	 */
	public void setBrightness(int brightness);
	
}
