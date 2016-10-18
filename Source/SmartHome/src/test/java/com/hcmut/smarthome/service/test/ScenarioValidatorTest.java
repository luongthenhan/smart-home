package com.hcmut.smarthome.service.test;

import static com.hcmut.smarthome.utils.ConstantUtil.BOTH_IF_ELSE_BLOCK_YIELD_SAME_ACTION;
import static com.hcmut.smarthome.utils.ConstantUtil.EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.GREATER_OR_EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.IS_NIGHT;
import static com.hcmut.smarthome.utils.ConstantUtil.LESS_THAN;
import static com.hcmut.smarthome.utils.ConstantUtil.LIGHT_SENSOR;
import static com.hcmut.smarthome.utils.ConstantUtil.NOT_EQUAL;
import static com.hcmut.smarthome.utils.ConstantUtil.TEMPERATURE_SENSOR;
import static com.hcmut.smarthome.utils.ConstantUtil.TURN_OFF;
import static com.hcmut.smarthome.utils.ConstantUtil.TURN_ON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.NotSupportedException;

import org.json.simple.parser.ParseException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.hcmut.smarthome.scenario.model.Scenario;
import com.hcmut.smarthome.service.IScenarioService;
import com.hcmut.smarthome.service.impl.ScenarioService;
import com.hcmut.smarthome.utils.ConflictConditionException;
import com.hcmut.smarthome.utils.ConstantUtil;
import com.hcmut.smarthome.utils.Pair;
import com.hcmut.smarthome.utils.ScriptBuilder;
import com.hcmut.smarthome.utils.ScriptBuilder.ScriptBuilderTemplate;

public class ScenarioValidatorTest {
	
	private static final int LIGHT_2 = 2;
	private static final int LIGHT_3 = 3;
	private static final String LSENSOR_4 = LIGHT_SENSOR + 4;
	private static final String TSENSOR_5 = TEMPERATURE_SENSOR + 5;
	private static final String TSENSOR_6 = TEMPERATURE_SENSOR + 6;
	private static final String LSENSOR_7 = LIGHT_SENSOR + 7;
	
