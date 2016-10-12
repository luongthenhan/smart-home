package com.hcmut.smarthome.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.hcmut.smarthome.dao.IModeDao;
import com.hcmut.smarthome.entity.ModeEntity;

@Repository
public class ModeDaoImpl extends CommonDaoImpl<ModeEntity> implements IModeDao{

	@Override
	@Transactional
	public boolean deleteMode(int homeId, int modeId) {
		String query = "DELETE FROM public.mode WHERE mode.id = :modeId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query);
		sqlStatement.setParameter("modeId", modeId);
		
		return sqlStatement.executeUpdate() != 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<ModeEntity> getAllModes(int homeId) {
		String query = "SELECT * FROM public.mode WHERE mode.home_id = :homeId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query).addEntity(ModeEntity.class);
		sqlStatement.setParameter("homeId", homeId);
		
		return sqlStatement.list();
	}
	
}
