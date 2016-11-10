package com.hcmut.smarthome.model;

import java.util.Set;

public class ResponeString {
	private static final int ERROR_MESSAGE = -1;
	
	String content;
	int id;
	Set<Integer> deviceIds = null;

	public ResponeString(String errorMessage){
		this.id = ERROR_MESSAGE;
		this.content = errorMessage;
	}
	
	public ResponeString(int id, String content){
		this.content = content;
		this.id = id;
	}
	
	public ResponeString(int id, String content, Set<Integer> deviceIds) {
		this.content = content;
		this.id = id;
		this.deviceIds = deviceIds;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Set<Integer> getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(Set<Integer> deviceIds) {
		this.deviceIds = deviceIds;
	}

}
