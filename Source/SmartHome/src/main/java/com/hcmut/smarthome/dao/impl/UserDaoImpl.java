package com.hcmut.smarthome.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.hcmut.smarthome.dao.IUserDao;
import com.hcmut.smarthome.entity.UserEntity;

@Repository
public class UserDaoImpl extends CommonDaoImpl<UserEntity> implements IUserDao {

	@Override
	public UserEntity getByUsername(String username) {
		Criteria crit = getCurrentSession().createCriteria(UserEntity.class);
		crit.add(Restrictions.eq("usrName", username));
		
		return (UserEntity) crit.uniqueResult();
	}

}
