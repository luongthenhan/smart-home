package com.hcmut.smarthome.dto;

import java.util.function.Predicate;

public class Condition implements IBlock, ICondition {
	protected String name;
	protected String logicOperator;
	protected Object value;
	protected Predicate<Object> predicate;
	
	public Condition() {
		super();
	}
	
	public Condition(String name, Predicate<Object> predicate) {
		super();
		this.name = name;
		this.predicate = predicate;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public Predicate<Object> getPredicate() {
		return predicate;
	}

	public void setPredicate(Predicate<Object> predicate) {
		this.predicate = predicate;
	}

	@Override
	public boolean check(){
		return this.predicate.test(value);
	}

	public String getLogicOperator() {
		return logicOperator;
	}

	public void setLogicOperator(String logicOperator) {
		this.logicOperator = logicOperator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
