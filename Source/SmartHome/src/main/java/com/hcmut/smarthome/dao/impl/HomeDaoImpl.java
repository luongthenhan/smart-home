package com.hcmut.smarthome.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.hcmut.smarthome.dao.IHomeDao;
import com.hcmut.smarthome.entity.HomeEntity;
import com.hcmut.smarthome.model.Home;

@Repository
public class HomeDaoImpl extends CommonDaoImpl<HomeEntity> implements IHomeDao{

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<HomeEntity> getAllHomes(int userId) {
		String query = "SELECT * FROM public.home WHERE home.user_id = :userId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query).addEntity(HomeEntity.class);
		sqlStatement.setParameter("userId", userId);
		
		return sqlStatement.list();
	}

	@Override
	@Transactional
	public Integer getHomeIdGivenDevice(int deviceId) {
		String query = "SELECT device.home_id FROM public.device WHERE device.id = :deviceId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query).addEntity(Integer.class);
		sqlStatement.setParameter("deviceId", deviceId);
		
		return (Integer) sqlStatement.uniqueResult();
	}

	@Override
	@Transactional
	public boolean updatePartialHome(int homeId, Home home){
		
		String hqlUpdateEnabled = "Update HomeEntity h set h.enabled = :enabled where h.id = :homeId";
		Query hqlUpdateEnabledQuery = getCurrentSession().createQuery(hqlUpdateEnabled);
		hqlUpdateEnabledQuery.setParameter("enabled", home.isEnabled());
		hqlUpdateEnabledQuery.setParameter("homeId", homeId);
		
		int updatedEntities = hqlUpdateEnabledQuery.executeUpdate();
		
		return updatedEntities > 0;
	}
	
	@Override
	@Transactional
	public boolean deleteHome(int userId , int homeId){
//		String query = "DELETE FROM public.home WHERE home.id = :homeId AND home.user_id = :userId ; ";
//		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query);
//		sqlStatement.setParameter("userId", userId);
//		sqlStatement.setParameter("homeId", homeId);
//		
//		return sqlStatement.executeUpdate() != 0;
		
		return delete(homeId);
	}
}
