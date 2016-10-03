package com.hcmut.smarthome.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
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

	
}
