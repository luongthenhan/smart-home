package com.hcmut.smarthome.model;


public class ScriptMoreDetail extends Script {

	private static final long serialVersionUID = -4409089799821613826L;
	
	private Integer modeId;

	private Integer homeId;
	
	private Integer deviceId;
	
	public ScriptMoreDetail() {
		super();
	}

	public ScriptMoreDetail(Script script){
		this.content = script.content;
		this.enabled = script.enabled;
		this.id = script.id;
		this.name = script.name;
		this.scriptType = script.scriptType;
	}
	
	public Integer getModeId() {
		return modeId;
	}

	public void setModeId(Integer modeId) {
		this.modeId = modeId;
	}

	public Integer getHomeId() {
		return homeId;
	}

	public void setHomeId(Integer homeId) {
		this.homeId = homeId;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
}
