package com.hcmut.smarthome.service.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hcmut.smarthome.service.IMailService;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "classpath:ApplicationContext.xml", "classpath:spring-security.xml" } )
public class MailServiceTest {
	
	@Autowired
	private IMailService mailService;

	@Test
	public void should_send_email_successfully() throws Exception {
		mailService.sendMail("tung.cs.1994@gmail.com", "Test 4", "<b>Test 4</b>");
		
	}

}
