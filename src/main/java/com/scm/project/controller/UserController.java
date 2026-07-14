package com.scm.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.project.Entity.User;
import com.scm.project.helper.Helper;
import com.scm.project.repository.ContactRepo;
import com.scm.project.services.userService;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private userService userService;

    @Autowired
    private ContactRepo contactRepo;

    // ── Dashboard ────────────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String userDashboard(Model model, Authentication authentication) {
        String email = Helper.getEmailOfLoggedUser(authentication);
        User user = userService.findByEmail(email);

        // Live stats
        long totalContacts   = contactRepo.countByUser(user);
        long favoriteContacts = contactRepo.countByUserAndFavorite(user, true);

        model.addAttribute("totalContacts",    totalContacts);
        model.addAttribute("favoriteContacts", favoriteContacts);
        // ${user} is already injected globally by ControllerRoot @ControllerAdvice
        return "user/dashboard";
    }

    // ── Profile ──────────────────────────────────────────────────────────────
    @RequestMapping("/profile")
    public String userProfile(Model model, Authentication authentication) {
        String email = Helper.getEmailOfLoggedUser(authentication);
        User user    = userService.findByEmail(email);

        long totalContacts = contactRepo.countByUser(user);
        model.addAttribute("totalContacts", totalContacts);
        // ${user} injected globally
        return "user/profile";
    }
}
