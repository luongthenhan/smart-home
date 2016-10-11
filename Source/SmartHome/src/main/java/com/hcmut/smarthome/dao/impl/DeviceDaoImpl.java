package com.hcmut.smarthome.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.hcmut.smarthome.dao.IDeviceDao;
import com.hcmut.smarthome.entity.DeviceEntity;
import com.hcmut.smarthome.model.Device;

@Repository
public class DeviceDaoImpl extends CommonDaoImpl<DeviceEntity> implements IDeviceDao{
	
	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public List<DeviceEntity> getAllGivenHomeAndDeviceType(int homeId , int deviceTypeId){
		String query = "SELECT * FROM public.device WHERE device.home_id = :homeId AND device.device_type_id = :deviceTypeId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query.toString()).addEntity(DeviceEntity.class);
		sqlStatement.setParameter("homeId", homeId);
		sqlStatement.setParameter("deviceTypeId", deviceTypeId);
		
		return sqlStatement.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<DeviceEntity> getAll(int homeId) {
		String query = "SELECT * FROM public.device WHERE device.home_id = :homeId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query.toString()).addEntity(DeviceEntity.class);
		sqlStatement.setParameter("homeId", homeId);
		
		return sqlStatement.list();
	}

	@Override
	@Transactional
	public boolean updatePartialDevice(int deviceId, Device device) {
		String hqlUpdateEnabled = "Update DeviceEntity d set d.enabled = :enabled where d.id = :deviceId";
		Query hqlUpdateEnabledQuery = getCurrentSession().createQuery(hqlUpdateEnabled);
		hqlUpdateEnabledQuery.setParameter("enabled", device.isEnabled());
		hqlUpdateEnabledQuery.setParameter("deviceId", deviceId);
		
		int updatedEntities = hqlUpdateEnabledQuery.executeUpdate();
		
		return updatedEntities > 0;
	}

	@Override
	@Transactional
	public boolean deleteDevice(int deviceId) {
//		String query = "DELETE FROM device WHERE id = :deviceId ;";
//		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query);
//		sqlStatement.setParameter("deviceId", deviceId);
//		
//		return sqlStatement.executeUpdate() != 0;
		return delete(deviceId);
	}
}
