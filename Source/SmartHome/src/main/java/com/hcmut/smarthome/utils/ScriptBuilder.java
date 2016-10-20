<<<<<<< HEAD
package com.hcmut.smarthome.utils;

import static com.hcmut.smarthome.utils.ConstantUtil.*;

import java.io.FileNotFoundException;
import java.util.Stack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptBuilder {
	private static final String SCRIPT_BUILDER_TEMPLATE_CLASS = "com.hcmut.smarthome.utils.ScriptBuilder.ScriptBuilderTemplate";
	private static final String SCRIPT_BUILDER_CLASS = "com.hcmut.smarthome.utils.ScriptBuilder";
	private static final String ENGINE_NAME = "JavaScript";
    private static ScriptEngineManager scriptEngineManager; 
    private static ScriptEngine scriptEngine;
    private static String templateJSCode;
    
	private StringBuilder builder;
	private Stack<Integer> stack;

	static{
		if( scriptEngineManager == null){
			scriptEngineManager = new ScriptEngineManager(); 
		}
		if( scriptEngine == null ){
			scriptEngine = scriptEngineManager.getEngineByName(ENGINE_NAME);
		}
		if( templateJSCode == null ){
			templateJSCode = new StringBuilder()
				.append("var ScriptBuilderEngine = new JavaImporter(%s,%s);")
		        .append("with(ScriptBuilderEngine){")
		        .append("var script = %s;}").toString();
		}
	}
	
	public ScriptBuilder(){
		builder = new StringBuilder();
		stack = new Stack<>();
	}
	
	public ScriptBuilder begin(){
		builder.append("[");
		stack.push(0);
		return this;
	}
	
	public ScriptBuilder end(){
		builder.append("]");
		stack.pop();
		return this;
	}
	
	public ScriptBuilder If(Object deviceId, String operator, Object value){
		addNewControlBlockToCurrentOne();
		builder.append(String.format("['If',['%s','%s','%s']",deviceId,operator,value));
		return then();
	}
	
	public ScriptBuilder FromTo(Object fromValue , Object toValue){
		addNewControlBlockToCurrentOne();
		builder.append(String.format("['FromTo','%s','%s'",fromValue,toValue));
		return then();
	}

	private void addNewControlBlockToCurrentOne() {
		int nbrBlock = increaseNbrBlock();
		if( nbrBlock > 1 )
			builder.append(",");
	}

	private int increaseNbrBlock() {
		int nbrBlock = stack.pop() + 1;
		stack.push(nbrBlock);
		return nbrBlock;
	}
	
	private ScriptBuilder then(){
		builder.append(",");
		return begin();
	}
	
	public ScriptBuilder action(String actionName, int deviceId){
		addNewControlBlockToCurrentOne();
		builder.append(String.format("['%s','%s']",actionName,deviceId));
		
		return this;
	}
	
	public ScriptBuilder endIf(){
		builder.append("]");
		return end();
	}
	
	public ScriptBuilder endFromTo(){
		return endIf();
	}
	
	public ScriptBuilder Else(){
		return end().then();
	}
	
	public String build(){
		return builder.toString();
	}
	
	public static String parseFromCodeAsString(String codeToParse) throws ScriptException{

		final String JSCode = String.format(templateJSCode, 
				SCRIPT_BUILDER_CLASS, 
				SCRIPT_BUILDER_TEMPLATE_CLASS, 
				codeToParse );
        
        // evaluate JavaScript code from given file - specified by first argument
        scriptEngine.eval(JSCode);
		
        Object resultScript = scriptEngine.get("script");
        if( resultScript == null )
        	return "";
        
		return resultScript.toString();
	}
	
	public static class ScriptBuilderTemplate{
		public static String blockIfOneAction(Object deviceIdCondition, String operator , Object value , String actionName, int deviceIdAction){
			String script = new ScriptBuilder()
			.begin()
				.If(deviceIdCondition,operator,value)
					.action(actionName, deviceIdAction)
				.endIf()
			.end().build();
			
			return script;
		}
		
		public static String blockIfElseOneAction(Object deviceIdCondition, String operator , Object value , String ifActionName, int deviceIdIfAction, String elseActionName, int deviceIdElseAction){
			String script = new ScriptBuilder()
			.begin()
				.If(deviceIdCondition,operator,value)
					.action(ifActionName, deviceIdIfAction)
				.Else()
					.action(elseActionName,deviceIdElseAction)
				.endIf()
			.end().build();
			
			return script;
		}
	}
	
	public static void main(String[] args) throws ScriptException, FileNotFoundException {
		ScriptBuilder scriptBuilder = new ScriptBuilder()
		.begin()
			.FromTo(4, 10)
				.action("Toggle", 2)
			.endFromTo()
			.If("Temperature Sensor",">",35.5f)
				.action("Toggle", 2)
				.action("Toggle", 3)
			.Else()
				.If("Temperature Sensor",">",35.5f)
					.action("Toggle", 2)
					.action("Toggle", 3)
				.endIf()
				.action("Toggle", 2)
			.endIf()
			.action("Toggle", 2)
		.end();
		
		String script = scriptBuilder.build();
		
		System.out.println(script);
		
		System.out.println(ScriptBuilderTemplate.blockIfOneAction(5, GREATER_OR_EQUAL, 30, TURN_ON, 3));
		
		String s = ScriptBuilder.parseFromCodeAsString("ScriptBuilderTemplate.blockIfOneAction(5, \">=\", 30, \"TurnOn\", 3)");
		System.out.println(s);
	}
}
=======
package com.hcmut.smarthome.utils;

