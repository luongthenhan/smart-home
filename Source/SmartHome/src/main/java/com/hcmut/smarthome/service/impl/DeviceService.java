package com.hcmut.smarthome.service.impl;

import static com.hcmut.smarthome.utils.ConstantUtil.ADD_UNSUCCESSFULLY;

import java.util.List;

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
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.ConflictConditionException;
import com.hcmut.smarthome.utils.ScriptBuilder;

@Service
public class DeviceService implements IDeviceService {
	private static final String CUSTOM_SCRIPT_TYPE = "Custom";

	// TODO : Update map after add new / update / delete something. Also in this time
	// call stopOrRemoveScenario. 
	// * Handle add new home ?
	//private HashMap<Integer,List<Device>> mapHomeDevices = new HashMap<>();
	
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
	
//	@PostConstruct
//	private void init(){
//		mapHomeDevices.put(HOME_ID, getAllDevices(HOME_ID));
//	}
	
	@Override
	public int addDevice(int homeId, int deviceTypeId, Device device) {
		
		DeviceEntity deviceEntity = new DeviceEntity();
		initDeviceEntityBeforeSaveOrUpdate(homeId, deviceTypeId, device, deviceEntity);

		int deviceId = deviceDao.save(deviceEntity).intValue();
		if( deviceId > 0 ){
//			deviceEntity = deviceDao.getById(deviceId);
//			updateMapHomeDevices(homeId, deviceId, deviceEntity);
			return deviceId;
		}
		return ADD_UNSUCCESSFULLY;
	}
	
	// TODO: Now PUT use the implementation of PATCH
	@Override
	public boolean updateDevice(int homeId, int deviceId, int deviceTypeId, Device updatedDevice) {
		return updatePartialDevice(homeId, deviceId, deviceTypeId, updatedDevice);
	}
	
	@Override
	public boolean deleteDevice(int homeId, int deviceId) {
		if( deviceDao.delete(deviceId) ){
			scenarioService.stopForeverScenarioInDevice(deviceId);
			//updateMapHomeDevices(homeId, deviceId, null);
			return true;
		}
		return false;
	}
	
	@Override
	public List<Device> getAllGivenHomeAndDeviceType(int homeId, int deviceTypeId){
		return DeviceConverter.toListModel(deviceDao.getAllGivenHomeAndDeviceType(homeId, deviceTypeId));
//		if( !mapHomeDevices.containsKey(homeId) ){
//			List<DeviceEntity> deviceEntities = deviceDao.getAll(homeId);
//			mapHomeDevices.put(homeId, DeviceConverter.toListModel(deviceEntities));
//		}
//		return mapHomeDevices.get(homeId).stream().filter(d->d.getDeviceType().getId() == deviceTypeId).collect(Collectors.toList()) ;
	}

	@Override
	public List<Device> getAllDevices(int homeId) {
		return DeviceConverter.toListModel(deviceDao.getAll(homeId));
//		if( !mapHomeDevices.containsKey(homeId) ){
//			List<DeviceEntity> deviceEntities = deviceDao.getAll(homeId);
//			mapHomeDevices.put(homeId, DeviceConverter.toListModel(deviceEntities));
//		}
//		return mapHomeDevices.get(homeId);
	}
	
	
	@Override
	public Device getDevice(int homeId, int deviceId) {
		return DeviceConverter.toModel(deviceDao.getById(deviceId));
//		if( mapHomeDevices.containsKey(homeId) ){
//			return mapHomeDevices.get(homeId).stream().filter(t -> t.getId() == deviceId).findFirst().orElse(null);
//		}
//		return null;
	}
	
	@Override
	public List<Script> getScripts(int modeId, int deviceId) {
		List<ScriptEntity> scriptEntities = scriptDao.getAllScripts(modeId, deviceId);
		return ScriptConverter.toListModel(scriptEntities);
	}
	
	@Override
	public boolean deleteScript( int deviceId, int scriptId) {
		if (scriptDao.deleteScript(scriptId)) {
			scenarioService.stopForeverScenario(scriptId);
			
//			int homeId = homeDao.getHomeIdGivenDevice(deviceId);
//			
//			if( mapHomeDevices.containsKey(homeId) ){
//				List<Device> devices = mapHomeDevices.get(homeId);
//				devices.removeIf(d -> d.getScripts().stream().filter(s -> s.getId() == scriptId).findFirst().orElse(null) != null);
//			}
			return true;
		}
		return false;
	}

	// TODO : save/update script custom . check name 
	@Override
	public int addScript(Script script, int deviceId , int modeId, int homeId) throws ParseException, NotSupportedException, ConflictConditionException, ScriptException {
		
		Scenario scenario = scriptToScenario(script);
		boolean isValid = scenarioService.isValid(modeId, deviceId, script, scenario);
		
		if( isValid ){
			int scenarioId = saveScriptToDB(modeId, deviceId, script); 
			runScenario(scenarioId, homeId, deviceId, modeId, scenario);
			return (scenarioId > 0 ? scenarioId : ADD_UNSUCCESSFULLY);
		}
		
		return ADD_UNSUCCESSFULLY;
	}
	
