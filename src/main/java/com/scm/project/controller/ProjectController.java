package com.scm.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.project.FormData.SupportContactForm;
import com.scm.project.helper.Message;
import com.scm.project.helper.MessageType;
import com.scm.project.services.MailService;
import com.scm.project.services.userService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProjectController {

    @Autowired
    private userService userService;

    @Autowired
    private MailService mailService;

    @Value("${admin.email.address:}")
    private String adminEmailAddress;

    @RequestMapping("/")
    public String index(){
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home(){
        return "home";
    }

    @RequestMapping("/about")
    public String about(){
        return "about";
    }

    @RequestMapping("/services")
    public String services(){
        return "services";
    }

    @GetMapping("/contact")
    public String contact(Model model){
        model.addAttribute("supportContactForm", new SupportContactForm());
        return "contact";
    }

    @PostMapping("/contact/submit")
    public String handleContactForm(
            @ModelAttribute SupportContactForm form,
            HttpSession session) {

        String subject = "New Contact Inquiry from " + form.getName();
        String body = "Name: " + form.getName() + "\n" +
                      "Email: " + form.getEmail() + "\n\n" +
                      "Message:\n" + form.getMessage();

        // Send to admin email (fallback to a default if not set)
        String recipient = adminEmailAddress != null && !adminEmailAddress.isBlank() 
                           ? adminEmailAddress 
                           : "admin@scm.com";

        mailService.sendEmail(recipient, subject, body);

        session.setAttribute("message", Message.builder()
                .content("Your message has been sent successfully. We will get back to you soon!")
                .type(MessageType.green)
                .build());

        return "redirect:/contact";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
    
    @RequestMapping("/signup")
    public String signup(){
        return "redirect:/login";
    }
}

