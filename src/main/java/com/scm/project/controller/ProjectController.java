package com.scm.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.project.services.userService;

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
       public String signup(){
        return "redirect:/login";
    }
}
