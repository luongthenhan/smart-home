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
@Table(name="device")
public class Device implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="name", nullable = false , length = 45)
	private String name;
	
	@Column(name="description", nullable = true , length = 1024)
	private String description;
	
	@Column(name="location", nullable = true , length = 100)
	private String location;
	
	@Column(name="code", nullable = true , length = 100)
	private String code;
	
	@Column(name="status", nullable = true , length = 100)
	private String status;

	@ManyToOne
	@JoinColumn(name="home_id", nullable = false)
	private Home home;
	
	@ManyToOne
	@JoinColumn(name="device_type_id", nullable = false)
	private DeviceType deviceType;
	
	@Column(name="enabled", nullable = false)
	private boolean enabled;
	
	@Column(name="gpio_info", nullable = false , length = 45)
	private int GPIOinfo;
	
	public Device() {
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
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public Home getHome() {
		return home;
	}
	public void setHome(Home home) {
		this.home = home;
	}
	
	public DeviceType getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}
	
	public int getGPIOinfo() {
		return GPIOinfo;
	}
	public void setGPIOinfo(int gPIOinfo) {
		GPIOinfo = gPIOinfo;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
