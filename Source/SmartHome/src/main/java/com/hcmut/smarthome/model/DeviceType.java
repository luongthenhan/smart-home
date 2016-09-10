package com.hcmut.smarthome.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="device_type")
public class DeviceType implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="type_name", nullable = false , length = 100)
	private String typeName;
	
	@Column(name="description", nullable = true , length = 1024)
	private String description;
	
	@Column(name="config", nullable = true , length = 45)
	private String config;
	
	@Column(name="ctrl_script", nullable = true , length = 4096)
	private String ctrlScript;
	
	public DeviceType(){
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String getCtrlScript() {
		return ctrlScript;
	}

	public void setCtrlScript(String ctrlScript) {
		this.ctrlScript = ctrlScript;
	}
	
}
