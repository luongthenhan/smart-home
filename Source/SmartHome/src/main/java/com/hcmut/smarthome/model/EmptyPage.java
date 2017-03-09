package com.hcmut.smarthome.model;

public class EmptyPage extends Page {
	private static final String EMPTY_PAGE = "EMPTY_PAGE";
	
	public EmptyPage() {
		super();
		setBack(null);
	}

	@Override
	public <T, R> R onNext(T data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T, R> R onBack(T data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T, R> R onContinueLater(T data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> void onTransfer(T data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setName(String name) {
		setName(EMPTY_PAGE);
	}
}
