package com.scm.project.services;

import org.springframework.scheduling.annotation.Async;

public interface MailService {

	//send mail method asynchronously
	@Async
	void sendMail(String to, String subject, String text);
	
	
}
