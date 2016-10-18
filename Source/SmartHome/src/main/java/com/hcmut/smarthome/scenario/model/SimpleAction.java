package com.hcmut.smarthome.scenario.model;

import java.util.Objects;
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

	@Override
	public String toString() {
		return String.format("[%s %s]", getName(),getDeviceId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name,this.deviceId);
	}

	@Override
	public boolean equals(Object o) {
		
		if( o == this )
			return true;
		
		if( o == null || !(o instanceof SimpleAction) )
			return false;
		
		SimpleAction that = (SimpleAction) o;
		
		return Objects.equals(this.name, that.name)
				&& Objects.equals(this.deviceId, that.deviceId);
	}

}
