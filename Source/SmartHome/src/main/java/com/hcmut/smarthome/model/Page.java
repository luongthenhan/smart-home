package com.hcmut.smarthome.model;

public abstract class Page implements DetectedValueChange{
	private boolean isSkip;
	protected Page next;
	protected Page back;
	protected String name;
	
	public abstract <T,R> R onNext(T data);
	public abstract <T,R> R onBack(T data);
	public abstract <T,R> R onContinueLater(T data);
	public abstract <T> void onTransfer(T data);
	
	public Page() {
		super();
		setSkip(false);
	}
	
	public Page(String name) {
		this();
		setName(name);
	}
	
	public Page(Page back, Page next) {
		super();
		setSkip(false);
		setBack(back);
		setNext(next);
	}
	
	public boolean hasChanged(){
		return false;
	}
	
	public boolean isSkip() {
		return isSkip;
	}
	public void setSkip(boolean isSkip) {
		this.isSkip = isSkip;
	}
	public Page getNext() {
		return next;
	}
	public void setNext(Page next) {
		this.next = next;
	}
	public Page getBack() {
		return back;
	}
	public void setBack(Page back) {
		this.back = back;
	}
	public String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
}
