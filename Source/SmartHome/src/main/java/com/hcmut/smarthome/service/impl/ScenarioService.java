package com.hcmut.smarthome.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.NotSupportedException;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.model.Script;
import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.service.IDeviceService;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.utils.ConflictConditionException;

@Service
public class ScenarioService implements IScenarioService {

	private static final Logger LOGGER = Logger
			.getLogger(ScenarioService.class);

	// TODO: Remove new object here after testing
	@Autowired
	private ScenarioCreator scenarioCreator;
	
	@Autowired
	private ScenarioRunner scenarioRunner;
	
	@Autowired
	private ScenarioValidator scenarioValidator;
	
	@Autowired
	private IDeviceService deviceService;
	
	public String JSONToString() {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public boolean isValid(int modeId, int deviceId, Scenario scenario) throws ParseException, NotSupportedException, ConflictConditionException{
		List<Script> existedScripts = deviceService.getScripts(modeId,deviceId);
		List<Scenario> existedScenarios = new ArrayList<Scenario>();
		for (Script existedScript : existedScripts) {
			Scenario existedScenario = JSONToScenario(existedScript.getContent());
			existedScenarios.add(existedScenario);
		}
		return isValid(scenario, existedScenarios);
	}
	
	
	@Override
	public boolean isValid(Scenario inputScenario,
			List<Scenario> existedScenarios) throws NotSupportedException, ConflictConditionException {
		return scenarioValidator.isValid(inputScenario, existedScenarios);
	}
	
	// TODO : When one script is removed , how to know and get rid of it and
	// also stop the timer
	@Override
	public void runScenario(Scenario scenario) {
		scenarioRunner.runScenario(scenario);
	}

	@Override
	public void stopScenario(int id) {
		scenarioRunner.stopScenario(id);
	}

	@Override
	public void stopForeverScenario(int id) {
		scenarioRunner.stopForeverScenario(id);
	}

	// TODO: Change parameter from String to Script ( for assigning id to
	// scenario after return)
	public Scenario JSONToScenario(String script) throws ParseException {
		return scenarioCreator.from(script);
	}

	

}
