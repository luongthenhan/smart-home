package com.hcmut.smarthome.utils;

import java.util.Stack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.hcmut.smarthome.service.IDeviceService;

public class ScriptBuilder {
	private static final String SCRIPT_BUILDER_TEMPLATE_CLASS = "com.hcmut.smarthome.utils.ScriptBuilder.ScriptBuilderTemplate";
	private static final String SCRIPT_BUILDER_CLASS = "com.hcmut.smarthome.utils.ScriptBuilder";
	private static final String ENGINE_NAME = "JavaScript";
	private static final int NO_DEFINED_HOME_ID = -1;
    private static ScriptEngineManager scriptEngineManager; 
    private static ScriptEngine scriptEngine;
    private static String templateJSCode;
    
	private StringBuilder builder;
	private Stack<Integer> stack;
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
		        .append("var script = new ScriptBuilder().configHome(%d).%s.build();}").toString();
		}
	}
	
	public ScriptBuilder configHome(int homeId){
		this.homeId = homeId;
		return this;
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
	
	public ScriptBuilder If(String deviceName, String operator, Object value) throws NotFoundException{
		int deviceId = deviceService.getDeviceIdGivenNameAndHomeId(homeId, deviceName);
		return If(deviceId, operator, value);
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
	
	public ScriptBuilder action(String actionName, String deviceName) throws NotFoundException{
		int deviceId = deviceService.getDeviceIdGivenNameAndHomeId(homeId, deviceName);
		return action(actionName, deviceId);
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

	public static void setDeviceService(IDeviceService deviceService) {
		ScriptBuilder.deviceService = deviceService;
	}

}
