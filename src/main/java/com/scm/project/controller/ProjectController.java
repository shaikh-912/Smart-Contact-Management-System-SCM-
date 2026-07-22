package com.scm.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.project.Entity.User;
import com.scm.project.FormData.UserForm;
import com.scm.project.helper.Message;
import com.scm.project.helper.MessageType;
import com.scm.project.services.userService;
import com.scm.project.services.serviceImplement.userImpl;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class ProjectController {

    @Autowired
    private userService userService;

    @RequestMapping("/")
    public String index(){
        return "redirect:/home";
    }
    @RequestMapping("/home")
    public String home(){
        System.out.println("home controller");
        return "home";
    }

    @RequestMapping("/about")
    public String about(){
        System.out.println("about page loading");
        return "about";
    }
     @RequestMapping("/services")
    public String services(){
        System.out.println("service page loading");
        return "services";
    }
       @RequestMapping("/contact")
    public String contact(){
        System.out.println("contact page loading");
        return "contact";
    }
      @GetMapping("/login")
        public String login(){
        System.out.println("login page loading");
        return "login";
    }
    
       @RequestMapping("/signup")
       public String signup(org.springframework.ui.Model model){
        System.out.println("signup page loading");
        UserForm userForm=new UserForm();
        model.addAttribute("userForm", userForm);
        return "signup";
    }

    @PostMapping(value = "/do-signup")
    public String doSignUP(@Valid @ModelAttribute UserForm userFrom, BindingResult rBindingResult, HttpSession session){
        System.out.println("do Register....");
        System.out.println(userFrom);

        if(rBindingResult.hasErrors()){
            return "signup";
        }

        // Check if email is already registered
        if(userService.isUserExistByEmail(userFrom.getEmail())){
            session.setAttribute("message", Message.builder()
                    .content("An account with this email already exists. Please login or use a different email.")
                    .type(MessageType.red).build());
            return "redirect:/signup";
        }

        User user=new User();
        user.setName(userFrom.getName());
        user.setEmail(userFrom.getEmail());
        user.setPassword(userFrom.getPassword());
        user.setPhoneNumber(userFrom.getPhoneNumber());
        user.setAbout(userFrom.getAbout());
        user.setProfilePic("https://www.shutterstock.com/shutterstock/photos/2558760599/display_1500/stock-vector-user-profile-icon-vector-avatar-or-person-icon-profile-picture-portrait-symbol-default-avatar-2558760599.jpg");
        user.setEnable(false);

        try {
            // saveUser encodes password, generates token, saves to DB, and sends verification email
            userService.saveUser(user);
            System.out.println("Saved & email sent for: " + user.getEmail());
            session.setAttribute("message", Message.builder()
                    .content("Registration successful! A verification link has been sent to " + user.getEmail() + ". Please check your inbox (and spam folder).")
                    .type(MessageType.green).build());
        } catch (RuntimeException e) {
            System.out.println("Registration email failed: " + e.getMessage());
            session.setAttribute("message", Message.builder()
                    .content("Account created but we could not send a verification email to " + user.getEmail()
                            + ". Please contact support or try registering again.")
                    .type(MessageType.red).build());
        }

        return "redirect:/login";
    }

}
