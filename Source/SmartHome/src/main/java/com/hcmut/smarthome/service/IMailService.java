package com.hcmut.smarthome.service;

public interface IMailService {
	
	public boolean sendMail(String to, String subject, String content);
	
	public boolean sendActivationMail(String to, int userId);

}
