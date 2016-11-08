package com.hcmut.smarthome.service.test;

import static com.hcmut.smarthome.utils.ConstantUtil.GREATER_OR_EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.LESS_THAN;
import static com.hcmut.smarthome.utils.ConstantUtil.TURN_OFF;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.service.impl.ScenarioConflictValidator;
import com.hcmut.smarthome.utils.ConstantUtil;
import com.hcmut.smarthome.utils.ScriptBuilder;

@ContextConfiguration( locations = { "classpath:ApplicationContext.xml", "classpath:spring-security.xml" } )
@RunWith(SpringJUnit4ClassRunner.class)
public class ScenarioExistenceCheckingTest {

	private static final int LIGHT_2 = 2;
	private static final int LIGHT_3 = 3;
	private static final int LSENSOR_4 = 11;
	private static final int TSENSOR_5 = 5;
	private static final int TSENSOR_6 = 6;
	private static final int LSENSOR_7 = 7;
	
	@Autowired
	IScenarioService scenarioService;
	
	@Autowired
	ScenarioConflictValidator scenarioConflictService;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void test_disordered_condition_with_nested_If() throws Exception {
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(TSENSOR_6, LESS_THAN, 40 )
					.action(TURN_OFF, LIGHT_3)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = new ScriptBuilder()
		.begin()
			.If(TSENSOR_6, LESS_THAN, 40 )
				.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
					.action(TURN_OFF, LIGHT_3)
				.endIf()
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		Scenario scenario = scenarioService.JSONToScenario(ConstantUtil.HOME_ID, input);
		List<Scenario> existedScenarios = new ArrayList<>();
		existedScenarios.add(scenarioService.JSONToScenario(ConstantUtil.HOME_ID, existedScript));
		
		boolean result = scenarioConflictService.checkDuplicateScenario(scenario, existedScenarios);
		assertThat(result, is(true));
	}
	
	@Test
	public void test_disordered_condition_with_nested_IfElse() throws Exception {
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(TSENSOR_6, LESS_THAN, 40 )
					.action(TURN_OFF, LIGHT_3)
				.Else()
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = new ScriptBuilder()
		.begin()
			.If(TSENSOR_6, LESS_THAN, 40 )
				.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
					.action(TURN_OFF, LIGHT_3)
				.Else()
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		Scenario scenario = scenarioService.JSONToScenario(ConstantUtil.HOME_ID, input);
		List<Scenario> existedScenarios = new ArrayList<>();
		existedScenarios.add(scenarioService.JSONToScenario(ConstantUtil.HOME_ID, existedScript));
		
		boolean result = scenarioConflictService.checkDuplicateScenario(scenario, existedScenarios);
		assertThat(result, is(false));
	}

	@Test
	public void test_disordered_condition_in_else() throws Exception {
		String input = new ScriptBuilder()
		.begin()
				.If(TSENSOR_5, GREATER_OR_EQUAL, 40 )
					.action(TURN_OFF, LIGHT_2)
				.Else()
					.action(TURN_OFF, LIGHT_3)
				.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = new ScriptBuilder()
		.begin()
				.If(TSENSOR_5, LESS_THAN, 40)
					.action(TURN_OFF, LIGHT_3)
				.Else()
					.action(TURN_OFF, LIGHT_2)
				.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		Scenario scenario = scenarioService.JSONToScenario(ConstantUtil.HOME_ID, input);
		List<Scenario> existedScenarios = new ArrayList<>();
		existedScenarios.add(scenarioService.JSONToScenario(ConstantUtil.HOME_ID, existedScript));
		
		boolean result = scenarioConflictService.checkDuplicateScenario(scenario, existedScenarios);
		assertThat(result, is(true));
	}
	
	@Test
	public void test_disordered_actions() throws Exception {
		String input = new ScriptBuilder()
		.begin()
			.action(TURN_OFF, LIGHT_2)
			.action(TURN_OFF, LIGHT_3)
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = new ScriptBuilder()
		.begin()
			.action(TURN_OFF, LIGHT_3)
			.action(TURN_OFF, LIGHT_2)
		.end().build();
		
		existedScritps.add(existedScript);
		
		Scenario scenario = scenarioService.JSONToScenario(ConstantUtil.HOME_ID, input);
		List<Scenario> existedScenarios = new ArrayList<>();
		existedScenarios.add(scenarioService.JSONToScenario(ConstantUtil.HOME_ID, existedScript));
		
		boolean result = scenarioConflictService.checkDuplicateScenario(scenario, existedScenarios);
		assertThat(result, is(true));
	}
	
	@Test
	public void test_disordered_condition_in_with_FromTo_and_If() throws Exception {
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.FromTo("12:00", "13:00")
					.action(TURN_OFF, LIGHT_2)
				.endFromTo()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = new ScriptBuilder()
		.begin()
			.FromTo("12:00", "13:00")
				.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endFromTo()	
		.end().build();
		
		existedScritps.add(existedScript);
		
		Scenario scenario = scenarioService.JSONToScenario(ConstantUtil.HOME_ID, input);
		List<Scenario> existedScenarios = new ArrayList<>();
		existedScenarios.add(scenarioService.JSONToScenario(ConstantUtil.HOME_ID, existedScript));
		
		boolean result = scenarioConflictService.checkDuplicateScenario(scenario, existedScenarios);
		assertThat(result, is(true));
	}
	
	@Test
	public void test_disordered_condition_in_with_FromTo_has_different_time_and_If() throws Exception {
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.FromTo("12:00", "14:00")
					.action(TURN_OFF, LIGHT_2)
				.endFromTo()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = new ScriptBuilder()
		.begin()
			.FromTo("12:00", "13:00")
				.If(TSENSOR_5, ConstantUtil.GREATER_THAN, 35.5f)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endFromTo()	
		.end().build();
		
		existedScritps.add(existedScript);
		
		Scenario scenario = scenarioService.JSONToScenario(ConstantUtil.HOME_ID, input);
		List<Scenario> existedScenarios = new ArrayList<>();
		existedScenarios.add(scenarioService.JSONToScenario(ConstantUtil.HOME_ID, existedScript));
		
		boolean result = scenarioConflictService.checkDuplicateScenario(scenario, existedScenarios);
		assertThat(result, is(false));
	}
	
	@Test
	public void test_disordered_condition_in_with_FromTo_and_If_has_different_operator() throws Exception {
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(TSENSOR_6, GREATER_OR_EQUAL, 35.5f)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = new ScriptBuilder()
		.begin()
			.FromTo("12:00", "13:00")
				.If(TSENSOR_5, ConstantUtil.GREATER_THAN, 35.5f)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endFromTo()	
		.end().build();
		
		existedScritps.add(existedScript);
		
		Scenario scenario = scenarioService.JSONToScenario(ConstantUtil.HOME_ID, input);
		List<Scenario> existedScenarios = new ArrayList<>();
		existedScenarios.add(scenarioService.JSONToScenario(ConstantUtil.HOME_ID, existedScript));
		
		boolean result = scenarioConflictService.checkDuplicateScenario(scenario, existedScenarios);
		assertThat(result, is(false));
	}
}
