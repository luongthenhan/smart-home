package com.hcmut.smarthome.model;

public class ResponeString {
	String content;
	int id;

	public ResponeString(int id, String content){
		this.content = content;
		this.id = id;
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
}
