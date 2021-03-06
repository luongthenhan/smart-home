package com.hcmut.smarthome.dao;

import java.util.List;

import com.hcmut.smarthome.entity.HomeEntity;
import com.hcmut.smarthome.model.Home;

public interface IHomeDao extends ICommonDao<HomeEntity>{
	List<HomeEntity> getAllHomes(int userId);
	
	Integer getHomeIdGivenDevice(int deviceId);

	boolean deleteHome(int userId, int homeId);

	boolean updatePartialHome(int homeId, Home home);
	
	boolean updateEnabled(int homeId, boolean enabled);
	
	boolean isEnabled(int homeId);

	Integer getHomeIdGivenMode(int modeId);

	Integer getCurrentModeIdGivenHome(int homeId);
	
	HomeEntity getBy(int userId, int homeId);
}
