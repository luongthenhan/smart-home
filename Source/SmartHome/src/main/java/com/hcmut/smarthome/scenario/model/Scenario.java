package com.hcmut.smarthome.scenario.model;

import java.util.List;

public class Scenario {
	
	private Integer id;
	
	private int homeId;
	
	private int timeout;
	
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
	
}
