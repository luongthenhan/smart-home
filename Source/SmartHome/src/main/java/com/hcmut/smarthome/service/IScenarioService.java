package com.hcmut.smarthome.service;

import org.json.simple.parser.ParseException;

import com.hcmut.smarthome.scenario.model.Scenario;

public interface IScenarioService {
	String JSONToString();
	void runScenario(Scenario scenario);
	Scenario JSONToScenario(String script) throws ParseException;
}
