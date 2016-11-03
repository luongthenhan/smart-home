package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.ADD_UNSUCCESSFULLY;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.converter.DeviceConverter;
import com.hcmut.smarthome.converter.ScriptConverter;
import com.hcmut.smarthome.dao.IDeviceDao;
import com.hcmut.smarthome.dao.IHomeDao;
import com.hcmut.smarthome.dao.IModeDao;
import com.hcmut.smarthome.dao.IScriptDao;
import com.hcmut.smarthome.entity.DeviceEntity;
import com.hcmut.smarthome.entity.DeviceTypeEntity;
import com.hcmut.smarthome.entity.HomeEntity;
import com.hcmut.smarthome.entity.ModeEntity;
import com.hcmut.smarthome.entity.ScriptEntity;
import com.hcmut.smarthome.entity.ScriptTypeEntity;
import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.model.Script;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.scenario.model.Scenario.ScenarioStatus;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.NotFoundException;
import com.hcmut.smarthome.utils.ScriptBuilder;

@Service
public class DeviceService implements IDeviceService {

	@Autowired
	private IScenarioService scenarioService;
	
	@Autowired
	private IDeviceDao deviceDao;
	
	@Autowired
	private IScriptDao scriptDao;
	
	@Autowired
	private IHomeDao homeDao;

	@Autowired
	private IModeDao modeDao;
	
	@PostConstruct
	private void init(){
		ScriptBuilder.setDeviceService(this);
//		List<Script> scripts = ScriptConverter.toListModel(scriptDao.getAll());
//		List<Mode> modes = ModeConverter.toListModel(modeDao.getAll());
//		
//		for (Script script : scripts) {
//			for (Mode mode : modes) {
//				if( mode == script.get )
//			}
//			Scenario scenario = scenarioService.sc
//		}
	}
	
	@Override
	public int addDevice(int homeId, int deviceTypeId, Device device) throws Exception{
		
		DeviceEntity deviceEntity = new DeviceEntity();
		initDeviceEntityBeforeSaveOrUpdate(homeId, deviceTypeId, device, deviceEntity);

		int deviceId = deviceDao.save(deviceEntity).intValue();
		if( deviceId > 0 ){
			return deviceId;
		}
		return ADD_UNSUCCESSFULLY;
	}
	
	// TODO: Now PUT use the implementation of PATCH
	@Override
	public boolean updateDevice(int homeId, int deviceId, int deviceTypeId, Device updatedDevice) throws Exception {
		return updatePartialDevice(homeId, deviceId, deviceTypeId, updatedDevice);
	}
	
	@Override
	public boolean updatePartialDevice(int homeId, int deviceId,
			int deviceTypeId, Device updatedDevice) throws Exception{
		DeviceEntity deviceEntity = deviceDao.getById(deviceId);
		
		if( deviceEntity == null )
			throw new NotFoundException(String.format("Device id %d not found", deviceId));
		
		boolean isDeviceStatusChanged = updatedDevice.isEnabled() != null
				&& updatedDevice.isEnabled() != deviceEntity.isEnabled();
		
		initDeviceEntityBeforeSaveOrUpdate(homeId, deviceTypeId, updatedDevice, deviceEntity);
		
		if( deviceDao.update(deviceEntity) ){
			updateScenarioStatusIfDeviceStatusChanged(deviceId, updatedDevice,
					isDeviceStatusChanged);
			return true;
		}
		return false;
	}

	private void updateScenarioStatusIfDeviceStatusChanged(int deviceId,
			Device updatedDevice, boolean isDeviceStatusChanged) {
		if( isDeviceStatusChanged ){
			if( updatedDevice.isEnabled() )
				scenarioService.updateAllScenarioStatusOfDevice(deviceId, ScenarioStatus.RUNNING);
			else scenarioService.updateAllScenarioStatusOfDevice(deviceId, ScenarioStatus.STOPPING);
		}
	}
	
	@Override
	public boolean deleteDevice(int homeId, int deviceId) throws NotFoundException {
		if( deviceDao.delete(deviceId) ){
			scenarioService.updateAllScenarioStatusOfDevice(deviceId, ScenarioStatus.STOP_FOREVER);
			return true;
		}
		throw new NotFoundException("Can't find device with id " + deviceId);
	}
	
	@Override
	public List<Device> getAllGivenHomeAndDeviceType(int homeId, int deviceTypeId){
		return DeviceConverter.toListModel(deviceDao.getAllGivenHomeAndDeviceType(homeId, deviceTypeId));
	}

