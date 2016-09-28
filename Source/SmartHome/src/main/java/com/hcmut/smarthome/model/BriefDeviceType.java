package com.hcmut.smarthome.model;

import java.io.Serializable;

public class BriefDeviceType implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected int id;
	
	protected String typeName;
	
	public BriefDeviceType(){
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
