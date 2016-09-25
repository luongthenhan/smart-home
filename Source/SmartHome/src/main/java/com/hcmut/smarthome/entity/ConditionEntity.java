package com.hcmut.smarthome.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="condition")
public class ConditionEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="name", length = 45, nullable = true)
	private String name;
	
	@Column(name="script", length = 128, nullable = false)
	private String script;
	
	@Column(name="has_parameter",nullable = false)
	private boolean hasParameter = false;
	
	@ManyToOne
	@JoinColumn(name="device_type_id", nullable = false)
	private DeviceTypeEntity deviceType;

	public ConditionEntity(){
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

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public DeviceTypeEntity getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceTypeEntity deviceType) {
		this.deviceType = deviceType;
	}

	public boolean isHasParameter() {
		return hasParameter;
	}

	public void setHasParameter(boolean hasParameter) {
		this.hasParameter = hasParameter;
	}
}
