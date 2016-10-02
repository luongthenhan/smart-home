package com.hcmut.smarthome.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="mode")
public class ModeEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="name", nullable = false , length = 45)
	private String name;
	
	@Column(name="description", nullable = true , length = 512)
	private String description;
	
	@ManyToOne
	@JoinColumn(name="home_id")
	private HomeEntity home;
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true,mappedBy="device")
	List<ScriptEntity> scripts;
	
	public ModeEntity(){
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HomeEntity getHome() {
		return home;
	}

	public void setHome(HomeEntity home) {
		this.home = home;
	}

	public List<ScriptEntity> getScripts() {
		return scripts;
	}

	public void setScripts(List<ScriptEntity> scripts) {
		this.scripts = scripts;
	}
	
	
}
