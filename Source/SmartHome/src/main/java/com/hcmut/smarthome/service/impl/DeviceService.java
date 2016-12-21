package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.ADD_UNSUCCESSFULLY;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.script.ScriptException;
import javax.transaction.NotSupportedException;

import org.json.simple.parser.ParseException;
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
import com.hcmut.smarthome.model.ScriptMoreDetail;
import com.hcmut.smarthome.scenario.model.Condition;
import com.hcmut.smarthome.scenario.model.ControlBlock;
import com.hcmut.smarthome.scenario.model.ControlBlockFromTo;
import com.hcmut.smarthome.scenario.model.ControlBlockIfElse;
import com.hcmut.smarthome.scenario.model.IBlock;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.scenario.model.Scenario.ScenarioStatus;
import com.hcmut.smarthome.scenario.model.SimpleAction;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.ConflictConditionException;
import com.hcmut.smarthome.utils.ConstantUtil;
import com.hcmut.smarthome.utils.NotFoundException;
import com.hcmut.smarthome.utils.ScriptBuilder;

@Service
public class DeviceService implements IDeviceService {

	private static final String DEVICE_ID_NOT_FOUND = "Device id %d not found";
	private static final String SCRIPT_ID_NOT_FOUND = "Script id %d not found";

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
			throw new NotFoundException(String.format(DEVICE_ID_NOT_FOUND, deviceId));
		
		boolean isDeviceStatusChanged = updatedDevice.isEnabled() != null
				&& updatedDevice.isEnabled() != deviceEntity.isEnabled();
		boolean isDeviceNameChanged = updatedDevice.getName() != null 
				&& !updatedDevice.getName().equals(deviceEntity.getName());
		String oldDeviceName = deviceEntity.getName();
		String newDeviceName = updatedDevice.getName();
		
		initDeviceEntityBeforeSaveOrUpdate(homeId, deviceTypeId, updatedDevice, deviceEntity);
		
