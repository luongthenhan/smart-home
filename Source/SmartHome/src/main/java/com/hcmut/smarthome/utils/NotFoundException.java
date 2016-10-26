package com.hcmut.smarthome.utils;

import java.io.Serializable;

public class NotFoundException extends Exception implements Serializable{

	private static final long serialVersionUID = -6491634730760476197L;
	
	public NotFoundException(){
		super();
	}
	
	public NotFoundException(String msg){
		super(msg);
	}
}
