package com.hcmut.smarthome.utils.test;

import static com.hcmut.smarthome.utils.ConstantUtil.GREATER_THAN;
import static com.hcmut.smarthome.utils.ConstantUtil.LESS_THAN;
import static com.hcmut.smarthome.utils.ConstantUtil.TOGGLE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hcmut.smarthome.utils.ScriptBuilder;

@ContextConfiguration( locations = { "classpath:ApplicationContext.xml", "classpath:spring-security.xml" } )
@RunWith(SpringJUnit4ClassRunner.class)
public class ScriptBuilderTest {
	
	private static final int LIGHT_2 = 2;
	private static final int LIGHT_3 = 3;
	private static final int TSENSOR_5 = 5;
	private static final int TSENSOR_6 = 6;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	@Rollback
	public void parse_code_as_string_should_work() throws Exception {
		String codeToParse = "configHome(1).If('Light sensor nead ground','>=',35.5).action('TurnOn','light 2').endIf()";
		String result = ScriptBuilder.parseFromCodeAsString(codeToParse, 1);
		String expected = "[['If',['7','>=','35.5'],[['TurnOn','3']]]]";
		assertThat(result, is(expected ));
	}
	
	@Test
	@Rollback
	public void parse_code_as_string_should_work1() throws Exception {
		String codeToParse = new ScriptBuilder().configHome(1)
		.begin()
			.If("temp 1 sensor",">=",35.5)
				.action("TurnOn","light 2")
			.endIf()
		.end().build();
		String expected = "[['If',['5','>=','35.5'],[['TurnOn','3']]]]";
		assertThat(codeToParse, is(expected ));
	}
	
	@Test
	public void test_If_clause_one_action() throws Exception {
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
	public void test_If_clause_two_actions() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.endIf()
		.end().build();
		
		String expected = "[['If',['5','>','35.5'],[['Toggle','2'],['Toggle','3']]]]";
		assertThat(script, is(expected));
	}
	
