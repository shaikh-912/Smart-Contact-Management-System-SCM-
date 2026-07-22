package com.scm.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scm.project.Entity.Contact;
import com.scm.project.services.MailService;
import com.scm.project.services.contactService;

@RestController
@RequestMapping("/api")
public class ApiController {

	private final contactService contactService;
	public ApiController(contactService contactService){
		this.contactService=contactService;
	}
	
	@GetMapping("/contacts/{contactId}")
	public Contact getContacts(@PathVariable String contactId) {
		return contactService.getById(contactId);
	}
	@Autowired
	private MailService mailService;

	@Autowired(required = false)
	private org.springframework.mail.javamail.JavaMailSender javaMailSender;

	@org.springframework.beans.factory.annotation.Value("${spring.mail.username:}")
	private String fromEmail;

	@GetMapping("/test-mail")
	public String testmail() {
		mailService.sendMail(
				"shahid91205@gmail.com",
				"Testing",
				"From SCM Project"
				);
		return "test mail (async - check server logs for errors)";
	}

	/**
	 * Synchronous mail test - surfaces the real SMTP error directly.
	 * Visit /api/test-mail-sync to see exact status or SMTP failure output.
	 */
	@GetMapping("/test-mail-sync")
	public org.springframework.http.ResponseEntity<String> testMailSync(
			@org.springframework.web.bind.annotation.RequestParam(value = "to", defaultValue = "shahid91205@gmail.com") String to) {
		if (javaMailSender == null) {
			return org.springframework.http.ResponseEntity.status(500)
					.body("ERROR: JavaMailSender bean is not configured in Spring context. Check spring.mail properties.");
		}
		try {
			jakarta.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			org.springframework.mail.javamail.MimeMessageHelper helper = 
					new org.springframework.mail.javamail.MimeMessageHelper(mimeMessage, false, "UTF-8");
			
			String sender = (fromEmail != null && !fromEmail.isBlank() && !fromEmail.startsWith("${")) 
					? fromEmail.trim() 
					: "noreply@scm.com";
			
			try {
				helper.setFrom(sender, "SCM Contact Manager");
			} catch (Exception ex) {
				helper.setFrom(sender);
			}
			helper.setTo(to);
			helper.setSubject("SCM SMTP Diagnostic Test");
			helper.setText("<h2 style='color:#1c1917;'>SCM Email Verification Test</h2><p>If you see this email, SMTP delivery is working cleanly!</p>", true);

			javaMailSender.send(mimeMessage);
			return org.springframework.http.ResponseEntity.ok("SUCCESS: Email sent to " + to + " using sender " + sender + ". Check inbox & spam.");
		} catch (Exception e) {
			String causeMsg = e.getCause() != null ? e.getCause().toString() : "None";
			return org.springframework.http.ResponseEntity.status(500)
					.body("SMTP FAILURE: " + e.getClass().getName() + " -> " + e.getMessage() + "\nCause: " + causeMsg);
		}
	}
}
