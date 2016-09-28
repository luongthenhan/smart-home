package com.hcmut.smarthome.model;

import java.io.Serializable;

public class Script implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;

	private String name;

	private String script;

	private String scriptType;

	public Script() {
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

	public String getScriptType() {
		return scriptType;
	}

	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}

}
