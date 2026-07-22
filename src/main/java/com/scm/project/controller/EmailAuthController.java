package com.scm.project.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.project.Entity.User;
import com.scm.project.helper.Helper;
import com.scm.project.helper.Message;
import com.scm.project.helper.MessageType;
import com.scm.project.repository.UserRepo;
import com.scm.project.services.MailService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class EmailAuthController {

	private static final Logger logger = LoggerFactory.getLogger(EmailAuthController.class);

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private MailService mailService;

	@Value("${app.base-url:http://localhost:8081}")
	private String baseUrl;

	@GetMapping("/verify-email")
	public String verifyEmail(@RequestParam("token") String token, HttpSession session) {
		User user = userRepo.findByEmailToken(token).orElse(null);
		if (user != null) {
			if (user.getEmailToken().equals(token)) {
				user.setEmailVarified(true);
				user.setEnable(true);
				userRepo.save(user);
				session.setAttribute("message", Message.builder()
						.content("Email verified successfully! You can now log in.")
						.type(MessageType.green)
						.build());
				return "redirect:/login";
			}
		}
		session.setAttribute("message", Message.builder()
				.content("Email verification failed. Invalid or expired token.")
				.type(MessageType.red)
				.build());
		return "redirect:/login";
	}

	// Show the resend verification page
	@GetMapping("/resend-verification")
	public String showResendVerification() {
		return "resend_verification";
	}

	// Handle resend verification form submission
	@PostMapping("/resend-verification")
	public String resendVerification(@RequestParam("email") String email, HttpSession session) {
		User user = userRepo.findByEmail(email.trim()).orElse(null);

		if (user == null) {
			session.setAttribute("message", Message.builder()
					.content("No account found with email: " + email)
					.type(MessageType.red)
					.build());
			return "redirect:/auth/resend-verification";
		}

		if (user.isEmailVarified() || user.isEnabled()) {
			session.setAttribute("message", Message.builder()
					.content("Your email is already verified. Please log in.")
					.type(MessageType.green)
					.build());
			return "redirect:/login";
		}

		// Generate a fresh token so old links are invalidated
		String newToken = UUID.randomUUID().toString();
		user.setEmailToken(newToken);
		userRepo.save(user);

		String emailLink = Helper.getEmailVerificationLink(newToken, baseUrl);
		logger.info("=================================================================");
		logger.info("RESEND VERIFICATION LINK FOR {}: {}", user.getEmail(), emailLink);
		logger.info("=================================================================");

		try {
			mailService.sendMail(user.getEmail(), "Verify Email : SCM Contact Manager", emailLink);
			session.setAttribute("message", Message.builder()
					.content("Verification email resent to " + email + ". Please check your inbox and spam folder.")
					.type(MessageType.green)
					.build());
		} catch (Exception e) {
			logger.error("Failed to resend verification email to {}: {}", email, e.getMessage());
			session.setAttribute("message", Message.builder()
					.content("Could not send the email. Please try again later.")
					.type(MessageType.red)
					.build());
		}

		return "redirect:/login";
	}
}