	IScenarioService scenarioService = new ScenarioService();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * Input is more simpler than existing one 
	 * 
	 * -> INVALID
	 * @throws ParseException
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase10_ManyExistingScripts() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_OR_EQUAL,35.5f)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
				
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(LSENSOR_4, EQUAL, IS_NIGHT)
					.action(TURN_ON, LIGHT_2)
				.endIf()
			.Else()
				.If(LSENSOR_7, EQUAL, IS_NIGHT)
					.action(TURN_ON, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		existedScritps.add(existedScript);
		
		String existedScript1 = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_OR_EQUAL,35.5f)
				.action(TURN_OFF, LIGHT_2)
			.endIf()
		.end().build();
		existedScritps.add(existedScript1);
		
		String existedScript2 = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,LESS_THAN,35.5f)
				.action(TURN_ON, LIGHT_2)
				.action(TURN_OFF, LIGHT_3)
			.endIf()
		.end().build();
		existedScritps.add(existedScript2);
		
		String existedScript3 = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_OR_EQUAL,35.5f)
				.action(TURN_OFF, LIGHT_3)
			.endIf()
		.end().build();
		existedScritps.add(existedScript3);
		
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Input is more complex than existing one
	 * 
	 * -> VALID
	 * @throws ParseException
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase11() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(LSENSOR_4, EQUAL, IS_NIGHT)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.Else()
				.If(LSENSOR_7, EQUAL, IS_NIGHT)
					.action(TURN_ON, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(LSENSOR_7, EQUAL, IS_NIGHT)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Input has conflicted conditions itself ( float value )
	 * 
	 * -> INVALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase3() throws ParseException, NotSupportedException, ConflictConditionException{
		expectedException.expect(ConflictConditionException.class);
		
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(TSENSOR_5, LESS_THAN, 34)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Input has conflicted conditions itself ( boolean value )
	 * 
	 * -> INVALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase3_1() throws ParseException, NotSupportedException, ConflictConditionException{
		expectedException.expect(ConflictConditionException.class);
		
		String input = new ScriptBuilder()
		.begin()
			.If(LSENSOR_4, NOT_EQUAL, IS_NIGHT)
				.If(LSENSOR_4, EQUAL, IS_NIGHT)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Input has conflicted conditions itself ( mixed condition types)
	 * 
	 * -> INVALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase3_2() throws ParseException, NotSupportedException, ConflictConditionException{
		expectedException.expect(ConflictConditionException.class);
		
		String input = new ScriptBuilder()
		.begin()
			.If(LSENSOR_4, NOT_EQUAL, IS_NIGHT)
				.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
					.If(LSENSOR_4, EQUAL, IS_NIGHT)
						.action(TURN_OFF, LIGHT_2)
					.endIf()
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Input has conflicted conditions itself ( conflict in Else clause)
	 * 
	 * -> INVALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase3_3() throws ParseException, NotSupportedException, ConflictConditionException{
		expectedException.expect(ConflictConditionException.class);
		
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.action(TURN_OFF, LIGHT_2)
			.Else()
				.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes 
	 * Counter action Yes
	 * Different devs No
	 * 
	 * -> INVALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase4() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(TSENSOR_5, LESS_THAN, 64)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 38.5f)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes 
	 * Counter action No
	 * Different devs No ( just use temp sensor in condition )
	 * 
	 * -> VALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase5() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(TSENSOR_5, LESS_THAN, 64)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 38.5f)
				.action(TURN_OFF, LIGHT_2)
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes 
	 * Counter action No
	 * Different devs No ( just use Light Sensor in condition )
	 * 
	 * -> VALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase5_1() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(LSENSOR_4, EQUAL, IS_NIGHT)
				.If(TSENSOR_5, LESS_THAN, 64)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(LSENSOR_4, EQUAL, IS_NIGHT)
				.action(TURN_OFF, LIGHT_2)
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes 
	 * Counter action Yes
	 * Different devs No ( just use Temp sensor )
	 * 
	 * -> INVALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase6() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(TSENSOR_5, LESS_THAN, 64)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, LESS_THAN, 78.5f)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes ( use equal )
	 * Counter action Yes
	 * Different devs No ( just use Temp sensor in Condition )
	 * 
	 * -> INVALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase6_3() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, EQUAL, 35.5f)
				.action(TURN_OFF, LIGHT_2)
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, LESS_THAN, 78.5f)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes ( use Else condition )
	 * Counter action Yes
	 * Different devs No ( just use Temp sensor in Condition )
	 * 
	 * -> INVALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase6_4() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, EQUAL, 35.5f)
				.action(TURN_ON, LIGHT_2)
			.Else()
				.action(TURN_OFF, LIGHT_2)
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, NOT_EQUAL, 35.5f)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	//TODO
	/**
	 * Conflict range Yes ( use Else condition )
	 * Counter action Yes
	 * Different devs No ( just use Temp sensor in Condition )
	 * 
	 * -> VALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase6_8() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, LESS_THAN, 35.5f)
					.action(TURN_OFF, LIGHT_2)
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, LESS_THAN, 40.5f)
			.If(TSENSOR_5, ConstantUtil.GREATER_THAN, 39.5f)
				.action(TURN_ON, LIGHT_2)
			.endIf()
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes ( use Else condition )
	 * Counter action Yes
	 * Different devs No ( just use Temp sensor in Condition )
	 * 
	 * -> INVALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase6_9() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, LESS_THAN, 35.5f)
				.action(TURN_OFF, LIGHT_2)
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, LESS_THAN, 40.5f)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes 
	 * Counter action Yes
	 * Different devs No ( just use Light Sensor in Condition )
	 * 
	 * -> INVALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase6_1() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(LSENSOR_4, EQUAL, IS_NIGHT)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(LSENSOR_4, EQUAL, IS_NIGHT)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes ( in Else condition )
	 * Counter action Yes
	 * Different devs No ( just use Light Sensor in Condition )
	 * 
	 * -> INVALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase6_2() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(LSENSOR_4, NOT_EQUAL, IS_NIGHT)
					.action(TURN_OFF, LIGHT_3)
				.Else()
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(LSENSOR_4, EQUAL, IS_NIGHT)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes ( in Else condition )
	 * Counter action Yes
	 * Different devs Yes ( just use Light Sensor in Condition )
	 * 
	 * -> VALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase6_7() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(LSENSOR_4, NOT_EQUAL, IS_NIGHT)
					.action(TURN_OFF, LIGHT_2)
				.Else()
					.action(TURN_OFF, LIGHT_3)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(LSENSOR_4, EQUAL, IS_NIGHT)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	// TODO: Recheck: If in block if and else have the same action , does it mean that in every case 
	/**
	 * Conflict range Yes ( stupid script but we must handle this case )
	 * Counter action Yes
	 * Different devs No
	 * 
	 * -> INVALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase6_5() throws ParseException, NotSupportedException, ConflictConditionException{
		expectedException.expect(ConflictConditionException.class);
		expectedException.expectMessage(BOTH_IF_ELSE_BLOCK_YIELD_SAME_ACTION);
		
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.action(TURN_OFF, LIGHT_2)
			.Else()
				.action(TURN_OFF, LIGHT_2)
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(LSENSOR_4, NOT_EQUAL, IS_NIGHT)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes ( a little bit complex condition )
	 * Counter action Yes
	 * Different devs No
	 * 
	 * -> VALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase6_6() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.action(TURN_OFF, LIGHT_3)
				.action(TURN_OFF, LIGHT_2)
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(LSENSOR_4, EQUAL, IS_NIGHT)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range No 
	 * Counter action Yes
	 * Different devs No
	 * 
	 * -> VALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase7() throws ParseException, NotSupportedException, ConflictConditionException{
		
		
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(TSENSOR_5, LESS_THAN, 64)
					.action(TURN_OFF, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, LESS_THAN, 35.5f)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range No 
	 * Counter action Yes ( script has only actions )
	 * Different devs No
	 * 
	 * -> INVALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase7_1() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.action(TURN_OFF, LIGHT_3)
			.action(TURN_OFF, LIGHT_2)
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, LESS_THAN, 35.5f)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		existedScritps.add(existedScript);
		
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range No ( mixed condition types )
	 * Counter action Yes
	 * Different devs No
	 * 
	 * -> VALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase7_2() throws ParseException, NotSupportedException, ConflictConditionException{
		
		
		String input = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_OR_EQUAL, 35.5f)
				.If(LSENSOR_4, EQUAL, IS_NIGHT)
					.If(LSENSOR_7, EQUAL, IS_NIGHT)
						.If(TSENSOR_5, LESS_THAN, 64)
							.action(TURN_OFF, LIGHT_2)
						.endIf()
						.action(TURN_OFF, LIGHT_2)
					.endIf()
					.action(TURN_OFF, LIGHT_2)
				.endIf()
				.action(TURN_OFF, LIGHT_2)
			.endIf()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		
		String existedScript = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, LESS_THAN, 35.5f)
				.action(TURN_ON, LIGHT_2)
			.endIf()
		.end().build();
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes 
	 * Counter action No
	 * Different devs Yes ( in Condition )
	 * 
	 * -> VALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase8() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = ScriptBuilderTemplate.blockIfOneAction(TSENSOR_5, GREATER_OR_EQUAL, 30, TURN_ON, LIGHT_2);
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = ScriptBuilderTemplate.blockIfOneAction(TSENSOR_6, GREATER_OR_EQUAL, 35, TURN_ON, LIGHT_2);
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes 
	 * Counter action No
	 * Different devs Yes ( in Action )
	 * 
	 * -> VALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase8_1() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = ScriptBuilderTemplate.blockIfOneAction(TSENSOR_5, GREATER_OR_EQUAL, 30, TURN_ON, LIGHT_2);
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = ScriptBuilderTemplate.blockIfOneAction(TSENSOR_5, GREATER_OR_EQUAL, 35, TURN_ON, LIGHT_3);
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range Yes 
	 * Counter action Yes
	 * Different devs Yes
	 * 
	 * -> VALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase9() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = ScriptBuilderTemplate.blockIfOneAction(TSENSOR_5, GREATER_OR_EQUAL, 30, TURN_ON, LIGHT_2);
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = ScriptBuilderTemplate.blockIfOneAction(TSENSOR_5, GREATER_OR_EQUAL, 35, TURN_OFF, LIGHT_3);
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range No 
	 * Counter action Yes
	 * Different devs Yes ( in Action )
	 * 
	 * -> VALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase1() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = ScriptBuilderTemplate.blockIfOneAction(TSENSOR_5, LESS_THAN, 30, TURN_ON, LIGHT_2);
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = ScriptBuilderTemplate.blockIfOneAction(TSENSOR_5, GREATER_OR_EQUAL, 35, TURN_OFF, LIGHT_3);
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	@Test
	public void testCase12() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = ScriptBuilderTemplate.blockIfOneAction(TSENSOR_5, LESS_THAN, 30, TURN_ON, LIGHT_2);
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = new ScriptBuilder()
		.begin()
			.FromTo("05:00", "23:00")
				.action(TURN_OFF, LIGHT_2)
			.endFromTo()
		.end().build();
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * VALID
	 * @throws ParseException
	 * @throws NotSupportedException
	 * @throws ConflictConditionException
	 */
	@Test
	public void testCase12_3() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.FromTo("04:00", "12:00")
				.If(TSENSOR_5, LESS_THAN, 35.5f)
					.action(TURN_ON, LIGHT_2)
				.endIf()
			.endFromTo()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = new ScriptBuilder()
		.begin()
			.FromTo("05:00", "23:00")
				.action(TURN_OFF, LIGHT_2)
			.endFromTo()
		.end().build();
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	@Test
	public void testCase12_1() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.FromTo("04:00", "12:00")
				.action(TURN_ON, LIGHT_2)
			.endFromTo()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = new ScriptBuilder()
		.begin()
			.FromTo("05:00", "23:00")
				.action(TURN_OFF, LIGHT_2)
			.endFromTo()
		.end().build();
		existedScritps.add(existedScript);
		
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	@Test
	public void testCase12_2() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = new ScriptBuilder()
		.begin()
			.FromTo("04:00", "12:00")
				.action(TURN_ON, LIGHT_2)
			.endFromTo()
		.end().build();
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = new ScriptBuilder()
		.begin()
			.FromTo("12:01", "23:00")
				.action(TURN_OFF, LIGHT_2)
			.endFromTo()
		.end().build();
		existedScritps.add(existedScript);
		
		boolean expectedResult = false;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	/**
	 * Conflict range No 
	 * Counter action Yes
	 * Different devs Yes ( in Condition )
	 * 
	 * -> VALID
	 * @throws ParseException 
	 * @throws ConflictConditionException 
	 * @throws NotSupportedException 
	 */
	@Test
	public void testCase1_1() throws ParseException, NotSupportedException, ConflictConditionException{
		String input = ScriptBuilderTemplate.blockIfOneAction(TSENSOR_6, LESS_THAN, 30, TURN_ON, LIGHT_3);
		
		List<String> existedScritps = new ArrayList<>();
		String existedScript = ScriptBuilderTemplate.blockIfOneAction(TSENSOR_5, GREATER_OR_EQUAL, 35, TURN_OFF, LIGHT_3);
		existedScritps.add(existedScript);
		
		boolean expectedResult = true;
		runTestScriptValidation(input, existedScritps, expectedResult);
	}
	
	private void runTestScriptValidation(String inputScript, List<String> existedScripts, boolean expectedResult) throws ParseException, NotSupportedException, ConflictConditionException{
		
		Pair<Scenario,List<Scenario>> pairInputAndExistedScenarios = scriptToScenario(inputScript,existedScripts);
		Scenario inputScenario = pairInputAndExistedScenarios.getFirst();
		List<Scenario> existedScenarios = pairInputAndExistedScenarios.getSecond();
		
		
		boolean isValidate = scenarioService.isScenarioValidate(inputScenario, existedScenarios);
		System.out.println("Script is validated ? -> " + isValidate);
		assertThat(isValidate, is(expectedResult));
	}
	
	private Pair<Scenario,List<Scenario>> scriptToScenario(String inputScript, List<String> existedScripts) throws ParseException, NotSupportedException, ConflictConditionException{
		Scenario inputScenario = scenarioService.JSONToScenario(inputScript);
		List<Scenario> existedScenarios = new ArrayList<>();
		for (String existedScript : existedScripts) {
			Scenario existedScenario = scenarioService.JSONToScenario(existedScript);
			if( scenarioService.isScenarioValidate(existedScenario, null) )
				existedScenarios.add(existedScenario);
		}
		
		return new Pair<>(inputScenario, existedScenarios);
	}
}
