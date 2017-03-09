package com.hcmut.smarthome.model;

public class ConfirmationPage extends Page{
	private static final String CONFIRMATION_PAGE = "CONFIRMATION_PAGE";
	
	public ConfirmationPage() {
		super();
		setNext(null);
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
		super.setName(CONFIRMATION_PAGE);
	}
}
