package com.hcmut.smarthome.service.impl;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.hcmut.smarthome.service.IMailService;

@Service
@PropertySource("classpath:smarthome.properties")
public class MailServiceImpl implements IMailService {

	private static final Logger LOGGER = Logger
			.getLogger(MailServiceImpl.class);
	private static final String SMART_HOME = "Smart home";

	@Value("${mail.username}")
	private String username;

	@Value("${mail.password}")
	private String password;

	@Value("${smarthome.domainName}")
	private String domainName;

	@Value("${activation.link}")
	private String activationLink;

	@Value("${activation.mail.subject}")
	private String activationMailSubject;

	@Value("${activation.mail.content}")
	private String activationMailContent;

	private Properties props;

	@PostConstruct
	public void init() {

		props = System.getProperties();
		props.setProperty("mail.smtp.host", "smtp.gmail.com");
		props.setProperty("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.store.protocol", "pop3");
		props.put("mail.transport.protocol", "smtp");

	}

	@Override
	public boolean sendMail(String to, String subject, String content) {

		try {
			Authenticator authenticator = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getDefaultInstance(props, authenticator);

			// -- Create a new message --
			Message msg = new MimeMessage(session);

			// -- Set the FROM and TO fields --
			msg.setFrom(new InternetAddress(username, SMART_HOME));
			msg.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to, false));

			msg.setSubject(subject);
			msg.setContent(content, "text/html; charset=utf-8");
			msg.setSentDate(new Date());

			Transport.send(msg);
			LOGGER.debug("Message sent");
		} catch (MessagingException | UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
			return false;
		}

		return true;
	}

	@Override
	public boolean sendActivationMail(String to, int userId) {
		
		String userActivationMailContent = MessageFormat.format(
				activationMailContent, getActivationLink(userId));
		
		return sendMail(to, activationMailSubject, userActivationMailContent);
	}

	private String getActivationLink(int userId) {
		String userActivationLink = MessageFormat
				.format(activationLink, String.valueOf(userId));
		return domainName + userActivationLink;
	}

}
