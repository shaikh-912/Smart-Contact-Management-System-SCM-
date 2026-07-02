package com.scm.project.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.project.Entity.Contact;
import com.scm.project.Entity.User;
import com.scm.project.FormData.ContactForm;
import com.scm.project.helper.Message;
import com.scm.project.helper.MessageType;
import com.scm.project.services.contactService;
import com.scm.project.services.imageService;
import com.scm.project.services.userService;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/user/contacts")
public class contactController {
	
	//cuntructor injection
	private final contactService contactService;
	private final userService userService;
	private final imageService imageService;
	public contactController(contactService contactService,userService userService,imageService imageService) {
		this.contactService=contactService;
		this.userService=userService;
		this.imageService=imageService;
	}
	
	@RequestMapping("/add")
	public String addContactView(Model model) {
		
		ContactForm contactForm=new ContactForm();
//		contactForm.setName("shahidShaikh");
//		contactForm.setFavorite(true);
		model.addAttribute("contactForm",contactForm);
		return "user/add_contact";
	}
	@RequestMapping(value = "/add",method=RequestMethod.POST)
	public String  saveContact(@ModelAttribute ContactForm contactForm,@AuthenticationPrincipal UserDetails current,
								HttpSession session) {
		
		//fetch user
		String useremail=current.getUsername();
		User user=userService.findByEmail(useremail);
		
		//process image 
		String filename=UUID.randomUUID().toString();
		System.out.println("image details : "+contactForm.getContactImage().getOriginalFilename());
		String url=imageService.uploadImage(contactForm.getContactImage(),filename);
		
		//process from data	
		
		//convert contactForm to contact 
		Contact contact=new Contact();
		contact.setName(contactForm.getName());
		contact.setEmail(contactForm.getEmail());
		contact.setPhoneNumber(contactForm.getPhoneNumber());
		contact.setAddress(contactForm.getAddress());
		contact.setDescription(contactForm.getDescription());
		contact.setWebSiteLink(contactForm.getWebSiteLink());
		contact.setLinkedInLink(contactForm.getLinkedInLink());
		contact.setUser(user);
		contact.setPicture(url);
		contact.setContactPublicId(filename);
		
		
	
		contactService.saveContact(contact);
		session.setAttribute("message",Message.builder()
				.content("You have Successfully Added new Contact.")
				.type(MessageType.green)
				.build());
		
		return "redirect:/user/contacts/add";
		
	}
}
