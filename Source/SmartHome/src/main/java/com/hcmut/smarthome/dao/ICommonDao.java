package com.hcmut.smarthome.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface ICommonDao<T extends Object> {

	/**
	 * function for saving object into database
	 * 
	 * @param t
	 * @return id of object after be created
	 */
	Long save(T t);

	/**
	 * function for getting object from database base on object' id
	 * 
	 * @param id
	 * @return object belong to T class if success
	 */
	T getById(Serializable id);

	/**
	 * function for getting all object is apart of Class T from database
	 * 
	 * @return list object belong to T class if success
	 */
	List<T> getAll();

	/**
	 * function for updating object into database
	 * 
	 * @param t
	 * @return true if update success and false if otherwise
	 */
	boolean update(T t);

	/**
	 * function for updating object into database
	 * 
	 * @param t
	 * @return true if delete success otherwise return false
	 */
	boolean delete(T t);

	
	/**
	 * get current session
	 * @return
	 */
	Session getCurrentSession();
}