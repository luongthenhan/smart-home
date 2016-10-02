package com.hcmut.smarthome.dao;

import java.util.List;

import com.hcmut.smarthome.entity.ScriptEntity;

public interface IScriptDao extends ICommonDao<ScriptEntity>{
	List<ScriptEntity> getAllScripts(int modeId, int deviceId);
	void updateScript(int scriptId, ScriptEntity updatedScript);
	void deleteScript(int scriptId);
}
