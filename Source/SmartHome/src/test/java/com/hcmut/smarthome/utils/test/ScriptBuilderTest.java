package com.hcmut.smarthome.utils.test;

import static com.hcmut.smarthome.utils.ConstantUtil.GREATER_THAN;
import static com.hcmut.smarthome.utils.ConstantUtil.TOGGLE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hcmut.smarthome.utils.ScriptBuilder;

@ContextConfiguration( locations = { "classpath:ApplicationContext.xml", "classpath:spring-security.xml" } )
@RunWith(SpringJUnit4ClassRunner.class)
public class ScriptBuilderTest {
	
	private static final int LIGHT_2 = 2;
	private static final int LIGHT_3 = 3;
	private static final int TSENSOR_5 = 5;
	
	@Test
	public void parseCodeAsStringShouldWork() throws Exception {
		String codeToParse = "begin().If('Light sensor nead ground','>=',35.5).action('TurnOn','light 2').endIf().end()";
		String result = ScriptBuilder.parseFromCodeAsString(codeToParse, 1);
		String expected = "[['If',['7','>=','35.5'],[['TurnOn','3']]]]";
		assertThat(result, is(expected ));
	}
	
	@Test
	public void testCase1() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.action(TOGGLE, LIGHT_2)
			.endIf()
		.end().build();
		
		String expected = "[['If',['5','>','35.5'],[['Toggle','2']]]]";
		assertThat(script, is(expected));
	}
	
	@Test
	public void testCase2() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.endIf()
		.end().build();
		System.out.println(script);
		String expected = "[['If',['5','>','35.5'],[['Toggle','2'],['Toggle','3']]]]";
		assertThat(script, is(expected));
	}
	
	@Test
	public void testCase3() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.Else()
				.action(TOGGLE, LIGHT_2)
			.endIf()
		.end().build();
		
		String expected = "[['If',['5','>','35.5'],[['Toggle','2'],['Toggle','3']],[['Toggle','2']]]]";
		assertThat(script, is(expected));
	}
	
	@Test
	public void testCase4() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.Else()
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.endIf()
		.end().build();
		String expected = "[['If',['5','>','35.5'],[['Toggle','2'],['Toggle','3']],[['Toggle','2'],['Toggle','3']]]]";
		assertThat(script, is(expected));
	}
	
	@Test
	public void testCase5() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.Else()
				.If(TSENSOR_5,GREATER_THAN,35.5f)
					.action(TOGGLE, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		String expected = "[['If',['5','>','35.5'],[['Toggle','2'],['Toggle','3']],[['If',['5','>','35.5'],[['Toggle','2']]]]]]";
		assertThat(script, is(expected));
	}
	
	@Test
	public void testCase6() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.Else()
				.action(TOGGLE, LIGHT_3)
				.If(TSENSOR_5,GREATER_THAN,35.5f)
					.action(TOGGLE, LIGHT_2)
				.endIf()
			.endIf()
		.end().build();
		
		String expected = "[['If',['5','>','35.5'],[['Toggle','2'],['Toggle','3']],[['Toggle','3'],['If',['5','>','35.5'],[['Toggle','2']]]]]]";
		assertThat(script, is(expected));
	}
	
	@Test
	public void testCase7() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.Else()
				.If(TSENSOR_5,GREATER_THAN,35.5f)
					.action(TOGGLE, LIGHT_2)
				.endIf()
				.action(TOGGLE, LIGHT_3)
			.endIf()
		.end().build();
		
		String expected = "[['If',['5','>','35.5'],[['Toggle','2'],['Toggle','3']],[['If',['5','>','35.5'],[['Toggle','2']]],['Toggle','3']]]]";
		assertThat(script, is(expected));
	}
	
	@Test
	public void testCase8() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.action(TOGGLE, LIGHT_3)
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.endIf()
		.end().build();
		
		String expected = "[['Toggle','3'],['If',['5','>','35.5'],[['Toggle','2'],['Toggle','3']]]]";
		assertThat(script, is(expected));
	}
	
	@Test
	public void testCase9() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.action(TOGGLE, LIGHT_3)
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.endIf()
			.action(TOGGLE, LIGHT_3)
		.end().build();
		
		String expected = "[['Toggle','3'],['If',['5','>','35.5'],[['Toggle','2'],['Toggle','3']]],['Toggle','3']]";
		assertThat(script, is(expected));
	}
	
	@Test
	public void testCase10() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.action(TOGGLE, LIGHT_2)
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.Else()
				.If(TSENSOR_5,GREATER_THAN,35.5f)
					.action(TOGGLE, LIGHT_2)
					.action(TOGGLE, LIGHT_3)
				.endIf()
				.action(TOGGLE, LIGHT_2)
			.endIf()
			.action(TOGGLE, LIGHT_2)
		.end().build();
		
		String expected = "[['Toggle','2'],['If',['5','>','35.5'],[['Toggle','2'],['Toggle','3']],[['If',['5','>','35.5'],[['Toggle','2'],['Toggle','3']]],['Toggle','2']]],['Toggle','2']]"; 
		
		assertThat(script, is(expected));
	}
	
}
