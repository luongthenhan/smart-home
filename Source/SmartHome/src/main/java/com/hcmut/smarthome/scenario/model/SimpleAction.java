package com.hcmut.smarthome.scenario.model;

import java.util.function.Consumer;

public class SimpleAction implements IBlock, IAction{
	private Object value;
	private String name;
	private int deviceId;
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

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

}