	// TODO: Now update a script involved so many queries -> need to improve performance
	@Override
	public boolean updateScript(int homeId, int modeId, int deviceId, int scriptId, Script updatedScript) throws ParseException, NotSupportedException, ConflictConditionException, ScriptException {
		return updatePartialScript(homeId, modeId, deviceId, scriptId, updatedScript);
	}


	@Override
	public boolean updatePartialScript(int homeId, int modeId, int deviceId, int scriptId, Script scriptToUpdate) throws ParseException, NotSupportedException, ConflictConditionException, ScriptException {
		ScriptEntity updatedScriptEntity = scriptDao.getById(scriptId);
		
		// Found in DB
		if( updatedScriptEntity != null ){
			Scenario updatedScenario = scriptToScenario(scriptToUpdate);
			boolean isValid = scenarioService.isValid(modeId, deviceId, scriptToUpdate, updatedScenario);
			if( isValid ){
				boolean isUpdateSuccessfully = updateScriptToDB(scriptToUpdate,updatedScriptEntity);
				if( isUpdateSuccessfully ){
					runScenario(scriptId, homeId, deviceId, modeId, updatedScenario);
				}
			}
		}
		
		// Not found or not valid
		return false;
	}

	private boolean updateScriptToDB(Script scriptToUpdate, ScriptEntity updatedScriptEntity){
		if( scriptToUpdate.getContent() != null )
			updatedScriptEntity.setContent(scriptToUpdate.getContent());
		
		if( scriptToUpdate.getName() != null )
			updatedScriptEntity.setName(scriptToUpdate.getName());
		
		if( scriptToUpdate.getType() != null && scriptToUpdate.getType().getId() > 0 ){
			ScriptTypeEntity scriptTypeEntity = new ScriptTypeEntity();
			scriptTypeEntity.setId(scriptToUpdate.getType().getId());
			updatedScriptEntity.setScriptType(scriptTypeEntity);
		}
		
		if( scriptDao.update(updatedScriptEntity) ){
//			if( mapHomeDevices.containsKey(homeId) ){
//				List<Device> devices = mapHomeDevices.get(homeId);
//				devices.removeIf(d -> d.getScripts().stream().filter(s -> s.getId() == scriptId).findFirst().get() != null);
//			}
			return true;
		}
		return false;
	}
	
	/**
	 * Convert script to scenario <br/>
	 * If script is custom type , need to use ScriptBuilder.parse first
	 * @param script
	 * @return
	 * @throws ParseException
	 * @throws ScriptException
	 */
	private Scenario scriptToScenario(Script script) throws ParseException, ScriptException{
		Scenario scenario = null;
		if( script.getContent() != null ){
			String jsonScript = script.getContent();
			if( script.getType() != null && CUSTOM_SCRIPT_TYPE.equals(script.getType().getName()) ){
				jsonScript = ScriptBuilder.parseFromCodeAsString(script.getContent());
			}
			scenario = scenarioService.JSONToScenario(jsonScript);
		}
		return scenario;
	}
	
	// TODO HomeId now is hard-coded
	private void runScenario(int scenarioId, int homeId, int deviceId, int modeId, Scenario scenario){
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
	public boolean updatePartialDevice(int homeId, int deviceId,
			int deviceTypeId, Device updatedDevice) {
		DeviceEntity deviceEntity = deviceDao.getById(deviceId);
		
		initDeviceEntityBeforeSaveOrUpdate(homeId, deviceTypeId, updatedDevice, deviceEntity);
		
		if( deviceDao.update(deviceEntity)){
			// TODO : When we have already had the function to create home , this case will not happen anymore
			//updateMapHomeDevices(homeId, deviceId, deviceEntity);
			return true;
		}
		return false;
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
		
		if( deviceEntity.isEnabled() != updatedDevice.isEnabled() )
			deviceEntity.setEnabled(updatedDevice.isEnabled());
		
		// TODO: Not implement check valid GPIO yet
		if( updatedDevice.getGPIO() != null && updatedDevice.getGPIO() > 0 )
			deviceEntity.setGPIOPin(updatedDevice.getGPIO());

		if( updatedDevice.getGPIOType() != null )
			deviceEntity.setGPIOType(updatedDevice.getGPIOType());
		
		if( updatedDevice.getLocation() != null )
			deviceEntity.setLocation(updatedDevice.getLocation());
		
		if( updatedDevice.getName() != null )
			deviceEntity.setName(updatedDevice.getName());
		
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
	
//	private void updateMapHomeDevices(int homeId, int deviceId, DeviceEntity device){
//		if( mapHomeDevices.containsKey(homeId) ){
//			List<Device> devices = mapHomeDevices.get(homeId);
//			devices.removeIf(d -> d.getId() == deviceId);
//			if( device != null )
//				devices.add(DeviceConverter.toModel(device));
//		}
//	}
}
