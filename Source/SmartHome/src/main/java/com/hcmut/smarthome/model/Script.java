package com.hcmut.smarthome.model;

import java.io.Serializable;

public class Script implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;

	private String name;

	private String content;

	private ScriptType scriptType;

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

	public String getContent() {
		return content;
	}

	public void setContent(String script) {
		this.content = script;
	}

	public ScriptType getType() {
		return scriptType;
	}

	public void setType(ScriptType scriptType) {
		this.scriptType = scriptType;
	}
}
