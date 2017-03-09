package com.hcmut.smarthome.model;

import java.util.List;

public class Process {
	
	protected List<Page> pages;
	protected Object data;
	
	public Process() {
		super();
		pages.add(new EmptyPage());
	}
	
	protected void addPage(Page page){
		int lastIndex = pages.size() - 1;
		Page lastPage = pages.get(lastIndex);
		
		lastPage.setNext(page);
		page.setBack(lastPage);
	}
	
	public List<Page> getPages() {
		return pages;
	}
	public void setPages(List<Page> pages) {
		this.pages = pages;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	public void next(){
		
	}
	
	public void back(){
		
	}
}
