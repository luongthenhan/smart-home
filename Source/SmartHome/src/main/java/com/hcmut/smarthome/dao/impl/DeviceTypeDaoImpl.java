package com.hcmut.smarthome.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.hcmut.smarthome.dao.IDeviceTypeDao;
import com.hcmut.smarthome.entity.DeviceTypeEntity;

@Repository
public class DeviceTypeDaoImpl extends CommonDaoImpl<DeviceTypeEntity> implements IDeviceTypeDao{

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<DeviceTypeEntity> getAllGivenUserAndHome(int userId, int homeId) {
		//String query = "SELECT DISTINCT * FROM public.condition, public.home, public.device_type, public.device WHERE home.id = :homeId AND device.home_id = home.id AND device_type.id = device.device_type_id AND device_type.id = condition.device_type_id;";
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT DISTINCT ").append("device_type.id,")
				.append("device_type.description, ")
				.append("device_type.main_action_id, ")
				.append("device_type.image_url, ")
				.append("device_type.name, ")
				.append("device_type.gpio_type ")
				.append("FROM ").append("device, ")
				.append("device_type ");
				/*.append("WHERE ")
				.append("device.home_id = :homeId AND ")
				.append("device.device_type_id = device_type.id ;")*/
		
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query.toString()).addEntity(DeviceTypeEntity.class);
		//sqlStatement.setParameter("homeId", homeId);
		
		return sqlStatement.list();
	}
	
}
