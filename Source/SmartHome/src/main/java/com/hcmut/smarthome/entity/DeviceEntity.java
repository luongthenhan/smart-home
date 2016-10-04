package com.hcmut.smarthome.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="device")
public class DeviceEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="name", nullable = false , length = 45)
	private String name;
	
	@Column(name="description", nullable = true , length = 512)
	private String description;
	
	@Column(name="location", nullable = true , length = 128)
	private String location;

	@Column(name="code", nullable = true , length = 45)
	private String code;
	
	@Column(name="timeout", nullable=true)
	private Integer timeout;
	
	@ManyToOne
	@JoinColumn(name="home_id", nullable = false)
	private HomeEntity home;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="device_type_id", nullable = false)
	private DeviceTypeEntity deviceType;
	
	@Column(name="enabled", nullable = false)
	private boolean enabled;
	
	@Column(name="gpio_pin", nullable = false)
	private int GPIOPin;
	
	@Column(name="gpio_type", nullable = true , length = 45)
	private String GPIOType;
	
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true,mappedBy="device")
	private Set<ScriptEntity> scripts;
	 
	public DeviceEntity() {
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
	public HomeEntity getHome() {
		return home;
	}
	public void setHome(HomeEntity home) {
		this.home = home;
	}
	public DeviceTypeEntity getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(DeviceTypeEntity deviceType) {
		this.deviceType = deviceType;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getGPIOPin() {
		return GPIOPin;
	}
	public void setGPIOPin(int gPIOPin) {
		GPIOPin = gPIOPin;
	}
	public String getGPIOType() {
		return GPIOType;
	}
	public void setGPIOType(String gPIOType) {
		GPIOType = gPIOType;
	}
	public Set<ScriptEntity> getScripts() {
		return scripts;
	}
	public void setScripts(Set<ScriptEntity> scripts) {
		this.scripts = scripts;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public Integer getTimeout() {
		return timeout;
	}
}
