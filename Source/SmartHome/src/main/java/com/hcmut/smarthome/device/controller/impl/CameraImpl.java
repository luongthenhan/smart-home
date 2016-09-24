package com.hcmut.smarthome.device.controller.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.hcmut.smarthome.device.controller.ICamera;
import com.hcmut.smarthome.model.Camera;
import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.enums.AWB;
import com.hopding.jrpicam.enums.DRC;
import com.hopding.jrpicam.enums.Encoding;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;

public class CameraImpl implements ICamera {
	
	private static final Logger LOGGER = Logger.getLogger(CameraImpl.class);
	
	private RPiCamera piCamera;
	
	private int width;
	private int height;
	private int quality;
	private int timeout;
	private int sharpness;
	private int contrast;
	private int brightness;

	public CameraImpl(Camera camera) {

		// set up pi camera
		try {
			piCamera = new RPiCamera();
			
		} catch (FailedToRunRaspistillException e) {
			LOGGER.error(e.getMessage());
		}
		
		width = camera.getWidth();
		height = camera.getHeight();
		quality = camera.getQuality();
		timeout = camera.getTimeout();
		sharpness = camera.getSharpness();
		contrast = camera.getContrast();
		brightness = camera.getBrightness();

	}
	
	@Override
	public BufferedImage takeAPhoto() {
		
		setupPiCamera();
		
		BufferedImage bufferedImage = null;
		
		//Take an image, buffer it
		try {
			
			bufferedImage = piCamera.takeBufferedStill(); //Take image and store in BufferedImage
			
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage());
		}
		
		return bufferedImage;
	}

	@Override
	public void setWidth(int width) {
		
		this.width = width;
	}

	@Override
	public void setHeight(int height) {

		this.height = height;
	}

	@Override
	public void setQuality(int quality) {

		this.quality = quality;
	}

	@Override
	public void setTimeout(int timeout) {

		this.timeout = timeout;
	}

	@Override
	public void setSharpness(int sharpness) {

		this.sharpness = sharpness;
	}

	@Override
	public void setContrast(int contrast) {

		this.contrast = contrast;
	}
	
	@Override
	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}
	
	private void setupPiCamera() {
		
		piCamera.setAWB(AWB.AUTO); //Change Automatic White Balance setting to automatic 
		piCamera.setDRC(DRC.OFF); //Turn off Dynamic Range Compression
		piCamera.setContrast(contrast); //Set maximum contrast
		piCamera.setSharpness(sharpness); //Set maximum sharpness
		piCamera.setBrightness(brightness);// Set brightness
		piCamera.setQuality(quality); //Set maximum quality
		piCamera.setTimeout(timeout); //Wait 1 second to take the image
		piCamera.setWidth(width);
		piCamera.setHeight(height);
		piCamera.turnOnPreview(); //Turn on image preview
		piCamera.setEncoding(Encoding.PNG); //Change encoding of images to PNG
		
	}

}