import static com.hcmut.smarthome.utils.ConstantUtil.*;

import java.io.FileNotFoundException;
import java.util.Stack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptBuilder {
	private static final String SCRIPT_BUILDER_TEMPLATE_CLASS = "com.hcmut.smarthome.utils.ScriptBuilder.ScriptBuilderTemplate";
	private static final String SCRIPT_BUILDER_CLASS = "com.hcmut.smarthome.utils.ScriptBuilder";
	private static final String ENGINE_NAME = "JavaScript";
    private static ScriptEngineManager scriptEngineManager; 
    private static ScriptEngine scriptEngine;
    private static String templateJSCode;
    
	private StringBuilder builder;
	private Stack<Integer> stack;

	static{
		if( scriptEngineManager == null){
			scriptEngineManager = new ScriptEngineManager(); 
		}
		if( scriptEngine == null ){
			scriptEngine = scriptEngineManager.getEngineByName(ENGINE_NAME);
		}
		if( templateJSCode == null ){
			templateJSCode = new StringBuilder()
				.append("var ScriptBuilderEngine = new JavaImporter(%s,%s);")
		        .append("with(ScriptBuilderEngine){")
		        .append("var script = %s;}").toString();
		}
	}
	
	public ScriptBuilder(){
		builder = new StringBuilder();
		stack = new Stack<>();
	}
	
	public ScriptBuilder begin(){
		builder.append("[");
		stack.push(0);
		return this;
	}
	
	public ScriptBuilder end(){
		builder.append("]");
		stack.pop();
		return this;
	}
	
	public ScriptBuilder If(Object deviceId, String operator, Object value){
		int nbrBlock = stack.pop() + 1;
		stack.push(nbrBlock);
		if( nbrBlock > 1 )
			builder.append(",");
		builder.append(String.format("['If',['%s','%s','%s']",deviceId,operator,value));
		return then();
	}
	
	private ScriptBuilder then(){
		builder.append(",");
		return begin();
	}
	
	public ScriptBuilder action(String actionName, int deviceId){
		int nbrBlock = stack.pop() + 1;
		stack.push(nbrBlock);
		if( nbrBlock > 1 )
			builder.append(",");
		builder.append(String.format("['%s','%s']",actionName,deviceId));
		
		return this;
	}
	
	public ScriptBuilder endIf(){
		int nbrBlock = stack.pop() + 1;
		stack.push(nbrBlock);
		
		builder.append("]");
		return end();
	}
	
	public ScriptBuilder Else(){
		return end().then();
	}
	
	public String build(){
		return builder.toString();
	}
	
	public static String parseFromCodeAsString(String codeToParse) throws ScriptException{

		final String JSCode = String.format(templateJSCode, 
				SCRIPT_BUILDER_CLASS, 
				SCRIPT_BUILDER_TEMPLATE_CLASS, 
				codeToParse );
        
        // evaluate JavaScript code from given file - specified by first argument
        scriptEngine.eval(JSCode);
		
        Object resultScript = scriptEngine.get("script");
        if( resultScript == null )
        	return "";
        
		return resultScript.toString();
	}
	
	public static class ScriptBuilderTemplate{
		public static String ifHasOneAction(Object deviceIdCondition, String operator , Object value , String actionName, int deviceIdAction){
			String script = new ScriptBuilder()
			.begin()
				.If(deviceIdCondition,operator,value)
					.action(actionName, deviceIdAction)
				.endIf()
			.end().build();
			
			return script;
		}
		
		public static String ifElseHasOneAction(Object deviceIdCondition, String operator , Object value , String ifActionName, int deviceIdIfAction, String elseActionName, int deviceIdElseAction){
			String script = new ScriptBuilder()
			.begin()
				.If(deviceIdCondition,operator,value)
					.action(ifActionName, deviceIdIfAction)
				.Else()
					.action(elseActionName,deviceIdElseAction)
				.endIf()
			.end().build();
			
			return script;
		}
	}
	
	public static void main(String[] args) throws ScriptException, FileNotFoundException {
		ScriptBuilder scriptBuilder = new ScriptBuilder()
		.begin()
			.action("Toggle", 2)
			.If("Temperature Sensor",">",35.5f)
				.action("Toggle", 2)
				.action("Toggle", 3)
			.Else()
				.If("Temperature Sensor",">",35.5f)
					.action("Toggle", 2)
					.action("Toggle", 3)
				.endIf()
				.action("Toggle", 2)
			.endIf()
			.action("Toggle", 2)
		.end();
		
		String script = scriptBuilder.build();
		
		System.out.println(script);
		
		System.out.println(ScriptBuilderTemplate.ifHasOneAction(5, GREATER_OR_EQUAL, 30, TURN_ON, 3));
		
		String s = ScriptBuilder.parseFromCodeAsString("ScriptBuilderTemplate.ifHasOneAction(5, \">=\", 30, \"TurnOn\", 3)");
		System.out.println(s);
	}
}
>>>>>>> master
