package com.hcmut.smarthome.scenario.model;

import java.util.List;

public class Scenario {
	private Integer id;
	
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
}
