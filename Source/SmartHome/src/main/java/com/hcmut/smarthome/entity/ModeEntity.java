package com.hcmut.smarthome.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
	
	@OnDelete(action=OnDeleteAction.CASCADE)
	@OneToMany(fetch= FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="mode")
	private Set<ScriptEntity> scripts;
	
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

	public Set<ScriptEntity> getScripts() {
		return scripts;
	}

	public void setScripts(Set<ScriptEntity> scripts) {
		this.scripts = scripts;
	}
}
