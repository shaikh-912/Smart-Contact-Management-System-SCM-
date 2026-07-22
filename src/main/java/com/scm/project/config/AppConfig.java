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
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(465);
		mailSender.setProtocol("smtps");

		if (mailUsername != null && !mailUsername.isBlank() && !mailUsername.startsWith("${")) {
			mailSender.setUsername(mailUsername.trim());
		}
		if (mailPassword != null && !mailPassword.isBlank() && !mailPassword.startsWith("${")) {
			mailSender.setPassword(mailPassword.trim());
		}

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		// Timeouts to prevent SMTP hanging
		props.put("mail.smtp.connectiontimeout", "10000");
		props.put("mail.smtp.timeout", "10000");
		props.put("mail.smtp.writetimeout", "10000");

		return mailSender;
	}
}

