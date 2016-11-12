package com.hcmut.smarthome.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.util.StringUtils;

@Entity
@Table(name="action")
public class ActionEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="name", length = 45, nullable = true)
	private String name;
	
	@Column(name="script", length = 128, nullable = false)
	private String script;

	public ActionEntity(){
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

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = StringUtils.trimWhitespace(script);
	}
}
