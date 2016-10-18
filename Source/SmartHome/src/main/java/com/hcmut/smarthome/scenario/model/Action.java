package com.hcmut.smarthome.scenario.model;

import java.util.List;

public class Action implements IBlock{
	private String name;
	private List<IBlock> blocks;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public List<IBlock> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<IBlock> blocks) {
		this.blocks = blocks;
	}

}
