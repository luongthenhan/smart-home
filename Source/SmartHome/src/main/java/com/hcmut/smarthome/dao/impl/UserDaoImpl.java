package com.hcmut.smarthome.dao.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.EMAIL_ALREADY_EXISTS;
import static com.hcmut.smarthome.utils.ConstantUtil.USERNAME_ALREADY_EXISTS;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hcmut.smarthome.dao.IUserDao;
import com.hcmut.smarthome.entity.UserEntity;

@Repository
@Transactional
public class UserDaoImpl extends CommonDaoImpl<UserEntity> implements IUserDao {

	@Override
	public UserEntity getByUsername(String username) {
		Criteria crit = getCurrentSession().createCriteria(UserEntity.class);
		crit.add(Restrictions.eq("usrName", username));

		return (UserEntity) crit.uniqueResult();
	}

	@Override
	public int addUser(UserEntity userEntity) throws Exception {

		if(!isValidUsername(userEntity.getUsrName())) {
			return USERNAME_ALREADY_EXISTS;
		}
		
		if(!isValidEmail(userEntity.getEmail())) {
			return EMAIL_ALREADY_EXISTS;
		}
		
		int id = save(userEntity);
		return id;
	}

	private boolean isValidUsername(String username) {

		Criteria crit = getCurrentSession().createCriteria(UserEntity.class);
		crit.add(Restrictions.eq("usrName", username));

		if (crit.uniqueResult() != null) {
			return false;
		}

		return true;
	}

	private boolean isValidEmail(String email) {

		Criteria crit = getCurrentSession().createCriteria(UserEntity.class);
		crit.add(Restrictions.eq("email", email));

		if (crit.uniqueResult() != null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean activateUser(int userId) {
		
		String hql = "Update UserEntity u set u.activated = true where u.id = :userId";
		Query hqlQuery = getCurrentSession().createQuery(hql);
		hqlQuery.setParameter("userId", userId);
		int updatedEntities = hqlQuery.executeUpdate();
		
		return updatedEntities > 0;
	}

}
