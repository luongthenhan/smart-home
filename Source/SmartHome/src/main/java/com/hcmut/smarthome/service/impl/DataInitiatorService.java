package com.hcmut.smarthome.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.model.ScriptMoreDetail;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IScenarioService;

@Service
public class DataInitiatorService {

	@Autowired
	private IScenarioService scenarioService;
	
	@Autowired
	private IDeviceService deviceService;
	
	@PostConstruct
	public void runAllScriptsAtFirstTimeStartApplication() throws Exception{
		List<ScriptMoreDetail> scripts = deviceService.getAllScripts();
		for (ScriptMoreDetail script : scripts) {
			Scenario scenario = scenarioService.scriptToScenario(script.getHomeId(), script);
			scenarioService.runScenario(script.getId(), script.getHomeId(), script.getDeviceId(), script.getModeId(), scenario);
		}
	}
}
