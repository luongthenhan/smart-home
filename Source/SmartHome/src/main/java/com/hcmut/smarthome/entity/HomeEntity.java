package com.hcmut.smarthome.entity;

import java.io.Serializable;
import java.util.List;

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
import org.springframework.util.StringUtils;

@Entity
@Table(name = "home")
public class HomeEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="name", nullable = true , length = 45)
	private String name;
	
	@Column(name="address", nullable = false , length = 100, unique=true)
	private String address;
	
	@Column(name="description", nullable = true , length = 512)
	private String description;
	
	@Column (name = "enabled", nullable = false)
	private boolean enabled;
	
	@ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.REMOVE)
	@JoinColumn(name="current_mode_id", nullable = true)
	private ModeEntity currentMode;
	
	@ManyToOne
	@JoinColumn(name = "user_id" , nullable = false)
	private UserEntity user;
	
	@OnDelete(action=OnDeleteAction.CASCADE)
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL,orphanRemoval=true, mappedBy="home")
	private List<ModeEntity> modes; 
	
	@OnDelete(action=OnDeleteAction.CASCADE)
	@OneToMany(cascade=CascadeType.ALL, mappedBy="home")
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
		this.name = StringUtils.trimWhitespace(name);
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = StringUtils.trimWhitespace(address);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = StringUtils.trimWhitespace(description);
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
	public ModeEntity getCurrentMode() {
		return currentMode;
	}
	public void setCurrentMode(ModeEntity currentMode) {
		this.currentMode = currentMode;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
}
