package com.hcmut.smarthome.model;

import java.io.Serializable;
import java.util.List;

public class Home implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String name;
	
	private String address;
	
	private String description;
	
	private boolean enabled;
	
	private Mode currentMode;
	
	private List<Mode> modes;
	
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
	public Mode getCurrentMode() {
		return currentMode;
	}
	public void setCurrentMode(Mode currentMode) {
		this.currentMode = currentMode;
	}
	public List<Mode> getModes() {
		return modes;
	}
	public void setModes(List<Mode> modes) {
		this.modes = modes;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
