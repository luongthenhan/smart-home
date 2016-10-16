package com.hcmut.smarthome.utils;

import java.io.Serializable;

public class ConflictConditionException extends Exception implements Serializable{

	private static final long serialVersionUID = -6491634730760476197L;
	
	public ConflictConditionException(){
		super();
	}
	
	public ConflictConditionException(String msg){
		super(msg);
	}
}
