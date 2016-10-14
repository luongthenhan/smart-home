package com.hcmut.smarthome.utils;

import java.util.Stack;

public class ScriptBuilder {
	private StringBuilder builder;
	private Stack<Integer> stack;
	
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
	
	public ScriptBuilder If(String deviceId, String operator, String value){
		int nbrBlock = stack.pop() + 1;
		stack.push(nbrBlock);
		if( nbrBlock > 1 )
			builder.append(",");
		builder.append(String.format("['If',['%s','%s','%s']",deviceId,operator,value));
		return this;
	}
	
	public ScriptBuilder then(){
		builder.append(",[");
		stack.push(0);
		return this;
	}
	
	public ScriptBuilder action(String actionName, String deviceId){
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
		builder.append("]");
		return end().then();
	}
	
	public String build(){
		return builder.toString();
	}
	
	public static void main(String[] args) {
		ScriptBuilder scriptBuilder = new ScriptBuilder()
		.begin()
			.If("Temperature Sensor",">","35.5").then()
				.action("Toggle", "2")
				.action("Toggle", "3")
			.Else()
				.If("Temperature Sensor",">","35.5").then()
					.action("Toggle", "2")
					.action("Toggle", "3")
				.endIf()
				.action("Toggle", "2")
			.endIf()
		.end();
		
		String script = scriptBuilder.build();
		
		System.out.println(script);
	}
}
