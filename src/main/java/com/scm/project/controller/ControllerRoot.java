package com.scm.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scm.project.Entity.User;
import com.scm.project.helper.Helper;
import com.scm.project.services.userService;

@ControllerAdvice
public class ControllerRoot {

	@Autowired
	private userService userService;
	//Loged in user information
    @ModelAttribute
    public void addLogedInUser(Model model,Authentication authentication) {
    	if(authentication==null) {
    		return;
    	}
    	System.out.print("adding loged in user informateion ");
    	  String name=Helper.getEmailOfLoggedUser(authentication);
          System.out.println("user name is "+name);
          User user= userService.findByEmail(name);          
          model.addAttribute("user",user);
    }
}
