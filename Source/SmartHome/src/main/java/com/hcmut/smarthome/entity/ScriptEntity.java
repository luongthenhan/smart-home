package com.hcmut.smarthome.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.util.StringUtils;

@Entity
@Table(name="script")
public class ScriptEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="name", nullable = true , length = 45)
	private String name;
	
	@Column(name="content", nullable = true , length = 4096)
	private String content;
	
	@Column(name="enabled", nullable = false)
	private boolean enabled; 
	
	@ManyToOne
	@JoinColumn(name="device_id")
	private DeviceEntity device;
	
	@ManyToOne
	@JoinColumn(name="script_type_id")
	private ScriptTypeEntity scriptType;
	
	@ManyToOne
	@JoinColumn(name="mode_id")
	private ModeEntity mode;
	
	public ScriptEntity() {
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
		this.name = StringUtils.trimWhitespace(name);
	}

	public DeviceEntity getDevice() {
		return device;
	}
	public void setDevice(DeviceEntity device) {
		this.device = device;
	}
	public ScriptTypeEntity getScriptType() {
		return scriptType;
	}
	public void setScriptType(ScriptTypeEntity scriptType) {
		this.scriptType = scriptType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = StringUtils.trimWhitespace(content);
	}
	public ModeEntity getMode() {
		return mode;
	}
	public void setMode(ModeEntity mode) {
		this.mode = mode;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
}
