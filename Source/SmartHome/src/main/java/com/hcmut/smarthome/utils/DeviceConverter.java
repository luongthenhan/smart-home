package com.hcmut.smarthome.utils;

import java.util.ArrayList;
import java.util.List;

import com.hcmut.smarthome.entity.DeviceEntity;
import com.hcmut.smarthome.model.Device;

public class DeviceConverter {
	public static DeviceEntity toEntity(Device device){
		return null;
	}
	
	public static Device toModel(DeviceEntity deviceEntity){
		Device device = new Device();
		device.setId(deviceEntity.getId());
		device.setCode(deviceEntity.getCode());
		device.setDescription(deviceEntity.getDescription());
		device.setEnabled(deviceEntity.isEnabled());
		device.setGPIOPin(deviceEntity.getGPIOPin());
		device.setGPIOType(deviceEntity.getGPIOType());
		device.setLocation(deviceEntity.getLocation());
		device.setName(deviceEntity.getName());
		device.setHome(HomeConverter.toModel(deviceEntity.getHome()) );
		
		return device;
	}
	
	public static List<Device> toListModel(List<DeviceEntity> deviceEntities){
		
		List<Device> devices = new ArrayList<Device>();
		
		for (DeviceEntity deviceEntity : deviceEntities) {
			devices.add(toModel(deviceEntity));
		}
		
		return devices;
	}
}
