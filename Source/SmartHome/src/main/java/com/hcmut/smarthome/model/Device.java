package com.hcmut.smarthome.model;

import java.io.Serializable;
import java.util.List;

import com.hcmut.smarthome.converter.DeviceTypeConverter;
import com.hcmut.smarthome.converter.HomeConverter;
import com.hcmut.smarthome.converter.ScriptConverter;
import com.hcmut.smarthome.entity.DeviceEntity;

public class Device implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected int id;
	
	protected String name;
	
	protected String description;
	
	protected String location;
	
	protected String code;

	protected Home home;
	
	protected Integer timeout;
	
	protected BriefDeviceType deviceType;
	
	protected Boolean enabled = null;
	
	protected Integer GPIOPin;
	
	protected String status;
	
	protected List<Script> scripts;
	
	public Device() {
		super();
	}
	
	public Device(Device that){
		this.id = that.getId();
		this.name = that.getName();
		this.description = that.getDescription();
		this.location = that.getLocation();
		this.GPIOPin = that.getGPIO();
		this.status = that.getStatus();
		this.enabled = that.isEnabled();
		this.code = that.getCode();
		this.deviceType = that.getDeviceType();
		this.home = that.getHome();
		this.scripts = that.getScripts();
		this.timeout = that.getTimeout();
	}
	
	public Device(DeviceEntity that){
		this.id = that.getId();
		this.name = that.getName();
		this.description = that.getDescription();
		this.location = that.getLocation();
		this.GPIOPin = that.getGPIOPin();
		this.status = "";
		this.enabled = that.isEnabled();
		this.code = that.getCode();
		this.deviceType = DeviceTypeConverter.toBriefDeviceType(that.getDeviceType());
		this.home = HomeConverter.toModel(that.getHome());
		this.timeout = that.getTimeout();
		this.scripts = ScriptConverter.toListModel(that.getScripts());
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
	public Boolean isEnabled() {
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
	public Integer getGPIO() {
		return GPIOPin;
	}
	public void setGPIO(Integer gPIOPin) {
		GPIOPin = gPIOPin;
	}

	public List<Script> getScripts() {
		return scripts;
	}

	public void setScripts(List<Script> scripts) {
		this.scripts = scripts;
	}

	public BriefDeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(BriefDeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	
}
