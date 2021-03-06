package com.hcmut.smarthome.model;

import java.io.Serializable;

public class Script implements Serializable {
	private static final long serialVersionUID = 1L;

	protected int id;

	protected String name;

	protected String content;
	
	protected Boolean enabled = null;

	protected ScriptType scriptType;

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

	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}
