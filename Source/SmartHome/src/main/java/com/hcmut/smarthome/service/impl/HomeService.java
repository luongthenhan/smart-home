package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.ADD_UNSUCCESSFULLY;
import static com.hcmut.smarthome.utils.ConstantUtil.DEFAULT_MODE;
import static com.hcmut.smarthome.utils.ConstantUtil.HIDDEN_DEVICE;
import static com.hcmut.smarthome.utils.ConstantUtil.NO_GPIO;
import static com.hcmut.smarthome.utils.ConstantUtil.NO_GPIO_PIN;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.converter.HomeConverter;
import com.hcmut.smarthome.converter.ModeConverter;
import com.hcmut.smarthome.dao.IHomeDao;
import com.hcmut.smarthome.dao.IModeDao;
import com.hcmut.smarthome.entity.DeviceEntity;
import com.hcmut.smarthome.entity.DeviceTypeEntity;
import com.hcmut.smarthome.entity.HomeEntity;
import com.hcmut.smarthome.entity.ModeEntity;
import com.hcmut.smarthome.entity.UserEntity;
import com.hcmut.smarthome.model.Home;
import com.hcmut.smarthome.model.Mode;
import com.hcmut.smarthome.scenario.model.Scenario.ScenarioStatus;
import com.hcmut.smarthome.service.IHomeService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.NotFoundException;

@Service
public class HomeService implements IHomeService{

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
	public int addHome(int userId, Home home) throws Exception{
		HomeEntity homeEntity = new HomeEntity();
		homeEntity.setAddress(home.getAddress());
		homeEntity.setDescription(home.getDescription());
		homeEntity.setName(home.getName());
		if( home.isEnabled() != null )
			homeEntity.setEnabled(home.isEnabled());
		else homeEntity.setEnabled(true);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(userId);
		homeEntity.setUser(userEntity);

		int homeId = homeDao.save(homeEntity).intValue(); 
		if( homeId > 0 ){
			addDefaultModeToHome(homeEntity, homeId);
			addHiddenDeviceToHome(homeEntity, homeId);
			homeDao.update(homeEntity);
			return homeId;
		}
		
		return ADD_UNSUCCESSFULLY;
	}

	/**
	 * Hidden device used to manage all custom script of this home. 
	 * One home has only one hidden device
	 * Its device type is naming <!Custom Device Type!>
	 * @param homeEntity
	 * @param homeId
	 */
	private void addHiddenDeviceToHome(HomeEntity homeEntity, int homeId){
		DeviceTypeEntity customDeviceType = new DeviceTypeEntity();
		customDeviceType.setId(8);
		
		DeviceEntity hiddenDevice = new DeviceEntity();
		hiddenDevice.setName(HIDDEN_DEVICE);
		hiddenDevice.setHome(homeEntity);
		hiddenDevice.setEnabled(true);
		hiddenDevice.setGPIOType(NO_GPIO);
		hiddenDevice.setGPIOPin(NO_GPIO_PIN);
		hiddenDevice.setDeviceType(customDeviceType);
		
		homeEntity.setDevices(new ArrayList<>());
		homeEntity.getDevices().add(hiddenDevice);
	}
	
	private void addDefaultModeToHome(HomeEntity homeEntity, int homeId) {
		ModeEntity defaultModeEntity = new ModeEntity();
		defaultModeEntity.setName(DEFAULT_MODE);
		defaultModeEntity.setHome(homeEntity);
		
		homeEntity.setCurrentMode(defaultModeEntity);
		homeEntity.setModes(new ArrayList<>());
		homeEntity.getModes().add(defaultModeEntity);
	}
	
	@Override
	public boolean updateHome(int userId, int homeId, Home home) throws Exception{
		return updatePartialHome(userId, homeId, home);
	}

	@Override
	public boolean updatePartialHome(int userId, int homeId, Home homeToUpdate) throws Exception {
		HomeEntity homeEntity = homeDao.getById(homeId);
		
		if( homeEntity == null )
			throw new NotFoundException(String.format("Home id %d not found", homeId));
		
		boolean isHomeStatusChanged = homeToUpdate.isEnabled() != null
				&& homeToUpdate.isEnabled() != homeEntity.isEnabled();
		boolean isCurrentModeChanged = homeToUpdate.getCurrentMode() != null
				&& homeEntity.getCurrentMode() != null
				&& homeToUpdate.getCurrentMode().getId() != homeEntity.getCurrentMode().getId();
		int oldModeId = homeEntity.getCurrentMode().getId();
		
		boolean isUpdateSuccessfully = updateHomeToDB(homeToUpdate, homeEntity);
		
		if( isUpdateSuccessfully ){
			updateScenarioStatusIfHomeStatusChanged(homeId, homeToUpdate,
					isHomeStatusChanged);
			updateScenarioStatusIfCurrentModeChanged(oldModeId, homeToUpdate,
					isCurrentModeChanged);
				
			return true;
		}
		return false;
	}

	private void updateScenarioStatusIfCurrentModeChanged(int oldModeId, Home homeToUpdate, boolean isCurrentModeChanged) {
		if( isCurrentModeChanged ){
			int currModeId = homeToUpdate.getCurrentMode().getId();
			scenarioService.updateAllScenarioStatusOfMode(oldModeId, ScenarioStatus.STOPPING);
			scenarioService.updateAllScenarioStatusOfMode(currModeId, ScenarioStatus.RUNNING);
		}
	}

	private void updateScenarioStatusIfHomeStatusChanged(int homeId,
			Home homeToUpdate, boolean isHomeStatusChanged) {
		if( isHomeStatusChanged ){
			if( homeToUpdate.isEnabled() )
				scenarioService.updateAllScenarioStatusOfHome(homeId, ScenarioStatus.RUNNING);
			else scenarioService.updateAllScenarioStatusOfHome(homeId, ScenarioStatus.STOPPING);
		}
	}

	private boolean updateHomeToDB(Home homeToUpdate, HomeEntity homeEntity) throws Exception {
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
		
		return homeDao.update(homeEntity);
	}
	
	@Override
	public boolean deleteHome(int userId, int homeId){
		boolean isDeletedSuccessfully = homeDao.deleteHome(userId, homeId);
		if( isDeletedSuccessfully ){
			scenarioService.updateAllScenarioStatusOfHome(homeId,ScenarioStatus.STOP_FOREVER);
			return true;
		}
		return false;
	}
	
	@Override
	public int addMode(int homeId, Mode mode) throws Exception{
		ModeEntity modeEntity = new ModeEntity();
		modeEntity.setName(mode.getName());
		
		HomeEntity homeEntity = new HomeEntity();
		homeEntity.setId(homeId);
		modeEntity.setHome(homeEntity);
		
		return modeDao.save(modeEntity).intValue();
	}
	
	@Override
	public boolean updateMode(int homeId, int modeId, Mode mode) throws Exception{
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
				scenarioService.updateAllScenarioStatusOfMode(modeId,ScenarioStatus.STOP_FOREVER);
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
