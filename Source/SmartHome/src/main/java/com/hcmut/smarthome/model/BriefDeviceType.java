package com.hcmut.smarthome.model;

import java.io.Serializable;

public class BriefDeviceType implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected int id;
	
	protected String name;
	
	protected String GPIOType;
	
	public BriefDeviceType(){
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String typeName) {
		this.name = typeName;
	}

	public String getGPIOType() {
		return GPIOType;
	}

	public void setGPIOType(String gPIOType) {
		GPIOType = gPIOType;
	}
	
}
