package com.scm.project.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.project.Entity.User;
import com.scm.project.helper.Helper;
import com.scm.project.services.userService;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private userService userService;

    //user Dashboard page
    @PostMapping("/dashboard")
    public String userDashboard(){
        return "user/dashboard";
    }
    @GetMapping("/dashboard")
public String userDashboard1() {
    return "user/dashboard";
}

    @RequestMapping("/profile")
    public String userProfile(Authentication authentication){
        String name=Helper.getEmailOfLoggedUser(authentication);
        System.out.println("user name is "+name);
       User user= userService.findByEmail(name);
       System.out.println(user.getName());
       System.out.println(user.getEmail());
       System.out.println(user.getPassword());
        return "user/profile";
    }

    //user add contact page

    //user view page

    //user edit page

    //user delete page

    //user search page
}
