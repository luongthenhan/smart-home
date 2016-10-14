package com.hcmut.smarthome.service;

import java.util.List;

import com.hcmut.smarthome.model.Home;
import com.hcmut.smarthome.model.Mode;

public interface IHomeService {
	
	List<Home> getAllHomes(int userId);
	
	boolean updateEnabled(int homeId, boolean enabled);
	
	boolean isEnabled(int homeId);

	Home getHome(int userId, int homeId);
	
	int addHome(int userId, Home home);

	boolean updateHome(int userId, int homeId, Home home);
	
	boolean updatePartialHome(int userId, int homeId, Home home);

	boolean deleteHome(int userId, int homeId);

	int addMode(int homeId, Mode mode);

	boolean updateMode(int homeId, int modeId, Mode mode);

	boolean deleteMode(int homeId, int modeId);

	List<Mode> getAllModes(int homeId);

}
	