package com.hcmut.smarthome.model;

import java.io.Serializable;

public class Home implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String name;
	
	private String address;
	
	private String description;
	
	public Home() {
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
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