	@Override
	public List<Device> getAllDevices(int homeId) {
		return DeviceConverter.toListModel(deviceDao.getAll(homeId));
	}
	
	
	@Override
	public Device getDevice(int homeId, int deviceId) {
		return DeviceConverter.toModel(deviceDao.getById(deviceId));
	}
	
	@Override
	public List<Script> getScripts(int modeId, int deviceId) {
		List<ScriptEntity> scriptEntities = scriptDao.getAllScripts(modeId, deviceId);
		return ScriptConverter.toListModel(scriptEntities);
	}
	
	@Override
	public boolean deleteScript( int deviceId, int scriptId) throws NotFoundException {
		if (scriptDao.deleteScript(scriptId)) {
			scenarioService.updateScenarioStatus(scriptId,ScenarioStatus.STOP_FOREVER);
			
			return true;
		}
		throw new NotFoundException("Can't find script with id " + scriptId);
	}

	@Override
	public int addScript(Script script, int deviceId , int modeId, int homeId) throws Exception  {
		
		Scenario scenario = scenarioService.scriptToScenario(homeId, script);
		boolean isValid = scenarioService.isValid(homeId, modeId, deviceId, script, scenario);
		
		if( isValid ){
			int scenarioId = saveScriptToDB(modeId, deviceId, script); 
			// TODO: We must decide when script is created , which status is default ? running or stopping ??
			runScenario(scenarioId, homeId, deviceId, modeId, scenario);
			return (scenarioId > 0 ? scenarioId : ADD_UNSUCCESSFULLY);
		}
		
		return ADD_UNSUCCESSFULLY;
	}
	
	// TODO: Now update a script involved so many queries -> need to improve performance
	@Override
	public boolean updateScript(int homeId, int modeId, int deviceId, int scriptId, Script updatedScript) throws Exception {
		return updatePartialScript(homeId, modeId, deviceId, scriptId, updatedScript);
	}


	@Override
	public boolean updatePartialScript(int homeId, int modeId, int deviceId, int scriptId, Script scriptToUpdate) throws Exception {
		ScriptEntity currentScriptEntity = scriptDao.getById(scriptId);
		
		if( currentScriptEntity == null )
			throw new NotFoundException(String.format("Script id %d not found", scriptId ));
		
		Scenario updatedScenario = scenarioService.scriptToScenario(homeId, scriptToUpdate);
		
		boolean isValid = scenarioService.isValid(homeId, modeId, deviceId, scriptToUpdate, updatedScenario);
		if( isValid )
			return handleWhenScriptIsValid(scriptId, scriptToUpdate, currentScriptEntity, updatedScenario);
		
		// Not found or not valid
		return false;
	}

	private boolean handleWhenScriptIsValid(int scriptId, Script scriptToUpdate,
			ScriptEntity currentScriptEntity, Scenario updatedScenario) throws Exception {
		boolean isScriptContentChanged = isScriptContentChanged(scriptToUpdate, currentScriptEntity);
		boolean isScriptStatusChanged = isScriptStatusChanged(scriptToUpdate, currentScriptEntity);
		
		boolean isUpdateSuccessfully = updateScriptToDB(scriptToUpdate,currentScriptEntity);
		if( isUpdateSuccessfully ){
			if( isScriptContentChanged )
				scenarioService.replaceOldScenarioWithNewOne(scriptId, updatedScenario);
			if ( isScriptStatusChanged )
				scenarioService.updateScenarioStatus(scriptId, scriptToUpdate.isEnabled()? ScenarioStatus.RUNNING: ScenarioStatus.STOPPING);
			return true;
		}
		return false;
	}


	
	private boolean isScriptStatusChanged(Script scriptToUpdate, ScriptEntity currentScriptEntity) {
		if (scriptToUpdate.isEnabled() != null
				&& !scriptToUpdate.isEnabled().equals(currentScriptEntity.isEnabled()))
			return true;
		return false;
	}
	
	private boolean isScriptContentChanged(Script scriptToUpdate, ScriptEntity currentScriptEntity){
		if( scriptToUpdate.getContent() != null 
			 && !scriptToUpdate.getContent().equals(currentScriptEntity.getContent()))
			return true;
		return false;
	}
	
