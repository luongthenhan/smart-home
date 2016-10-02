package com.hcmut.smarthome.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.hcmut.smarthome.dao.IScriptDao;
import com.hcmut.smarthome.entity.ScriptEntity;

@Repository
public class ScriptDaoImpl extends CommonDaoImpl<ScriptEntity> implements IScriptDao{

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<ScriptEntity> getAllScripts(int modeId, int deviceId) {
		String query = "SELECT * FROM public.script WHERE script.mode_id = :modeId AND script.device_id = :deviceId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query).addEntity(ScriptEntity.class);
		sqlStatement.setParameter("modeId", modeId);
		sqlStatement.setParameter("deviceId", deviceId);
		
		return sqlStatement.list();
	}

	@Override
	@Transactional
	public void updateScript(int scriptId, ScriptEntity updatedScript) {
		updatedScript.setId(scriptId);
		try {
			getCurrentSession().saveOrUpdate(updatedScript);
		} catch (Exception e) {
			System.out.println("Error: updateScript: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public void deleteScript(int scriptId) {
		String query = "DELETE FROM script WHERE id = :scriptId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query);
		sqlStatement.setParameter("scriptId", scriptId);
		
		sqlStatement.executeUpdate();
	}

//	public List<DeviceTypeEntity> getAllGivenUserAndHome(int userId, int homeId) {
//		//String query = "SELECT DISTINCT * FROM public.condition, public.home, public.device_type, public.device WHERE home.id = :homeId AND device.home_id = home.id AND device_type.id = device.device_type_id AND device_type.id = condition.device_type_id;";
//		
//		StringBuilder query = new StringBuilder();
//		query.append("SELECT DISTINCT ").append("device_type.id,")
//				.append("device_type.description, ")
//				.append("device_type.main_action_id, ")
//				.append("device_type.image_url, ").append("device_type.name ")
//				.append("FROM ").append("device, ")
//				.append("device_type ")
//				.append("WHERE ")
//				.append("device.home_id = :homeId AND ")
//				.append("device.device_type_id = device_type.id ;");
//		
//		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query.toString()).addEntity(DeviceTypeEntity.class);
//		sqlStatement.setParameter("homeId", homeId);
//		
//		return sqlStatement.list();
//	}
	
}
