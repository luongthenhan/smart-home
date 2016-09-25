package com.hcmut.smarthome.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.dao.IDeviceDao;
import com.hcmut.smarthome.entity.DeviceEntity;
import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.utils.DeviceConverter;

@Service
public class DeviceService implements IDeviceService {
	private boolean isLightOn = true;
	private boolean isBuzzerBeep = true;
	private boolean isDayLight = true;
	
	@Autowired
	private IDeviceDao deviceDao;
	
	@Override
	public List<Device> getAllGivenHomeAndDeviceType(int homeId, int deviceTypeId){
		List<DeviceEntity> devices = deviceDao.getAllGivenHomeAndDeviceType(homeId, deviceTypeId);
		return DeviceConverter.toListModel(devices);
	}

	public void toggleLight(String deviceName){
		if( isLightOn )
			System.out.println("Turn " + deviceName + " on .....");
		else System.out.println("Turn " + deviceName + " off .....");
		isLightOn = !isLightOn;
	}
	
	public boolean isLightOn(String deviceName){
		System.out.println("Check " + deviceName + " is on...");
		return isLightOn;
	}
	
	public boolean isBuzzerBeep(String deviceName){
		System.out.println("Check " + deviceName + " is beep...");
		return isBuzzerBeep;
	}
	
	public void toggleBuzzer(String deviceName){
		if( isBuzzerBeep )
			System.out.println(deviceName + " beep .....");
		else System.out.println( deviceName + " is silent .....");
		isBuzzerBeep = !isBuzzerBeep;
		
	}
	
	public boolean isDayLight(String deviceName){
		System.out.println("Check " + deviceName + " is day light...");
		return isDayLight;
	}
	
	public float getLightIntensity(String deviceName){
		System.out.println("Get light intensity from " + deviceName);
		return 35.5F;
	}
	
	private float temp = 32.5F;
	public float getTemperature(String deviceName){
		temp = temp + 1 ;
		System.out.println("Get temperature from " + deviceName + " :" +temp);
		
		return temp;
	}
	
	public float getGasThreshold(String deviceName){
		System.out.println("Get gas threshold from " + deviceName);
		return 0.95F;
	}

	public void takeAShot(String deviceName) {
		System.out.println("Take a shot from " + deviceName);
	}
	
}
