package com.hcmut.smarthome.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.dao.IDeviceTypeDao;
import com.hcmut.smarthome.entity.ActionEntity;
import com.hcmut.smarthome.entity.DeviceTypeEntity;
import com.hcmut.smarthome.model.Action;
import com.hcmut.smarthome.model.DeviceType;
import com.hcmut.smarthome.service.IDeviceTypeService;
import com.hcmut.smarthome.utils.ActionConverter;
import com.hcmut.smarthome.utils.ConditionConverter;

@Service
public class DeviceTypeServiceImpl implements IDeviceTypeService{

	@Autowired
	IDeviceTypeDao deviceTypeDao;
	
	@Override
	public List<DeviceType> getAll(int userId, int homeId) {
		List<DeviceTypeEntity> deviceTypeEntities = deviceTypeDao.getAllGivenUserAndHome(userId, homeId);
		return toModel(deviceTypeEntities);
	}
	
	private List<DeviceType> toModel(List<DeviceTypeEntity> deviceTypeEntities){
		
		List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
		
		for(DeviceTypeEntity deviceTypeEntity: deviceTypeEntities){
			deviceTypes.add( toModel(deviceTypeEntity));
		}
		return deviceTypes;
	}
	
	private DeviceType toModel(DeviceTypeEntity deviceTypeEntity){
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
}
