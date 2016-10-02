package com.hcmut.smarthome.service.impl;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

// TODO Not finished yet
public class TimerService{
	private Consumer<Object> function;
	
	public void schedule(Date fromValue , Date toValue, final Consumer<Object> function){
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				function.accept(null);
			}
		};
		
		timer.schedule(task, fromValue, 1000);
	}
	
	public Consumer<Object> getFunction() {
		return function;
	}
	public void setFunction(Consumer<Object> function) {
		this.function = function;
	}
}
