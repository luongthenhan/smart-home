package com.hcmut.smarthome.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.hcmut.smarthome.dao.IDeviceDao;
import com.hcmut.smarthome.entity.DeviceEntity;
import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.utils.NotFoundException;

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
	
	@Override
	@Transactional
	public boolean isEnabled(int deviceId) {

		Criteria crit = getCurrentSession().createCriteria(DeviceEntity.class);
		crit.add(Restrictions.eq("id", deviceId));
		crit.setProjection(Projections.property("enabled"));
		
		return (boolean) crit.uniqueResult();
	}

	@Override
	@Transactional
	public boolean isDeviceNameExisted(int homeId, String deviceName) {
		try {
			getDeviceIdGivenNameAndHomeId(homeId, deviceName);
			return true;
		} catch (NotFoundException e) {
			return false;
		}
	}
	
	@Override
	@Transactional
	public Integer getDeviceIdGivenNameAndHomeId(int homeId, String deviceName) throws NotFoundException {
		String query = "SELECT device.id FROM public.device WHERE device.home_id = :homeId AND device.name = :deviceName ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query.toString());
		sqlStatement.setParameter("homeId", homeId);
		sqlStatement.setParameter("deviceName", deviceName);
		
		Object result = sqlStatement.uniqueResult();
		if( result == null )
			throw new NotFoundException(String.format("Device with name %s is not found in home id %s",deviceName, homeId));
		return (Integer) result;
	}
}
