package com.scm.project.services.serviceImplement;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.scm.project.services.MailService;

@Service
public class MailServiceImpl implements MailService{

	private JavaMailSender javaMailSender;
	
	public MailServiceImpl(JavaMailSender javaMailSender) {
		this.javaMailSender=javaMailSender;
	}
	
	@Override
	public void sendMail(String to, String subject, String text) {
		try {
			SimpleMailMessage message=new SimpleMailMessage();
			System.out.print("before");
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			
			javaMailSender.send(message);
			System.out.println("success fully sent email");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