		if( deviceDao.update(deviceEntity) ){
			if( isDeviceStatusChanged )
				updateScenarioStatus(homeId, deviceId, updatedDevice.isEnabled());
			if( isDeviceNameChanged )
				updateContentCustomScripts(homeId, oldDeviceName, newDeviceName);
			return true;
		}
		return false;
	}

	private void updateContentCustomScripts(int homeId, String oldDeviceName, String newDeviceName) throws Exception {
		List<Script> customScriptsInHome = ScriptConverter.toListModel(scriptDao.getAllCustomScripts(homeId)); 
		for (Script customScript : customScriptsInHome) {
			String contentCustomScript = customScript.getContent(); 
			if( contentCustomScript != null ){
				String findRegex = String.format("'%s'", oldDeviceName);
				String replacementRegex = String.format("'%s'", newDeviceName);
				String newContent = contentCustomScript.replaceAll(findRegex, replacementRegex);
				if ( !scriptDao.updateCustomScriptContent(customScript.getId(), newContent) )
					throw new Exception("Cannot update content custom script " + customScript.getId());
			}
		}
	}

	private void updateScenarioStatus(int homeId, int deviceId,
			boolean isUpdatedDeviceEnabled) throws ParseException, ScriptException, NotSupportedException, ConflictConditionException, Exception {
	
		List<Script> scripts = getAllScriptsGivenHome(homeId);
		for (Script script : scripts) {
			Scenario scenario = scenarioService.scriptToScenario(homeId, script);
			
			if( hasScenarioReferencedToDevice(deviceId, scenario.getBlocks()) ){
				if( isUpdatedDeviceEnabled ){
					scriptDao.updateScriptStatusToEnable(script.getId());
					scenarioService.updateScenarioStatus(script.getId(), ScenarioStatus.RUNNING);
				}
				else {
					scriptDao.updateScriptStatusToDisable(script.getId());
					scenarioService.updateScenarioStatus(script.getId(), ScenarioStatus.STOPPING);
				}
			}
		}
	}
	
	@Override
	public boolean deleteDevice(int homeId, int deviceId) throws Exception {
		scenarioService.updateAllScenarioStatusOfDevice(deviceId, ScenarioStatus.STOP_FOREVER);
		
		List<Script> scripts = getAllScriptsGivenHome(homeId);
		for (Script script : scripts) {
			Scenario scenario = scenarioService.scriptToScenario(homeId, script);
			if( hasScenarioReferencedToDevice(deviceId, scenario.getBlocks()) ){
				deleteScript(deviceId, script.getId());
				scenarioService.updateScenarioStatus(script.getId(), ScenarioStatus.STOP_FOREVER);
			}
		}
		
		if ( deviceDao.delete(deviceId))
			return true;	
		throw new NotFoundException("Can't find device with id " + deviceId);
	}
	
	private boolean hasScenarioReferencedToDevice(int deviceId, List<IBlock> blocks){
		if (blocks == null)
			return false;

		for (IBlock block : blocks) {
			if (block instanceof SimpleAction) {
				SimpleAction simpleAction = (SimpleAction) block;
				if( simpleAction.getDeviceId() == deviceId )
					return true;
			}
			else if ( block instanceof ControlBlockFromTo ){
				ControlBlockFromTo blockFromTo = (ControlBlockFromTo) block;
				return hasScenarioReferencedToDevice(deviceId, blockFromTo.getAction().getBlocks());
			}
			// Block If, IfElse
			else if (block instanceof ControlBlock) {
				ControlBlock<?> blockIf = (ControlBlock<?>) block;
				Condition<?> innerIfCondition = blockIf.getCondition();
				
				if( Integer.parseInt(innerIfCondition.getName()) == deviceId )
					return true;
				
				boolean needToDelete = hasScenarioReferencedToDevice(deviceId, blockIf.getAction().getBlocks());

				if (block instanceof ControlBlockIfElse) {
					ControlBlockIfElse blockIfElse = (ControlBlockIfElse) block;
					needToDelete = hasScenarioReferencedToDevice(deviceId, blockIfElse.getAction().getBlocks());
				}
				
				return needToDelete;
			}
		}
		return false;
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
		boolean isValid = scenarioService.isValid(homeId, modeId, script, scenario);
		
		if( isValid ){
			int scenarioId = saveScriptToDB(modeId, deviceId, script); 
			// TODO: We must decide when script is created , which status is default ? running or stopping ??
			scenarioService.runScenario(scenarioId, homeId, deviceId, modeId, scenario);
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
			throw new NotFoundException(String.format(SCRIPT_ID_NOT_FOUND, scriptId ));
		else scriptToUpdate.setId(currentScriptEntity.getId());
		
		Scenario updatedScenario = scenarioService.scriptToScenario(homeId, scriptToUpdate);
		
		boolean isValid = scenarioService.isValid(homeId, modeId, scriptToUpdate, updatedScenario);
		if( isValid ){
			if( checkHomeOrDevicesDisabled(homeId, deviceId, updatedScenario) )
				scriptToUpdate.setEnabled(false);
			return handleWhenScriptIsValid(scriptId, scriptToUpdate, currentScriptEntity, updatedScenario);
		}
		
		// Not found or not valid
		return false;
	}

	@Override
	public boolean checkHomeOrDevicesDisabled(int homeId, int deviceId, Scenario scenario) throws Exception{
		boolean isHomeOrDeviceDisabled = !homeDao.isEnabled(homeId) || !deviceDao.isEnabled(deviceId);
		if( isHomeOrDeviceDisabled )
			return true;
		
		// In case of hidden device, it always be enabled so that we need to check devices referenced by scenario
		boolean areAnyDevicesReferencedByScenarioDisabled = false; 
		Set<Integer> deviceIdsInScenario = scenarioService.getListDeviceIdInScenario(scenario);
		for (Integer deviceIdInScenario : deviceIdsInScenario) {
			areAnyDevicesReferencedByScenarioDisabled = !deviceDao.isEnabled(deviceIdInScenario);
			if( areAnyDevicesReferencedByScenarioDisabled )
				return true;
		}
		
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
	private boolean updateScriptToDB(Script scriptToUpdate, ScriptEntity currentScriptEntity) throws Exception{
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
	
	private int saveScriptToDB(int modeId, int deviceId, Script scriptToSave) throws Exception{
		ScriptEntity scriptEntity = new ScriptEntity();
		scriptEntity.setName(scriptToSave.getName());
		scriptEntity.setContent(scriptToSave.getContent());
		
		if( scriptToSave.isEnabled() != null )
			scriptEntity.setEnabled(scriptToSave.isEnabled());
		else if( deviceDao.isEnabled(deviceId) )
			scriptEntity.setEnabled(true);
		else scriptEntity.setEnabled(false);
		
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
	public List<Script> getScriptsGivenMode(int modeId){
		return ScriptConverter.toListModel(scriptDao.getAllScriptsGivenMode(modeId));
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
			DeviceEntity deviceEntity) throws Exception {
		if( updatedDevice.getCode() != null )
			deviceEntity.setCode(updatedDevice.getCode());
		
		if( updatedDevice.getDescription() != null )
			deviceEntity.setDescription(updatedDevice.getDescription());
		
		if( updatedDevice.isEnabled() != null )
			deviceEntity.setEnabled(updatedDevice.isEnabled());
		else if( homeDao.isEnabled(homeId) ) 
			deviceEntity.setEnabled(true);
		else deviceEntity.setEnabled(false);
		
		// TODO: Not implement check valid GPIO yet, because client has checked it already
		if( updatedDevice.getGPIO() != null && updatedDevice.getGPIO() > 0 )
			deviceEntity.setGPIOPin(updatedDevice.getGPIO());

		if( updatedDevice.getGPIOType() != null )
			deviceEntity.setGPIOType(updatedDevice.getGPIOType());
		
		if( updatedDevice.getLocation() != null )
			deviceEntity.setLocation(updatedDevice.getLocation());
		
		if( updatedDevice.getName() != null
				&& isValidDeviceName(updatedDevice.getName())
				&& !isDeviceNameExisted(homeId, deviceEntity.getId() , updatedDevice.getName())){
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

	private boolean isValidDeviceName(String name) throws Exception {
		if( name == null )
			return false;
		
		String newDeviceName = name.trim();
		if( ConstantUtil.ALL_DEVICE_ACTIONS.contains(newDeviceName) )
			throw new Exception("Device name can't be one of the followings " + ConstantUtil.ALL_DEVICE_ACTIONS);
		return true;
	}

	@Override
	public boolean isDeviceEnabled(int deviceId){
		return deviceDao.isEnabled(deviceId);
	}
	
	@Override
	public Integer getDeviceIdGivenNameAndHomeId(int homeId, String deviceName) throws NotFoundException{
		return deviceDao.getDeviceIdGivenNameAndHomeId(homeId, deviceName);
	}
	
	private boolean isDeviceNameExisted(int homeId, int deviceId, String deviceName) throws Exception{
		// Case update
		if( deviceId > 0 ){
			try{
				int idOfDeviceHasSameName =  deviceDao.getDeviceIdGivenNameAndHomeId(homeId, deviceName);
				if( idOfDeviceHasSameName > 0 && idOfDeviceHasSameName == deviceId){
					return false;
				}
				else throw new Exception(deviceName + " has already existed!");
			}catch(NotFoundException e){
				// Device name not found
				return false;
			}
		}
		// Case save
		else if( deviceDao.isDeviceNameExisted(homeId, deviceName) )
			throw new Exception(deviceName + " has already existed!");
		return false;
	}
	
	@Override
	public List<Script> getAllScriptsGivenMode(int modeId) throws Exception{
		List<ScriptEntity> scripts = scriptDao.getAllScriptsGivenMode(modeId);
		if( scripts != null )
			return ScriptConverter.toListModel(scripts);
		else throw new Exception("Can't get scripts with mode id " + modeId);
	}
	
	@Override
	public List<Script> getAllScriptsGivenHome(int homeId) throws Exception{
		List<ScriptEntity> scripts = scriptDao.getAllScripts(homeId);
		if( scripts != null )
			return ScriptConverter.toListModel(scripts);
		else throw new Exception("Can't get scripts with home id " + homeId);
	}
	
	@Override
	public Set<Integer> getListDeviceIdInScript(int homeId, Script script) throws Exception{
		Scenario scenario = scenarioService.scriptToScenario(homeId, script);
		if( scenario == null )
			return Collections.emptySet();
		return scenarioService.getListDeviceIdInScenario(scenario);
	}
	
	@Override
	public Set<Integer> getListDeviceIdInScript(int homeId, int scriptId) throws Exception{
		ScriptEntity scriptEntity = scriptDao.getById(scriptId);
		if( scriptEntity == null )
			throw new NotFoundException(String.format(SCRIPT_ID_NOT_FOUND, scriptId ));
		Script script = ScriptConverter.toModel(scriptEntity);
		
		return getListDeviceIdInScript(homeId, script);
	}
	
	@Override
	public List<ScriptMoreDetail> getAllScripts() throws Exception{
		List<ScriptEntity> scriptEntities = scriptDao.getAllScripts();
		if( scriptEntities != null )
			return ScriptConverter.toListModelWithMoreDetail(scriptEntities);
		throw new Exception("Something wrong with DB");
	}
}
