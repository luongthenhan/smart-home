package com.hcmut.smarthome.model;

import java.io.Serializable;
import java.util.List;

public class DeviceType extends BriefDeviceType implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String description;
	
	private String imageURL;
	
	private Action mainAction;
	
	private List<Action> actions;
	
	private List<Condition> conditions;
	
	public DeviceType(){
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
		this.typeName = typeName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public Action getMainAction() {
		return mainAction;
	}

	public void setMainAction(Action mainAction) {
		this.mainAction = mainAction;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	
}
