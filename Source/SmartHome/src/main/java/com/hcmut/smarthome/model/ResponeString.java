package com.hcmut.smarthome.model;


public class ResponeString {
	private static final int ERROR_MESSAGE = -1;
	
	String content;
	int id;

	public ResponeString(String errorMessage){
		this.id = ERROR_MESSAGE;
		this.content = errorMessage;
	}
	
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
