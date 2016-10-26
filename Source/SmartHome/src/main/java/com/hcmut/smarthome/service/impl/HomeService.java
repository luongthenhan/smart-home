package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.ADD_UNSUCCESSFULLY;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.converter.HomeConverter;
import com.hcmut.smarthome.converter.ModeConverter;
import com.hcmut.smarthome.dao.IHomeDao;
import com.hcmut.smarthome.dao.IModeDao;
import com.hcmut.smarthome.entity.HomeEntity;
import com.hcmut.smarthome.entity.ModeEntity;
import com.hcmut.smarthome.entity.UserEntity;
import com.hcmut.smarthome.model.Home;
import com.hcmut.smarthome.model.Mode;
import com.hcmut.smarthome.scenario.model.Scenario.ScenarioStatus;
import com.hcmut.smarthome.service.IHomeService;
import com.hcmut.smarthome.service.IScenarioService;

@Service
public class HomeService implements IHomeService{

	private static final String DEFAULT_MODE = "default";

	@Autowired
	private IHomeDao homeDao;
	
	@Autowired
	private IModeDao modeDao;
	
	@Autowired
	private IScenarioService scenarioService;
	
	@Override
	public int getHomeIdGivenMode(int modeId){
		return homeDao.getHomeIdGivenMode(modeId);
	}
	
	@Override
	public List<Home> getAllHomes(int userId) {
		List<HomeEntity> homes = homeDao.getAllHomes(userId);
		if( homes == null )
			return null;
		return HomeConverter.toListModel(homes);
	}
	
	@Override
	public Home getHome(int userId, int homeId)  {
		HomeEntity home = homeDao.getById(homeId);
		if( home == null )
			return null;
		return HomeConverter.toModel(home);
	}
	
	@Override
	public int addHome(int userId, Home home){
		HomeEntity homeEntity = new HomeEntity();
		homeEntity.setAddress(home.getAddress());
		homeEntity.setDescription(home.getDescription());
		homeEntity.setName(home.getName());
		homeEntity.setEnabled(home.isEnabled());
		UserEntity userEntity = new UserEntity();
		userEntity.setId(userId);
		homeEntity.setUser(userEntity);

		int homeId = homeDao.save(homeEntity).intValue(); 
		if( homeId > 0 ){
			Mode defaultMode = new Mode();
			defaultMode.setName(DEFAULT_MODE);
			int defaultModeId = addMode(homeId, defaultMode);
			
			ModeEntity defaultModeEntity = new ModeEntity();
			defaultModeEntity.setId(defaultModeId);
			homeEntity.setCurrentMode(defaultModeEntity);
			
			homeDao.update(homeEntity);
			
			return homeId;
		}
		
		return ADD_UNSUCCESSFULLY;
	}
	
	@Override
	public boolean updateHome(int userId, int homeId, Home home){
		return updatePartialHome(userId, homeId, home);
	}

	@Override
	public boolean updatePartialHome(int userId, int homeId, Home homeToUpdate) {
		HomeEntity homeEntity = homeDao.getById(homeId);
		if( homeToUpdate.getAddress() != null )
			homeEntity.setAddress(homeToUpdate.getAddress());
		
		if( homeToUpdate.getDescription() != null )
			homeEntity.setDescription(homeToUpdate.getDescription());
		
		if( homeToUpdate.getName() != null )
			homeEntity.setName(homeToUpdate.getName());
		
		if( homeToUpdate.isEnabled() != null 
				&& homeToUpdate.isEnabled() != homeEntity.isEnabled() )
			homeEntity.setEnabled(homeToUpdate.isEnabled());
		
		if( homeToUpdate.getCurrentMode() != null && homeToUpdate.getCurrentMode().getId() > 0 ){
			ModeEntity currentMode = new ModeEntity();
			currentMode.setId(homeToUpdate.getCurrentMode().getId());
			homeEntity.setCurrentMode(currentMode);
		}
		
		if( homeDao.update(homeEntity) ){
			if( homeToUpdate.isEnabled() != null
					&& homeToUpdate.isEnabled() != homeEntity.isEnabled() ){
				if( homeToUpdate.isEnabled() )
					scenarioService.updateAllScenarioStatusInHome(homeId, ScenarioStatus.RUNNING);
				else scenarioService.updateAllScenarioStatusInHome(homeId, ScenarioStatus.STOPPING);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean deleteHome(int userId, int homeId){
		boolean isDeletedSuccessfully = homeDao.deleteHome(userId, homeId);
		if( isDeletedSuccessfully ){
			scenarioService.updateAllScenarioStatusInHome(homeId,ScenarioStatus.STOP_FOREVER);
			return true;
		}
		return false;
	}
	
	@Override
	public int addMode(int homeId, Mode mode){
		ModeEntity modeEntity = new ModeEntity();
		modeEntity.setName(mode.getName());
		
		HomeEntity homeEntity = new HomeEntity();
		homeEntity.setId(homeId);
		modeEntity.setHome(homeEntity);
		
		return modeDao.save(modeEntity).intValue();
	}
	
	@Override
	public boolean updateMode(int homeId, int modeId, Mode mode){
		ModeEntity modeEntity = modeDao.getById(modeId);
		
		if( mode.getName() != null )
			modeEntity.setName(mode.getName());
		
		return modeDao.update(modeEntity);
	}
	
	@Override
	public boolean deleteMode(int homeId, int modeId){
		HomeEntity home = homeDao.getById(homeId);
		if( home.getCurrentMode() != null && home.getCurrentMode().getId() != modeId ){
			if( modeDao.deleteMode(homeId, modeId) ){
				scenarioService.updateAllScenarioStatusInMode(modeId,ScenarioStatus.STOP_FOREVER);
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Mode> getAllModes(int homeId) {
		return ModeConverter.toListModel(modeDao.getAllModes(homeId));
	}

	@Override
	public boolean updateEnabled(int homeId, boolean enabled) {
		return homeDao.updateEnabled(homeId, enabled);
	}

	@Override
	public boolean isEnabled(int homeId) {
		return homeDao.isEnabled(homeId);
	}

	@Override
	public int getCurrentModeIdGivenHome(int homeId){
		return homeDao.getCurrentModeIdGivenHome(homeId);
	}
}
