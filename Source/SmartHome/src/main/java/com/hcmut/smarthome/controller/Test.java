//package com.hcmut.smarthome.controller;
//
//import static com.hcmut.smarthome.utils.ConstantUtil.EQUAL;
//import static com.hcmut.smarthome.utils.ConstantUtil.GREATER_OR_EQUAL;
//import static com.hcmut.smarthome.utils.ConstantUtil.IS_NIGHT;
//import static com.hcmut.smarthome.utils.ConstantUtil.LESS_THAN;
//import static com.hcmut.smarthome.utils.ConstantUtil.LIGHT_SENSOR;
//import static com.hcmut.smarthome.utils.ConstantUtil.TEMPERATURE_SENSOR;
//import static com.hcmut.smarthome.utils.ConstantUtil.TURN_OFF;
//import static com.hcmut.smarthome.utils.ConstantUtil.TURN_ON;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import javax.transaction.NotSupportedException;
//
//import org.json.simple.parser.ParseException;
//
//import com.hcmut.smarthome.scenario.model.Scenario;
//import com.hcmut.smarthome.service.IScenarioService;
//import com.hcmut.smarthome.service.impl.ScenarioConflictValidator;
//import com.hcmut.smarthome.service.impl.ScenarioService;
//import com.hcmut.smarthome.utils.ConflictConditionException;
//import com.hcmut.smarthome.utils.Pair;
//import com.hcmut.smarthome.utils.ScriptBuilder;
//import com.hcmut.smarthome.utils.ScriptBuilder.ScriptBuilderTemplate;
//
//public class Test {
//	private static final int LIGHT_2 = 2;
//	private static final int LIGHT_3 = 3;
//	private static final String LSENSOR_4 = LIGHT_SENSOR + 4;
//	private static final String TSENSOR_5 = TEMPERATURE_SENSOR + 5;
//	private static final String TSENSOR_6 = TEMPERATURE_SENSOR + 6;
//	private static final String LSENSOR_7 = LIGHT_SENSOR + 7;
//	
//	ScenarioConflictValidator scenarioConflictService = new ScenarioConflictValidator();
//	IScenarioService scenarioService = new ScenarioService();
//	
//	public static void main(String[] args) throws ParseException, NotSupportedException, ConflictConditionException {
//	
//		Test test = new Test();
//		test.testCase8();
//		
//	}
//	
//	public void testCase8() throws ParseException, NotSupportedException, ConflictConditionException{
//		String input = new ScriptBuilder()
//		.begin()
//			.FromTo("00:00", "01:00")
//				.action(TURN_ON, 2)
//			.endFromTo()
//		.end().build();
//		
//		Scenario scenario = scenarioService.JSONToScenario(input);
//		scenario.setId(1);
//		scenario.setHomeId(1);
//		scenarioService.runScenario(scenario);
//	}
//	
//	/**
//	 * Input is more simpler than existing one 
//	 * @throws ParseException
//	 */
//	public void testCase1() throws ParseException{
//		String input = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5,GREATER_OR_EQUAL,35.5f)
//				.action(TURN_ON, LIGHT_2)
//			.endIf()
//		.end().build();
//				
//		List<String> existedScritps = new ArrayList<>();
//		
//		String existedScript = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
//				.If(LSENSOR_4, EQUAL, IS_NIGHT)
//					.action(TURN_ON, LIGHT_2)
//				.endIf()
//			.Else()
//				.If(LSENSOR_7, EQUAL, IS_NIGHT)
//					.action(TURN_ON, LIGHT_2)
//				.endIf()
//			.endIf()
//		.end().build();
//		existedScritps.add(existedScript);
//		
//		String existedScript1 = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5,GREATER_OR_EQUAL,35.5f)
//				.action(TURN_ON, LIGHT_2)
//			.endIf()
//		.end().build();
//		existedScritps.add(existedScript1);
//		
//		String existedScript2 = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5,LESS_THAN,35.5f)
//				.action(TURN_OFF, LIGHT_2)
//				.action(TURN_OFF, LIGHT_3)
//			.endIf()
//		.end().build();
//		existedScritps.add(existedScript2);
//		
//		String existedScript3 = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5,GREATER_OR_EQUAL,35.5f)
//				.action(TURN_OFF, LIGHT_3)
//			.endIf()
//		.end().build();
//		existedScritps.add(existedScript3);
//		
//		runTestScriptValidation(input, existedScritps);
//	}
//	
//	/**
//	 * Input is more complex than existing one
//	 * @throws ParseException
//	 */
//	public void testCase2() throws ParseException{
//		String input = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
//				.If(LSENSOR_4, EQUAL, IS_NIGHT)
//					.action(TURN_ON, LIGHT_2)
//				.endIf()
//			.Else()
//				.If(LSENSOR_7, EQUAL, IS_NIGHT)
//					.action(TURN_ON, LIGHT_2)
//				.endIf()
//			.endIf()
//		.end().build();
//		
//		List<String> existedScritps = new ArrayList<>();
//		
//		String existedScript = new ScriptBuilder()
//		.begin()
//			.If(LSENSOR_7, EQUAL, IS_NIGHT)
//				.action(TURN_ON, LIGHT_2)
//			.endIf()
//		.end().build();
//		existedScritps.add(existedScript);
//		
//		runTestScriptValidation(input, existedScritps);
//	}
//	
//	// TODO 
//	/**
//	 * Input has conflicted conditions itself -> Not check yet
//	 * @throws ParseException 
//	 */
//	public void testCase3() throws ParseException{
//		String input = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
//				.If(TSENSOR_5, LESS_THAN, 34)
//					.action(TURN_OFF, LIGHT_2)
//				.endIf()
//			.endIf()
//		.end().build();
//		
//		List<String> existedScritps = new ArrayList<>();
//		
//		String existedScript = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5,GREATER_OR_EQUAL,35.5f)
//				.action(TURN_OFF, LIGHT_2)
//			.endIf()
//		.end().build();
//		existedScritps.add(existedScript);
//		
//		runTestScriptValidation(input, existedScritps);
//	}
//	
//	/**
//	 * Input conflict range and has counter action with existing one
//	 * @throws ParseException 
//	 */
//	public void testCase4() throws ParseException{
//		String input = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
//				.If(TSENSOR_5, LESS_THAN, 64)
//					.action(TURN_OFF, LIGHT_2)
//				.endIf()
//			.endIf()
//		.end().build();
//		
//		List<String> existedScritps = new ArrayList<>();
//		
//		String existedScript = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5, GREATER_OR_EQUAL, 38.5f)
//				.action(TURN_ON, LIGHT_2)
//			.endIf()
//		.end().build();
//		
//		existedScritps.add(existedScript);
//		
//		runTestScriptValidation(input, existedScritps);
//	}
//	
//	/**
//	 * Input conflict range but not has counter action 
//	 * @throws ParseException 
//	 */
//	public void testCase5() throws ParseException{
//		String input = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
//				.If(TSENSOR_5, LESS_THAN, 64)
//					.action(TURN_OFF, LIGHT_2)
//				.endIf()
//			.endIf()
//		.end().build();
//		
//		List<String> existedScritps = new ArrayList<>();
//		
//		String existedScript = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5, GREATER_OR_EQUAL, 38.5f)
//				.action(TURN_OFF, LIGHT_2)
//			.endIf()
//		.end().build();
//		
//		existedScritps.add(existedScript);
//		
//		runTestScriptValidation(input, existedScritps);
//	}
//	
//	// TODO : Recheck 
//	/**
//	 * Not Conflict range but has counter action -> not work yet
//	 * @throws ParseException 
//	 */
//	public void testCase6() throws ParseException{
//		String input = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
//				.If(TSENSOR_5, LESS_THAN, 64)
//					.action(TURN_OFF, LIGHT_2)
//				.endIf()
//			.endIf()
//		.end().build();
//		
//		List<String> existedScritps = new ArrayList<>();
//		
//		String existedScript = new ScriptBuilder()
//		.begin()
//			.If(TSENSOR_5, LESS_THAN, 35.5f)
//				.action(TURN_OFF, LIGHT_2)
//			.endIf()
//		.end().build();
//		existedScritps.add(existedScript);
//		
//		runTestScriptValidation(input, existedScritps);
//	}
//	
//	/**
//	 * Conflict range Y
//	 * Counter action N
//	 * Different kind of devices Y 
//	 * -> valid
//	 * @throws ParseException 
//	 */
//	private void testCase7() throws ParseException{
//		String input = ScriptBuilderTemplate.blockIfOneAction(TSENSOR_5, GREATER_OR_EQUAL, 30, TURN_ON, LIGHT_2);
//		
//		List<String> existedScritps = new ArrayList<>();
//		String existedScript = ScriptBuilderTemplate.blockIfOneAction(TSENSOR_6, GREATER_OR_EQUAL, 35, TURN_ON, LIGHT_2);
//		existedScritps.add(existedScript);
//		
//		runTestScriptValidation(input, existedScritps);
//	}
//	
//	private void runTestScriptValidation(String inputScript, List<String> existedScripts) throws ParseException, NotSupportedException, ConflictConditionException{
//		
//		Pair<Scenario,List<Scenario>> pairInputAndExistedScenarios = scriptToScenario(inputScript,existedScripts);
//		Scenario inputScenario = pairInputAndExistedScenarios.getFirst();
//		List<Scenario> existedScenarios = pairInputAndExistedScenarios.getSecond();
//		
//		try{
//			boolean isValidate = scenarioConflictService.isNotConflicted(inputScenario, existedScenarios);
//			System.out.println("Script is validated ? -> " + isValidate);
//			if( isValidate )
//				scenarioService.runScenario(inputScenario);
//		}
//		catch(Exception e){
//			System.out.println(e.getMessage());
//		}
//	}
//	
//	private Pair<Scenario,List<Scenario>> scriptToScenario(String inputScript, List<String> existedScripts) throws ParseException, NotSupportedException, ConflictConditionException{
//		Scenario inputScenario = scenarioService.JSONToScenario(inputScript);
//		List<Scenario> existedScenarios = new ArrayList<>();
//		for (String existedScript : existedScripts) {
//			existedScenarios.add(scenarioService.JSONToScenario(existedScript));
//		}
//		
//		return new Pair<>(inputScenario, existedScenarios);
//	}
//}
