package com.scm.project.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class AppConfig {
	
	@Value("${cloudinary.cloud.name}")
	private String cloudName;
	@Value("${cloudinary.api.key}")
	private String apiKey;
	@Value("${cloudinary.api.secret}")
	private String apiSecret;
	
	@Value("${spring.mail.host:smtp.gmail.com}")
	private String mailHost;

	@Value("${spring.mail.port:465}")
	private int mailPort;

	@Value("${spring.mail.username:}")
	private String mailUsername;

	@Value("${spring.mail.password:}")
	private String mailPassword;

	@Value("${spring.mail.protocol:smtps}")
	private String mailProtocol;

	@Bean
	public Cloudinary cloudinary() {
		return new Cloudinary(
				ObjectUtils.asMap(
					"cloud_name",cloudName,
					"api_key",apiKey,
					"api_secret",apiSecret
				)
		);
	}

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		// Force port 465 SSL if mailPort is 587 (Render env vars often set MAIL_PORT=587 which gets blocked)
		int effectivePort = (mailPort == 587) ? 465 : mailPort;
		mailSender.setHost(mailHost);
		mailSender.setPort(effectivePort);
		
		String proto = (effectivePort == 465) ? "smtps" : "smtp";
		mailSender.setProtocol(proto);

		if (mailUsername != null && !mailUsername.isBlank() && !mailUsername.startsWith("${")) {
			mailSender.setUsername(mailUsername.trim());
		}
		if (mailPassword != null && !mailPassword.isBlank() && !mailPassword.startsWith("${")) {
			mailSender.setPassword(mailPassword.trim());
		}

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", proto);
		props.put("mail.smtp.auth", "true");

		if (effectivePort == 465 || "smtps".equalsIgnoreCase(proto)) {
			// SSL configuration for port 465 (Cloud / Render compatible)
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.ssl.trust", mailHost);
			props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
			props.put("mail.smtp.socketFactory.port", String.valueOf(effectivePort));
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");
		} else {
			// STARTTLS configuration for port 587
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");
			props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
			props.put("mail.smtp.ssl.trust", mailHost);
		}

		// Timeouts to prevent SMTP from hanging indefinitely on Render/cloud networks
		props.put("mail.smtp.connectiontimeout", "10000");
		props.put("mail.smtp.timeout", "10000");
		props.put("mail.smtp.writetimeout", "10000");

		return mailSender;
	}
}

