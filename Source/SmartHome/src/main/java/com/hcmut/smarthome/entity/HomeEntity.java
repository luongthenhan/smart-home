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
@Table(name = "home")
public class HomeEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="name", nullable = true , length = 45)
	private String name;
	
	@Column(name="address", nullable = true , length = 100)
	private String address;
	
	@Column(name="description", nullable = true , length = 512)
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "user_id" , nullable = false)
	private UserEntity user;
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true,mappedBy="home")
	private List<ModeEntity> modes; 
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true,mappedBy="home")
	private List<DeviceEntity> devices;
	
	public HomeEntity() {
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public UserEntity getUser() {
		return user;
	}
	public void setUser(UserEntity user) {
		this.user = user;
	}
	public List<ModeEntity> getModes() {
		return modes;
	}
	public void setModes(List<ModeEntity> modes) {
		this.modes = modes;
	}
	public List<DeviceEntity> getDevices() {
		return devices;
	}
	public void setDevices(List<DeviceEntity> devices) {
		this.devices = devices;
	}
	
	
}
