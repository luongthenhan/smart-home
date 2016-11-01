package com.hcmut.smarthome.utils;

import java.util.Stack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.hcmut.smarthome.service.IDeviceService;

public class ScriptBuilder {
	private static final String INPUT_SCRIPT_HAS_INCORRECT_SYNTAX = "Input script has incorrect syntax";
	private static final String SCRIPT_BUILDER_TEMPLATE_CLASS = "com.hcmut.smarthome.utils.ScriptBuilder.ScriptBuilderTemplate";
	private static final String SCRIPT_BUILDER_CLASS = "com.hcmut.smarthome.utils.ScriptBuilder";
	private static final String ENGINE_NAME = "JavaScript";
	private static final int NO_DEFINED_HOME_ID = -1;
	private static final int INITIAL_VALUE = 0;
	private static final Short BLOCK_DEFAULT = 0;
	private static final Short BLOCK_IF = 1;
	private static final Short BLOCK_FROM_TO = 2;
	
    private static ScriptEngineManager scriptEngineManager; 
    private static ScriptEngine scriptEngine;
    private static String templateJSCode;
    
	private StringBuilder builder;
	private Stack<Pair<Integer,Short>> stack;
	private Stack<Integer> stackAndCondition;
	private int homeId;
	
	private static IDeviceService deviceService ;

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
		        //.append("var script = %s;}").toString();
		        .append("var script = new ScriptBuilder().configHome(%d).begin().%s.end().build();}").toString();
		}
	}
	
	public ScriptBuilder configHome(int homeId){
		this.homeId = homeId;
		return this;
	}
	
	public ScriptBuilder(){
		builder = new StringBuilder();
		stack = new Stack<>();
		stackAndCondition = new Stack<>();
	}
	
	public ScriptBuilder begin(){
		return begin(BLOCK_DEFAULT);
	}
	
	public ScriptBuilder begin(short blockType){
		builder.append("[");
		stack.push(new Pair<>(0,blockType));
		return this;
	}
	
	public ScriptBuilder end() throws Exception{
		builder.append("]");
		
		if( stack.isEmpty() || stack.pop().getFirst() == INITIAL_VALUE)
			throw new Exception(INPUT_SCRIPT_HAS_INCORRECT_SYNTAX);

		return this;
	}
	
	public ScriptBuilder If(Object deviceId, String operator, Object value){
		addNewControlBlockToCurrentOne(BLOCK_IF);
		builder.append(String.format("['If',['%s','%s','%s']",deviceId,operator,value));
		return then(BLOCK_IF);
	}
	
	public ScriptBuilder If(String deviceName, String operator, Object value) throws NotFoundException{
		int deviceId = deviceService.getDeviceIdGivenNameAndHomeId(homeId, deviceName);
		return If(deviceId, operator, value);
	}
	
	public ScriptBuilder and(Object deviceId, String operator, Object value) throws Exception{
		
		if( stack.peek().getFirst() != INITIAL_VALUE )
			throw new Exception(INPUT_SCRIPT_HAS_INCORRECT_SYNTAX);
		
		if( BLOCK_IF.equals(stack.peek().getSecond()) ){
			stackAndCondition.push(INITIAL_VALUE);
			return If(deviceId, operator, value);
		}
		else throw new Exception(INPUT_SCRIPT_HAS_INCORRECT_SYNTAX);
	}
	
	public ScriptBuilder and(String deviceName, String operator, Object value) throws Exception{
		int deviceId = deviceService.getDeviceIdGivenNameAndHomeId(homeId, deviceName);
		return and(deviceId, operator, value);
	}
	
	// TODO Do we need to check from time and to time ??
	public ScriptBuilder FromTo(Object fromValue , Object toValue){
		addNewControlBlockToCurrentOne(BLOCK_FROM_TO);
		builder.append(String.format("['FromTo','%s','%s'",fromValue,toValue));
		return then(BLOCK_FROM_TO);
	}

	private void addNewControlBlockToCurrentOne(short blockType) {
		int nbrBlock = increaseNbrBlock(blockType);
		if( nbrBlock > 1 )
			builder.append(",");
	}

	private int increaseNbrBlock(short blockType) {
		int nbrBlock = stack.peek().getFirst() + 1;
		stack.peek().setFirst(nbrBlock);
		return nbrBlock;
	}
	
	private ScriptBuilder then(short blockType){
		builder.append(",");
		return begin(blockType);
	}
	
	public ScriptBuilder action(String actionName, int deviceId){
		addNewControlBlockToCurrentOne(BLOCK_DEFAULT);
		builder.append(String.format("['%s','%s']",actionName,deviceId));
		
		return this;
	}
	
	public ScriptBuilder action(String actionName, String deviceName) throws Exception{
		int deviceId = deviceService.getDeviceIdGivenNameAndHomeId(homeId, deviceName);
		return action(actionName, deviceId);
	}
	
	public ScriptBuilder endIf() throws Exception{
		if( stack.isEmpty() || BLOCK_FROM_TO.equals(stack.peek().getSecond()) )
			throw new Exception(INPUT_SCRIPT_HAS_INCORRECT_SYNTAX);
		builder.append("]");
		return end().takeCareAndConditionIfAny();
	}
	
	private ScriptBuilder takeCareAndConditionIfAny() throws Exception{
		// Has 'And' Clause
		if( !stackAndCondition.isEmpty() ){
			int startIndex = stackAndCondition.pop();
			
			// But no has 'Else' clause 
			if( startIndex == INITIAL_VALUE )
				return endIf();
			
			int endIndex = builder.length();
			builder.append(builder.substring(startIndex, endIndex));
			stack.pop();
		}
		
		return this;
	}
	
	public ScriptBuilder endFromTo() throws Exception{
		if( stack.isEmpty() || !BLOCK_FROM_TO.equals(stack.peek().getSecond()) )
			throw new Exception(INPUT_SCRIPT_HAS_INCORRECT_SYNTAX);
		builder.append("]");
		return end();
	}
	
	// Append "],["n
	public ScriptBuilder Else() throws Exception{
		if( BLOCK_FROM_TO.equals(stack.peek().getSecond()) )
			throw new Exception(INPUT_SCRIPT_HAS_INCORRECT_SYNTAX);
		
		if( !stackAndCondition.isEmpty() ){
			stackAndCondition.pop();
			stackAndCondition.push(builder.length());
		}
		return end().then(BLOCK_DEFAULT);
	}
	
	public String build() throws Exception{
		if( !stack.isEmpty() || !stackAndCondition.isEmpty())
			throw new Exception(INPUT_SCRIPT_HAS_INCORRECT_SYNTAX);
		
		return builder.toString();
	}
	
	public static String parseFromCodeAsString(String codeToParse, int homeId) throws ScriptException{

		final String JSCode = String.format(templateJSCode, 
				SCRIPT_BUILDER_CLASS, 
				SCRIPT_BUILDER_TEMPLATE_CLASS, 
				homeId,
				codeToParse );
        
        // evaluate JavaScript code from given file - specified by first argument
        scriptEngine.eval(JSCode);
		
        Object resultScript = scriptEngine.get("script");
        if( resultScript == null )
        	return "";
        
		return resultScript.toString();
	}
	
	public static String parseFromCodeAsString(String codeToParse) throws ScriptException{

		final String JSCode = String.format(templateJSCode, 
				SCRIPT_BUILDER_CLASS, 
				SCRIPT_BUILDER_TEMPLATE_CLASS, 
				NO_DEFINED_HOME_ID,
				codeToParse );
        
        // evaluate JavaScript code from given file - specified by first argument
        scriptEngine.eval(JSCode);
		
        Object resultScript = scriptEngine.get("script");
        if( resultScript == null )
        	return "";
        
		return resultScript.toString();
	}
	
	public static class ScriptBuilderTemplate{
		public static String blockIfOneAction(Object deviceIdCondition, String operator , Object value , String actionName, int deviceIdAction) throws Exception{
			String script = new ScriptBuilder()
			.begin()
				.If(deviceIdCondition,operator,value)
					.action(actionName, deviceIdAction)
				.endIf()
			.end().build();
			
			return script;
		}
		
		public static String blockIfElseOneAction(Object deviceIdCondition, String operator , Object value , String ifActionName, int deviceIdIfAction, String elseActionName, int deviceIdElseAction) throws Exception{
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

	public static void setDeviceService(IDeviceService deviceService) {
		ScriptBuilder.deviceService = deviceService;
	}

}
