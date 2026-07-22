package com.scm.project.services.serviceImplement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.scm.project.services.MailService;

@Service
public class MailServiceImpl implements MailService{

	private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;
	
	public MailServiceImpl(JavaMailSender javaMailSender) {
		this.javaMailSender=javaMailSender;
	}
	
	@Override
	public void sendMail(String to, String subject, String text) {
		try {
			SimpleMailMessage message=new SimpleMailMessage();
			if (fromEmail != null && !fromEmail.isBlank()) {
				message.setFrom(fromEmail);
			}
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			
			javaMailSender.send(message);
			logger.info("Email successfully sent to {}", to);
		} catch (Exception e) {
			logger.error("Failed to send email to {}", to, e);
		}
		
	}

}

