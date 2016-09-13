package com.hcmut.smarthome.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.hcmut.smarthome.dao.ICommonDao;

@Transactional
public class CommonDaoImpl<K extends Object> implements ICommonDao<K> {

	private static final Logger LOGGER = Logger.getLogger(CommonDaoImpl.class);

	@Autowired
	protected SessionFactory sessionFactory;

	private Class<K> entityClass;

	@SuppressWarnings("unchecked")
	public CommonDaoImpl() {
		if (entityClass == null)
			entityClass = (Class<K>) ((ParameterizedType) getClass()
					.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public void merge(K entity) throws Exception {
		sessionFactory.getCurrentSession().merge(entity);
	}

	public void persist(K entity) throws Exception {
		sessionFactory.getCurrentSession().persist(entity);
	}

	@Override
	public final Session getCurrentSession() {
		return this.sessionFactory.getCurrentSession();
	}

	@Override
	public Long save(K item) {
		try {
			persist(item);
			return (Long) this.sessionFactory.getCurrentSession().save(item);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			return -1L;
		}
	}

	@Override
	public final boolean delete(K item) throws DataAccessException {
		getCurrentSession().delete(item);
		return true;

	}

	@SuppressWarnings("unchecked")
	@Override
	public final List<K> getAll() throws DataAccessException {
		return this.sessionFactory.getCurrentSession()
				.createCriteria(this.getEntityClass()).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final K getById(Serializable id) throws DataAccessException {
		return (K) getCurrentSession().get(getEntityClass(), id);
	}

	@Override
	public final boolean update(K entity) {
		try {
			merge(entity);
			getCurrentSession().saveOrUpdate(entity);
		} catch (Exception e) {
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public Class<K> getEntityClass() {
		if (entityClass == null) {
			entityClass = (Class<K>) ((ParameterizedType) getClass()
					.getGenericSuperclass()).getActualTypeArguments()[0];
		}
		return entityClass;
	}

	public void setEntityClass(Class<K> entityClass) {
		this.entityClass = entityClass;
	}
}