	@Test
	public void test_If_clause_two_actions_Else_clause_one_action() throws Exception {
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
	public void test_If_clause_two_actions_Else_clause_two_actions() throws Exception {
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
	public void test_If_clause_two_actions_Else_clause_has_another_If_clause_contain_one_action() throws Exception {
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
	public void test_If_clause_two_actions_Else_clause_has_one_action_and_another_If_clause() throws Exception {
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
	public void test_If_clause_two_actions_Else_clause_has_another_If_clause_and_one_action() throws Exception {
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
	public void test_one_action_and_If_clause() throws Exception {
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
	public void test_one_action_and_If_clause_and_another_action_in_same_level() throws Exception {
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
	public void test_action_IfElse_clause_with_multiple_actions_inside_and_another_action_in_same_level() throws Exception {
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
	
	@Test
	public void test_If_clause_then_FromTo_clause() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.FromTo("05:00", "10:00")
					.action(TOGGLE, LIGHT_2)
				.endFromTo()
			.endIf()
		.end().build();
		String expected = "[['If',['5','>','35.5'],[['FromTo','05:00','10:00',[['Toggle','2']]]]]]"; 
		
		assertThat(script, is(expected));
	}
	
	@Test
	public void test_ifAndElse() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
			.and(TSENSOR_5, LESS_THAN, 48f)
				.action(TOGGLE, LIGHT_2)
			.Else()
				.action(TOGGLE, LIGHT_3)
			.endIf()
		.end().build();
		
		String expected = "[['If',['5','>','35.5'],[['If',['5','<','48.0'],[['Toggle','2']],[['Toggle','3']]]],[['Toggle','3']]]]";
		
		assertThat(script, is(expected));
	}
	
	@Test
	public void test_ifAndElse_with_more_than_one_action() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
			.and(TSENSOR_5, LESS_THAN, 48f)
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.Else()
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.endIf()
		.end().build();
		
		String expected = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
				.If(TSENSOR_5, LESS_THAN, 48f)
					.action(TOGGLE, LIGHT_2)
					.action(TOGGLE, LIGHT_3)
				.Else()
					.action(TOGGLE, LIGHT_2)
					.action(TOGGLE, LIGHT_3)
				.endIf()
			.Else()
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.endIf()
		.end().build();
		
		assertThat(script, is(expected));
	}
	
	@Test
	public void test_one_if_clause_and_one_and_clause() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
			.and(TSENSOR_5, LESS_THAN, 48f)
				.action(TOGGLE, LIGHT_2)
			.endIf()
		.end().build();
		
		String expected = "[['If',['5','>','35.5'],[['If',['5','<','48.0'],[['Toggle','2']]]]]]";
		
		assertThat(script, is(expected));
	}
	
	@Test
	public void test_one_if_clause_and_one_and_clause_with_more_than_one_action() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
			.and(TSENSOR_5, LESS_THAN, 48f)
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.endIf()
		.end().build();
		
		String expected = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
				.If(TSENSOR_5, LESS_THAN, 48f)
					.action(TOGGLE, LIGHT_2)
					.action(TOGGLE, LIGHT_3)
				.endIf()
			.endIf()
		.end().build();
		
		assertThat(script, is(expected));
	}
	
	@Test
	public void test_one_if_clause_and_two_and_clauses() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
			.and(TSENSOR_5, LESS_THAN, 48f)
			.and(TSENSOR_6, GREATER_THAN, 35)
				.action(TOGGLE, LIGHT_2)
			.endIf()
		.end().build();
		
		String expected = "[['If',['5','>','35.5'],[['If',['5','<','48.0'],[['If',['6','>','35'],[['Toggle','2']]]]]]]]";
		
		assertThat(script, is(expected));
	}
	
	@Test
	public void test_one_if_clause_and_two_and_clauses_with_more_than_one_action() throws Exception {
		String script = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
			.and(TSENSOR_5, LESS_THAN, 48f)
			.and(TSENSOR_6, GREATER_THAN, 35)
				.action(TOGGLE, LIGHT_2)
				.action(TOGGLE, LIGHT_3)
			.endIf()
		.end().build();
		
		String expected = new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
				.If(TSENSOR_5, LESS_THAN, 48f)
					.If(TSENSOR_6, GREATER_THAN, 35)
						.action(TOGGLE, LIGHT_2)
						.action(TOGGLE, LIGHT_3)
					.endIf()
				.endIf()
			.endIf()
		.end().build();
		
		assertThat(script, is(expected));
	}
	
	@Test
	public void should_throw_error_when_And_clause_followed_by_endFromTo() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.FromTo("05:00", "10:00")
					.action(TOGGLE, LIGHT_2)
				.endFromTo()
				.and(TSENSOR_5,GREATER_THAN,35.5f)
			.endIf()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_And_clause_followed_by_endIf() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.If(TSENSOR_5,GREATER_THAN,35.5f)
				.FromTo("05:00", "10:00")
					.action(TOGGLE, LIGHT_2)
				.endFromTo()
			.endIf()
			.and(TSENSOR_5,GREATER_THAN,35.5f)
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_If_clause_but_no_endIf_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
			.and(TSENSOR_5, LESS_THAN, 48f)
				.action(TOGGLE, LIGHT_2)
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_And_clause_but_no_If_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.and(TSENSOR_5, LESS_THAN, 48f)
				.action(TOGGLE, LIGHT_2)
			.endIf()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_And_clause_followed_by_Else_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
			.and(TSENSOR_5, LESS_THAN, 48f)
				.action(TOGGLE, LIGHT_2)
			.Else()
			.and(TSENSOR_5, LESS_THAN, 48f)
				.action(TOGGLE, LIGHT_2)
			.endIf()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_And_clause_followed_by_FromTo_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.FromTo("05:00", "10:00")
			.and(TSENSOR_5, LESS_THAN, 48f)
				.action(TOGGLE, LIGHT_2)
			.endFromTo()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_FromTo_clause_but_no_endFromTo_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.FromTo("05:00", "10:00")
				.action(TOGGLE, LIGHT_2)
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_FromTo_clause_but_use_endIf_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.FromTo("05:00", "10:00")
				.action(TOGGLE, LIGHT_2)
			.endIf()
		.end().build();
	}
	
	// TODO Just use one endBlock() is enough
	//@Test
	public void should_throw_error_when_script_has_If_clause_but_use_endFromTo_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
				.action(TOGGLE, LIGHT_2)
			.endFromTo()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_And_clause_followed_by_use_Else_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
				.action(TOGGLE, LIGHT_2)
			.Else()
			.and(TSENSOR_5, GREATER_THAN, 5.5f)
				.action(TOGGLE, LIGHT_3)
			.endIf()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_no_actions_in_If_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
			.endIf()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_no_actions_in_FromTo_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.FromTo("05:00", "10:00")
			.endFromTo()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_no_actions_in_Else_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
				.action(TOGGLE, LIGHT_2)
			.Else()
			.endIf()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_no_actions_in_If_clause_and_in_Else_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
			.Else()
			.endIf()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_And_clause_followed_by_action_in_If_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
				.action(TOGGLE, LIGHT_2)
				.and(TSENSOR_5, GREATER_THAN, 35.5f)
			.endIf()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_And_clause_between_two_actions_in_If_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
				.action(TOGGLE, LIGHT_2)
				.and(TSENSOR_5, GREATER_THAN, 35.5f)
				.action(TOGGLE, LIGHT_2)
			.endIf()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_And_clause_followed_by_action_in_Else_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
				.action(TOGGLE, LIGHT_2)
			.Else()
				.action(TOGGLE, LIGHT_2)
				.and(TSENSOR_5, GREATER_THAN, 35.5f)
			.endIf()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_And_clause_between_two_actions_in_Else_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.If(TSENSOR_5, GREATER_THAN, 35.5f)
				.action(TOGGLE, LIGHT_2)
			.Else()
				.action(TOGGLE, LIGHT_2)
				.and(TSENSOR_5, GREATER_THAN, 35.5f)
				.action(TOGGLE, LIGHT_2)
			.endIf()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_use_Else_clause_with_FromTo_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.FromTo("05:00", "04:00")
				.action(TOGGLE, LIGHT_2)
			.Else()
				.action(TOGGLE, LIGHT_2)
			.endFromTo()
		.end().build();
	}
	
	// TODO : Do we need to check this case?
	//@Test
	public void should_throw_error_when_script_has_FromTo_clause_and_To_value_less_than_From_value() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.FromTo("05:00", "04:00")
				.action(TOGGLE, LIGHT_2)
			.endFromTo()
		.end().build();
	}
	
	@Test
	public void should_throw_error_when_script_has_no_actions_inside_If_clause() throws Exception {
		expectedException.expect(Exception.class);
		
		new ScriptBuilder()
		.begin()
			.FromTo("05:00", "04:00")
				.If(TSENSOR_5, GREATER_THAN, 35.5f)
				.endIf()
			.endFromTo()
		.end().build();
	}
}
