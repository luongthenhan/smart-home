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
import com.hcmut.smarthome.service.IHomeService;

@Service
public class HomeService implements IHomeService{

	private static final String DEFAULT_MODE = "default";

	@Autowired
	private IHomeDao homeDao;
	
	@Autowired
	private IModeDao modeDao;
	
	@Override
	public int getHomeIdGivenMode(int modeId){
		return homeDao.getHomeIdGivenMode(modeId);
	}
	
	@Override
	public List<Home> getAllHomes(int userId) {
		return HomeConverter.toListModel(homeDao.getAllHomes(userId));
	}
	
	@Override
	public Home getHome(int userId, int homeId) {
		return HomeConverter.toModel(homeDao.getById(homeId));
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
	public boolean updatePartialHome(int userId, int homeId, Home home) {
		HomeEntity homeEntity = homeDao.getById(homeId);
		if( home.getAddress() != null )
			homeEntity.setAddress(home.getAddress());
		
		if( home.getDescription() != null )
			homeEntity.setDescription(home.getDescription());
		
		if( home.getName() != null )
			homeEntity.setName(home.getName());
		
		if( home.isEnabled() != homeEntity.isEnabled() )
			homeEntity.setEnabled(home.isEnabled());
		
		if( home.getCurrentMode() != null && home.getCurrentMode().getId() > 0 ){
			ModeEntity currentMode = new ModeEntity();
			currentMode.setId(home.getCurrentMode().getId());
			homeEntity.setCurrentMode(currentMode);
		}
		
		return homeDao.update(homeEntity);
	}
	
	@Override
	public boolean deleteHome(int userId, int homeId){
		return homeDao.deleteHome(userId, homeId);
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
			return modeDao.deleteMode(homeId, modeId);
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

}
