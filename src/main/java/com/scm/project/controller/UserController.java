package com.scm.project.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.project.Entity.User;
import com.scm.project.FormData.ProfileForm;
import com.scm.project.helper.Helper;
import com.scm.project.helper.Message;
import com.scm.project.helper.MessageType;
import com.scm.project.repository.ContactRepo;
import com.scm.project.services.imageService;
import com.scm.project.services.userService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private userService userService;

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private imageService imageService;

    // ── Dashboard ────────────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String userDashboard(Model model, Authentication authentication) {
        String email = Helper.getEmailOfLoggedUser(authentication);
        User user = userService.findByEmail(email);

        long totalContacts    = contactRepo.countByUser(user);
        long favoriteContacts = contactRepo.countByUserAndFavorite(user, true);

        model.addAttribute("totalContacts",    totalContacts);
        model.addAttribute("favoriteContacts", favoriteContacts);
        return "user/dashboard";
    }

    // ── Profile View ─────────────────────────────────────────────────────────
    @RequestMapping("/profile")
    public String userProfile(Model model, Authentication authentication) {
        String email = Helper.getEmailOfLoggedUser(authentication);
        User user    = userService.findByEmail(email);

        long totalContacts = contactRepo.countByUser(user);
        model.addAttribute("totalContacts", totalContacts);

        // Pre-fill the edit form with current values
        ProfileForm profileForm = new ProfileForm();
        profileForm.setName(user.getName());
        profileForm.setPhoneNumber(user.getPhoneNumber());
        profileForm.setAbout(user.getAbout());
        model.addAttribute("profileForm", profileForm);

        return "user/profile";
    }

    // ── Profile Update (POST) ─────────────────────────────────────────────────
    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute ProfileForm profileForm,
            Authentication authentication,
            HttpSession session) {

        String email   = Helper.getEmailOfLoggedUser(authentication);
        User user      = userService.findByEmail(email);

        // Update basic fields
        user.setName(profileForm.getName());
        user.setPhoneNumber(profileForm.getPhoneNumber());
        user.setAbout(profileForm.getAbout());

        // Handle profile picture upload/replace
        if (profileForm.getProfileImage() != null && !profileForm.getProfileImage().isEmpty()) {

            // Delete old profile pic from Cloudinary if it was uploaded there
            // (Google profile pics are external URLs — don't try to delete those)
            String oldPic = user.getProfilePic();
            if (oldPic != null && oldPic.contains("res.cloudinary.com")) {
                // Extract public_id from stored Cloudinary URL (last path segment without extension)
                try {
                    String[] parts = oldPic.split("/");
                    String fileWithExt = parts[parts.length - 1];
                    String oldPublicId = fileWithExt.contains(".") 
                                        ? fileWithExt.substring(0, fileWithExt.lastIndexOf('.'))
                                        : fileWithExt;
                    imageService.deleteImage(oldPublicId);
                } catch (Exception ignored) { /* safe to skip */ }
            }

            // Upload new profile picture
            String newPublicId = "profile_" + UUID.randomUUID();
            String newUrl = imageService.uploadImage(profileForm.getProfileImage(), newPublicId);
            if (newUrl != null && !newUrl.isEmpty()) {
                user.setProfilePic(newUrl);
            }
        }

        userService.updatUser(user);

        session.setAttribute("message", Message.builder()
                .content("Profile updated successfully!")
                .type(MessageType.green)
                .build());

        return "redirect:/user/profile";
    }
}

