package com.hcmut.smarthome.scenario.model;

import java.util.Objects;
import java.util.function.Predicate;

import com.google.common.collect.Range;

public class Condition<C extends Comparable<? extends Object>> implements IBlock, ICondition {
	protected String name;
	protected String operator;
	protected Object value;
	protected Predicate<Object> predicate;
	protected Range<C> range;
	protected Class<C> valueClassType;
	
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

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Range<C> getRange() {
		return range;
	}

	public void setRange(Range<C> range) {
		this.range = range;
	}
	
	@Override
	public String toString() {
		return String.format("[Device %s %s %s]", getName(),getOperator(),getValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(name,operator,value);
	}

	@Override
	public boolean equals(Object o) {
		if( o == this )
			return true;
		
		if( o == null )
			return false;
		
		if( !(o instanceof Condition) )
			return false;
		
		Condition condition = (Condition) o;
		return Objects.equals(name, condition.name)
				&& Objects.equals(operator, condition.operator)
				&& Objects.equals(value, condition.value);
	}

	public Class<C> getValueClassType() {
		return valueClassType;
	}

	public void setValueClassType(Class<C> valueClassType) {
		this.valueClassType = valueClassType;
	}
	
}
