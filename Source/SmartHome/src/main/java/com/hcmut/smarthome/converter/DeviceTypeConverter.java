package com.hcmut.smarthome.converter;

import java.util.ArrayList;
import java.util.List;

import com.hcmut.smarthome.entity.ActionEntity;
import com.hcmut.smarthome.entity.DeviceTypeEntity;
import com.hcmut.smarthome.model.Action;
import com.hcmut.smarthome.model.BriefDeviceType;
import com.hcmut.smarthome.model.DeviceType;

public class DeviceTypeConverter {
	
	public static List<DeviceType> toListModel(List<DeviceTypeEntity> deviceTypeEntities){
		
		List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
		
		for(DeviceTypeEntity deviceTypeEntity: deviceTypeEntities){
			deviceTypes.add( toModel(deviceTypeEntity));
		}
		return deviceTypes;
	}
	
	public static DeviceType toModel(DeviceTypeEntity deviceTypeEntity){
		DeviceType deviceType = new DeviceType();
		deviceType.setId(deviceTypeEntity.getId());
		deviceType.setDescription(deviceTypeEntity.getDescription());
		deviceType.setImageURL(deviceTypeEntity.getImageURL());
		deviceType.setTypeName(deviceTypeEntity.getTypeName());
		
		Action mainAction = new Action();
		
		if (deviceTypeEntity.getMainAction() != null) {
			mainAction.setId(deviceTypeEntity.getMainAction());
			for (ActionEntity actionEntity : deviceTypeEntity.getActions()) {
				if (actionEntity.getId() == mainAction.getId()) {
					mainAction.setName(actionEntity.getName());
					mainAction.setScript(actionEntity.getScript());
					break;
				}
			}
		} else mainAction = null;
		
		deviceType.setMainAction(mainAction);
		
		deviceType.setActions(ActionConverter.toListModel(deviceTypeEntity.getActions()));
		deviceType.setConditions(ConditionConverter.toListModel(deviceTypeEntity.getConditions()));
		
		return deviceType;
	}

	public static BriefDeviceType toBriefDeviceType(DeviceTypeEntity deviceTypeEntity) {
		BriefDeviceType deviceType = new BriefDeviceType();
		deviceType.setId(deviceTypeEntity.getId());
		deviceType.setTypeName(deviceTypeEntity.getTypeName());
		
		return deviceType;
	}
}
