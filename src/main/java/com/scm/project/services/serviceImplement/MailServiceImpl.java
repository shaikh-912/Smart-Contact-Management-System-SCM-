package com.scm.project.services.serviceImplement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.scm.project.services.MailService;

import jakarta.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {

	private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;

	public MailServiceImpl(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	@Async
	@Override
	public void sendMail(String to, String subject, String text) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

			String sender = (fromEmail != null && !fromEmail.isBlank() && !fromEmail.startsWith("${"))
					? fromEmail.trim()
					: "noreply@scm.com";

			try {
				helper.setFrom(sender, "SCM Contact Manager");
			} catch (Exception ex) {
				helper.setFrom(sender);
			}

			helper.setTo(to);
			helper.setSubject(subject);

			// Build HTML body so verification link is a clickable button
			String htmlBody = buildHtmlEmail(subject, text);
			helper.setText(htmlBody, true);

			javaMailSender.send(mimeMessage);
			logger.info("Email successfully sent to {}", to);

		} catch (MailException | jakarta.mail.MessagingException e) {
			logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
			// Re-throw so callers know the email was NOT delivered
			throw new RuntimeException("Failed to send verification email to " + to + ". Please try again later.", e);
		}
	}

	/**
	 * Wraps a URL or plain text in a minimal branded HTML email body.
	 * If text is a URL it renders a clickable button; otherwise plain paragraph.
	 */
	private String buildHtmlEmail(String subject, String text) {
		String buttonOrText;
		if (text != null && (text.startsWith("http://") || text.startsWith("https://"))) {
			buttonOrText = "<p style=\"text-align:center;margin:24px 0;\">"
					+ "<a href=\"" + text + "\" "
					+ "style=\"background:#1c1917;color:#fff;padding:12px 28px;border-radius:8px;"
					+ "text-decoration:none;font-weight:600;font-size:15px;display:inline-block;\">"
					+ "Verify My Email"
					+ "</a></p>"
					+ "<p style=\"color:#78716c;font-size:13px;text-align:center;\">Or copy this link into your browser:</p>"
					+ "<p style=\"word-break:break-all;font-size:12px;color:#a8a29e;text-align:center;\">" + text + "</p>";
		} else {
			buttonOrText = "<p style=\"color:#44403c;font-size:15px;line-height:1.6;\">" + text + "</p>";
		}

		return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body style=\"margin:0;padding:0;"
				+ "background:#f5f5f4;font-family:Arial,sans-serif;\">"
				+ "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr><td align=\"center\" style=\"padding:40px 16px;\">"
				+ "<table width=\"560\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#fff;border-radius:12px;"
				+ "border:1px solid #e7e5e4;overflow:hidden;\">"
				+ "<tr><td style=\"background:#1c1917;padding:24px 32px;\">"
				+ "<h1 style=\"margin:0;color:#fff;font-size:20px;font-weight:700;\">SCM &mdash; Smart Contact Manager</h1>"
				+ "</td></tr>"
				+ "<tr><td style=\"padding:32px;\">"
				+ "<h2 style=\"margin:0 0 12px;color:#1c1917;font-size:18px;\">" + subject + "</h2>"
				+ buttonOrText
				+ "<hr style=\"border:none;border-top:1px solid #f5f5f4;margin:28px 0;\">"
				+ "<p style=\"color:#a8a29e;font-size:12px;margin:0;\">This link expires in 24 hours. "
				+ "If you did not register on SCM, please ignore this email.</p>"
				+ "</td></tr></table>"
				+ "</td></tr></table></body></html>";
	}

}
