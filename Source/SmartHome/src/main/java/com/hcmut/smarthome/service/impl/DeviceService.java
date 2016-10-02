package com.hcmut.smarthome.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.converter.DeviceConverter;
import com.hcmut.smarthome.converter.ScriptConverter;
import com.hcmut.smarthome.dao.IDeviceDao;
import com.hcmut.smarthome.dao.IScriptDao;
import com.hcmut.smarthome.entity.DeviceEntity;
import com.hcmut.smarthome.entity.ModeEntity;
import com.hcmut.smarthome.entity.ScriptEntity;
import com.hcmut.smarthome.entity.ScriptTypeEntity;
import com.hcmut.smarthome.model.Device;
import com.hcmut.smarthome.model.Script;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.utils.ConstantUtil;
import static com.hcmut.smarthome.utils.ConstantUtil.ALL_GPIO;
import static com.hcmut.smarthome.utils.ConstantUtil.ALWAYS_AVAILABLE_GPIO;

@Service
public class DeviceService implements IDeviceService {
	private boolean isLightOn = true;
	private boolean isBuzzerBeep = true;
	private boolean isDayLight = true;
	
	// TODO : Update map after add new / update / delete something. Also in this time
	// call stopOrRemoveScenario
	private HashMap<Integer,List<Device>> mapHomeDevices = new HashMap<>();
	
	@Autowired
	private ScenarioService scenarioService;
	
	@Autowired
	private IDeviceDao deviceDao;
	
	@Autowired
	private IScriptDao scriptDao;
	
	@PostConstruct
	private void init(){
		mapHomeDevices.put(ConstantUtil.HOME_ID, getAllDevices(ConstantUtil.HOME_ID));
	}
	
	@Override
	public List<Device> getAllGivenHomeAndDeviceType(int homeId, int deviceTypeId){
		List<DeviceEntity> devices = deviceDao.getAllGivenHomeAndDeviceType(homeId, deviceTypeId);
		return DeviceConverter.toListModel(devices);
	}

	@Override
	public List<Device> getAllDevices(int homeId) {
		if( !mapHomeDevices.containsKey(homeId) ){
			List<DeviceEntity> deviceEntities = deviceDao.getAll(homeId);
			mapHomeDevices.put(homeId, DeviceConverter.toListModel(deviceEntities));
		}
		return mapHomeDevices.get(homeId);
	}
	
	@Override
	public Device getDevice(int homeId, int deviceId) {
		System.out.println("Call get device by id");
		if( mapHomeDevices.containsKey(homeId) ){
			return mapHomeDevices.get(homeId).stream().filter(t -> t.getId() == deviceId).findFirst().orElse(null);
		}
		return null;
	}
	
	@Override
	public List<Script> getScripts(int modeId, int deviceId) {
		List<ScriptEntity> scriptEntities = scriptDao.getAllScripts(modeId, deviceId);
		return ScriptConverter.toListModel(scriptEntities);
	}
	
	@Override
	public boolean deleteScript(int scriptId) {
		scriptDao.deleteScript(scriptId);
		//scenarioService.stopForeverScenario(scriptId);
		return true;
	}

	// TODO: Now update a script involved so many queries -> need to improve performance
	@Override
	public boolean updateScript(int scriptId, Script updatedScript) {
		ScriptEntity updatedScriptEntity = scriptDao.getById(scriptId);
		updatedScriptEntity.setContent(updatedScript.getContent());
		updatedScriptEntity.setName(updatedScript.getName());
		scriptDao.update(updatedScriptEntity);
		return false;
	}


	@Override
	public boolean addScript(Script script, int deviceId , int modeId) {
		ScriptEntity scriptEntity = new ScriptEntity();
		scriptEntity.setName(script.getName());
		scriptEntity.setContent(script.getContent());
		
		ModeEntity mode = new ModeEntity();
		mode.setId(modeId);
		scriptEntity.setMode(mode);
		
		ScriptTypeEntity scriptType = new ScriptTypeEntity();
		scriptType.setId(script.getType().getId());
		scriptEntity.setScriptType(scriptType);
		
		DeviceEntity device = new DeviceEntity();
		device.setId(deviceId);
		scriptEntity.setDevice(device);
		
		scriptDao.save(scriptEntity);
		return false;
	}
	
	@Override
	public List<Integer> getAllAvailableGpio(int homeId) {
		List<Integer> availableGpio = new ArrayList<Integer>();
		List<Device> devices = getAllDevices(homeId);
		
		for( int i = 0; i < ALL_GPIO.size(); i++ ) {
			if(isAvailable(ALL_GPIO.get(i).intValue(), devices)) {
				availableGpio.add(ALL_GPIO.get(i));
			}
		}
		
		return availableGpio;
	}
	
	private boolean isAvailable(int gpio, List<Device> devices) {
		
		if( isAlwaysAvailable(gpio) ) {
			return true;
		}
		
		for(Device device : devices) {
			if(device.isEnabled() && (device.getGPIO().intValue() == gpio) ) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isAlwaysAvailable(int gpio) {
		
		for(int i = 0; i < ALWAYS_AVAILABLE_GPIO.size(); i++) {
			if(gpio == ALWAYS_AVAILABLE_GPIO.get(i).intValue()) {
				return true;
			}
		}
		
		return false;
	}
	
	/// AVAILABLE FOR TESTING PURPOSE 
	// TODO: Remove later 
	public void toggleLight(String deviceName){
		if( isLightOn )
			System.out.println("Turn " + deviceName + " on .....");
		else System.out.println("Turn " + deviceName + " off .....");
		isLightOn = !isLightOn;
	}
	
	public boolean isLightOn(String deviceName){
		System.out.println("Check " + deviceName + " is on...");
		return isLightOn;
	}
	
	public boolean isBuzzerBeep(String deviceName){
		System.out.println("Check " + deviceName + " is beep...");
		return isBuzzerBeep;
	}
	
	public void toggleBuzzer(String deviceName){
		if( isBuzzerBeep )
			System.out.println(deviceName + " beep .....");
		else System.out.println( deviceName + " is silent .....");
		isBuzzerBeep = !isBuzzerBeep;
		
	}
	
	public boolean isDayLight(String deviceName){
		System.out.println("Check " + deviceName + " is day light...");
		return isDayLight;
	}
	
	public float getLightIntensity(String deviceName){
		System.out.println("Get light intensity from " + deviceName);
		return 35.5F;
	}
	
	private float temp = 32.5F;
	public float getTemperature(String deviceName){
		temp = temp + 1 ;
		System.out.println("Get temperature from " + deviceName + " :" +temp);
		
		return temp;
	}
	
	public float getGasThreshold(String deviceName){
		System.out.println("Get gas threshold from " + deviceName);
		return 0.95F;
	}

	public void takeAShot(String deviceName) {
		System.out.println("Take a shot from " + deviceName);
	}


}
