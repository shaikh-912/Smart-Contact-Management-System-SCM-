package com.scm.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.project.Entity.User;
import com.scm.project.repository.UserRepo;
import com.scm.project.helper.Message;
import com.scm.project.helper.MessageType;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class EmailAuthController {

	@Autowired
	private UserRepo userRepo;

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
}
