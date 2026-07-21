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

	@Value("${spring.mail.port:587}")
	private int mailPort;

	@Value("${spring.mail.username:}")
	private String mailUsername;

	@Value("${spring.mail.password:}")
	private String mailPassword;

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
		mailSender.setHost(mailHost);
		mailSender.setPort(mailPort);

		if (mailUsername != null && !mailUsername.isBlank()) {
			mailSender.setUsername(mailUsername);
		}
		if (mailPassword != null && !mailPassword.isBlank()) {
			mailSender.setPassword(mailPassword);
		}

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
		props.put("mail.smtp.ssl.trust", mailHost);

		return mailSender;
	}
}

