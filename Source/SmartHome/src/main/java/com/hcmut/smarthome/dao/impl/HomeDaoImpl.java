package com.hcmut.smarthome.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.hcmut.smarthome.dao.IHomeDao;
import com.hcmut.smarthome.entity.HomeEntity;

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
	public boolean updateEnabled(int homeId, boolean enabled) {
		
		Session session = getCurrentSession();
		
		String hqlUpdateEnabled = "Update HomeEntity h set h.enabled = :enabled where h.id = :homeId";
		Query hqlUpdateEnabledQuery = session.createQuery(hqlUpdateEnabled);
		hqlUpdateEnabledQuery.setParameter("enabled", enabled);
		hqlUpdateEnabledQuery.setParameter("homeId", homeId);
		
		int updatedEntities = hqlUpdateEnabledQuery.executeUpdate();
		
		return updatedEntities > 0;
	}

	@Override
	public boolean isEnabled(int homeId) {
		
		Criteria crit = getCurrentSession().createCriteria(HomeEntity.class);
		crit.add(Restrictions.eq("id", homeId));
		crit.setProjection(Projections.property("enabled"));
		
		return (boolean) crit.uniqueResult();
	}

	
}
