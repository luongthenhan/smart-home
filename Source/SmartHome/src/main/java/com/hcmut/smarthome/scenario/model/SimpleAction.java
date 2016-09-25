package com.hcmut.smarthome.scenario.model;

import java.util.function.Consumer;

public class SimpleAction implements IBlock, IAction{
	private int value;
	private String name;
	private Consumer<Object> action;
	
	public SimpleAction() {
		super();
	}

	public SimpleAction(String name, Consumer<Object> action) {
		super();
		this.name = name;
		this.action = action;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public Consumer<Object> getAction() {
		return action;
	}

	public void setAction(Consumer<Object> action) {
		this.action = action;
	}

	@Override
	public void doAction(){
		action.accept(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
