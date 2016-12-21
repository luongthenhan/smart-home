package com.hcmut.smarthome.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.util.StringUtils;

@Entity
@Table(name="device_type")
public class DeviceTypeEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="name", nullable = false , length = 45)
	private String typeName;
	
	@Column(name="description", nullable = true , length = 512)
	private String description;
	
	@Column(name="image_url", nullable = true , length = 100)
	private String imageURL;
	
	@Column(name="main_action_id", nullable=true)
	private Integer mainAction;
	
	@Column(name="gpio_type", nullable=true)
	private String GPIOType;
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true,mappedBy="deviceType")
	private List<DeviceEntity> devices;
	
	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL, orphanRemoval=true,mappedBy="deviceType")
	private List<ConditionEntity> conditions;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="devicetype_has_action", joinColumns={@JoinColumn(name="device_type_id",nullable=false)}, inverseJoinColumns={@JoinColumn(name="action_id",nullable=false)})
	private Set<ActionEntity> actions;
	
	public DeviceTypeEntity(){
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = StringUtils.trimWhitespace(typeName);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = StringUtils.trimWhitespace(description);
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = StringUtils.trimWhitespace(imageURL);
	}

	public List<ConditionEntity> getConditions() {
		return conditions;
	}

	public void setConditions(List<ConditionEntity> conditions) {
		this.conditions = conditions;
	}


	public void setDevices(List<DeviceEntity> devices) {
		this.devices = devices;
	}

	public List<DeviceEntity> getDevices() {
		return devices;
	}

	public Integer getMainAction() {
		return mainAction;
	}

	public Set<ActionEntity> getActions() {
		return actions;
	}

	public void setActions(Set<ActionEntity> actions) {
		this.actions = actions;
	}

	public void setMainAction(Integer mainAction) {
		this.mainAction = mainAction;
	}

	public String getGPIOType() {
		return GPIOType;
	}

	public void setGPIOType(String gPIOType) {
		GPIOType = gPIOType;
	}

}
