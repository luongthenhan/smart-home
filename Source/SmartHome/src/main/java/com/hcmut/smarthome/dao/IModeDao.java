package com.hcmut.smarthome.dao;

import java.util.List;

import com.hcmut.smarthome.entity.ModeEntity;

public interface IModeDao extends ICommonDao<ModeEntity>{
	boolean deleteMode(int homeId, int modeId);

	List<ModeEntity> getAllModes(int homeId);
}
