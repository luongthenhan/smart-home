package com.hcmut.smarthome.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.hcmut.smarthome.dao.IScriptDao;
import com.hcmut.smarthome.entity.ScriptEntity;
import com.hcmut.smarthome.utils.ConstantUtil;

@Repository
public class ScriptDaoImpl extends CommonDaoImpl<ScriptEntity> implements IScriptDao{
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<ScriptEntity> getAllScripts() {
		String query = "select * from script s, mode m where s.mode_id = m.id ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query).addEntity(ScriptEntity.class);
		
		return sqlStatement.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<ScriptEntity> getAllScripts(int homeId) {
		String query = "select * from script s, mode m where s.mode_id = m.id and m.home_id = :homeId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query).addEntity(ScriptEntity.class);
		sqlStatement.setParameter("homeId", homeId);
		
		return sqlStatement.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<ScriptEntity> getAllCustomScripts(int homeId) {
		String query = String.format( "select * from script s, mode m where s.script_type_id = %s and s.mode_id = m.id and m.home_id = :homeId ;", ConstantUtil.CUSTOM_SCRIPT_ID );
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query).addEntity(ScriptEntity.class);
		sqlStatement.setParameter("homeId", homeId);
		
		return sqlStatement.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<ScriptEntity> getAllScriptsGivenMode(int modeId) {
		String query = "SELECT * FROM public.script WHERE script.mode_id = :modeId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query).addEntity(ScriptEntity.class);
		sqlStatement.setParameter("modeId", modeId);
		
		return sqlStatement.list();
	}
	
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
	public boolean updateScriptStatusToEnable(int scriptId){
		String query = "update script set enabled = true where id = :scriptId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query);
		sqlStatement.setParameter("scriptId", scriptId);
		
		return sqlStatement.executeUpdate() > 0;
	}
	
	@Override
	@Transactional
	public boolean updateCustomScriptContent(int scriptId, String content){
		String query = "update script set content = :content where id = :scriptId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query);
		sqlStatement.setParameter("scriptId", scriptId);
		sqlStatement.setParameter("content", content);
		
		return sqlStatement.executeUpdate() > 0;
	}
	
//	@Override
//	@Transactional
//	public boolean updatePartialDevice(int deviceId, Device device) {
//		String hqlUpdateEnabled = "Update DeviceEntity d set d.enabled = :enabled where d.id = :deviceId";
//		Query hqlUpdateEnabledQuery = getCurrentSession().createQuery(hqlUpdateEnabled);
//		hqlUpdateEnabledQuery.setParameter("enabled", device.isEnabled());
//		hqlUpdateEnabledQuery.setParameter("deviceId", deviceId);
//		
//		int updatedEntities = hqlUpdateEnabledQuery.executeUpdate();
//		
//		return updatedEntities > 0;
//	}
	
	@Override
	@Transactional	
	public boolean updateScriptStatusToDisable(int scriptId){
		String query = "update script set enabled = false where id = :scriptId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query);
		sqlStatement.setParameter("scriptId", scriptId);
		
		return sqlStatement.executeUpdate() > 0;
	}
	
	@Override
	@Transactional
	public void updateScript(int scriptId, ScriptEntity updatedScript) {
//		updatedScript.setId(scriptId);
//		try {
//			getCurrentSession().saveOrUpdate(updatedScript);
//		} catch (Exception e) {
//			System.out.println("Error: updateScript: " + e.getMessage());
//		}
	}

	@Override
	@Transactional
	public boolean deleteScript(int scriptId) {
		String query = "DELETE FROM script WHERE id = :scriptId ;";
		SQLQuery sqlStatement = getCurrentSession().createSQLQuery(query);
		sqlStatement.setParameter("scriptId", scriptId);
		
		return sqlStatement.executeUpdate() != 0;
	}
}