	// TODO: Now don't allow to edit mode of script
	private boolean updateScriptToDB(Script scriptToUpdate, ScriptEntity currentScriptEntity){
		if( scriptToUpdate.getContent() != null )
			currentScriptEntity.setContent(scriptToUpdate.getContent());
		
		if( scriptToUpdate.getName() != null )
			currentScriptEntity.setName(scriptToUpdate.getName());
		
		if( scriptToUpdate.isEnabled() != null )
			currentScriptEntity.setEnabled(scriptToUpdate.isEnabled());
		
		if( scriptToUpdate.getType() != null && scriptToUpdate.getType().getId() > 0 ){
			ScriptTypeEntity scriptTypeEntity = new ScriptTypeEntity();
			scriptTypeEntity.setId(scriptToUpdate.getType().getId());
			currentScriptEntity.setScriptType(scriptTypeEntity);
		}
		
		if( scriptDao.update(currentScriptEntity) ){
			return true;
		}
		return false;
	}
	
	private void runScenario(int scenarioId, int homeId, int deviceId, int modeId, Scenario scenario) throws Exception{
		if( scenarioId > 0 ){
			scenario.setId(scenarioId);
			scenario.setHomeId(homeId);
			scenario.setDeviceId(deviceId);
			scenario.setModeId(modeId);
			scenarioService.runScenario(scenario);
		}
	}
	
	private int saveScriptToDB(int modeId, int deviceId, Script scriptToSave){
		ScriptEntity scriptEntity = new ScriptEntity();
		scriptEntity.setName(scriptToSave.getName());
		scriptEntity.setContent(scriptToSave.getContent());
		
		if( scriptToSave.isEnabled() == null )
			scriptEntity.setEnabled(true);
		else scriptEntity.setEnabled(scriptToSave.isEnabled());
		
		ModeEntity mode = new ModeEntity();
		mode.setId(modeId);
		scriptEntity.setMode(mode);
		
		ScriptTypeEntity scriptType = new ScriptTypeEntity();
		scriptType.setId(scriptToSave.getType().getId());
		scriptEntity.setScriptType(scriptType);
		
		DeviceEntity device = new DeviceEntity();
		device.setId(deviceId);
		scriptEntity.setDevice(device);
		
		int scriptId = scriptDao.save(scriptEntity);
		return scriptId;
	}
	
	@Override
	public Script getScript(int scriptId){
		return ScriptConverter.toModel(scriptDao.getById(scriptId));
	}
	
	@Override
	public List<Integer> getAllAvailableGpio(int homeId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void initDeviceEntityBeforeSaveOrUpdate(int homeId, int deviceTypeId, Device updatedDevice,
			DeviceEntity deviceEntity) {
		if( updatedDevice.getCode() != null )
			deviceEntity.setCode(updatedDevice.getCode());
		
		if( updatedDevice.getDescription() != null )
			deviceEntity.setDescription(updatedDevice.getDescription());
		
		if( updatedDevice.isEnabled() != null )
			deviceEntity.setEnabled(updatedDevice.isEnabled());
		else deviceEntity.setEnabled(true);
		
		// TODO: Not implement check valid GPIO yet, because client has checked it already
		if( updatedDevice.getGPIO() != null && updatedDevice.getGPIO() > 0 )
			deviceEntity.setGPIOPin(updatedDevice.getGPIO());

		if( updatedDevice.getGPIOType() != null )
			deviceEntity.setGPIOType(updatedDevice.getGPIOType());
		
		if( updatedDevice.getLocation() != null )
			deviceEntity.setLocation(updatedDevice.getLocation());
		
		if( updatedDevice.getName() != null 
				&& !isDeviceNameExisted(homeId, updatedDevice.getName())){
			deviceEntity.setName(updatedDevice.getName());
		}
		
		if( updatedDevice.getTimeout() != null )
			deviceEntity.setTimeout(updatedDevice.getTimeout());
		
		HomeEntity home = new HomeEntity();
		home.setId(homeId);
		
		DeviceTypeEntity deviceType = new DeviceTypeEntity();
		deviceType.setId(deviceTypeId);
		
		deviceEntity.setHome(home);
		deviceEntity.setDeviceType(deviceType);
	}

	@Override
	public boolean isDeviceEnabled(int deviceId){
		return deviceDao.isEnabled(deviceId);
	}
	
	@Override
	public Integer getDeviceIdGivenNameAndHomeId(int homeId, String deviceName) throws NotFoundException{
		return deviceDao.getDeviceIdGivenNameAndHomeId(homeId, deviceName);
	}
	
	private boolean isDeviceNameExisted(int homeId, String deviceName){
		return deviceDao.isDeviceNameExisted(homeId, deviceName);
	}
}
