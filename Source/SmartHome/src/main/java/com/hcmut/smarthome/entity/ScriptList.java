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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="script_list")
public class ScriptList implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="home_id" , nullable= false)
	private Home home;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="user_id", nullable=false)
	private User user;
	
	@Column(name="description", nullable = true , length = 1024)
	private String description;
	
	@ManyToMany
	@JoinTable(name="scriptlist_has_script", joinColumns={@JoinColumn(name="script_list_id",nullable=false)}, inverseJoinColumns={@JoinColumn(name="script_id",nullable=false)})
	private List<Script> scripts;
	
	public ScriptList() {
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
	public Home getHome() {
		return home;
	}
	public void setHome(Home home) {
		this.home = home;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
}
