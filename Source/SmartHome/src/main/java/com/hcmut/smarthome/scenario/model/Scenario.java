package com.hcmut.smarthome.scenario.model;

import java.util.List;

public class Scenario {
	
	public enum ScenarioStatus{
		RUNNING,
		STOPPING,
		STOP_FOREVER
	}
	
	private Integer id;
	
	private int homeId;
	
	private int deviceId;
	
	private int modeId;
	
	private int timeout;
	
	private ScenarioStatus status;	
	
	List<IBlock> blocks;

	public List<IBlock> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<IBlock> blocks) {
		this.blocks = blocks;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getHomeId() {
		return homeId;
	}

	public void setHomeId(int homeId) {
		this.homeId = homeId;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public ScenarioStatus getStatus() {
		return status;
	}

	public void setStatus(ScenarioStatus status) {
		this.status = status;
	}

	public int getModeId() {
		return modeId;
	}

	public void setModeId(int modeId) {
		this.modeId = modeId;
	}
}
