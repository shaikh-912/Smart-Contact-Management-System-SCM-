package com.scm.project.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.project.Entity.Contact;
import com.scm.project.Entity.User;
import com.scm.project.FormData.ContactForm;
import com.scm.project.helper.AppConstants;
import com.scm.project.helper.Helper;
import com.scm.project.helper.Message;
import com.scm.project.helper.MessageType;
import com.scm.project.repository.UserRepo;
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

		model.addAttribute("contactForm",contactForm);
		return "user/add_contact";
	}
	@RequestMapping(value = "/add",method=RequestMethod.POST)
	public String saveContact(@ModelAttribute ContactForm contactForm,
								Authentication authentication,
								HttpSession session) {
		
		//fetch user
		String useremail = Helper.getEmailOfLoggedUser(authentication);
		User user = userService.findByEmail(useremail);
		
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
		contact.setFavorite(contactForm.isFavorite());
		contact.setUser(user);
		if (url != null && !url.isEmpty()) {
		    contact.setPicture(url);
		}
		
		contact.setContactPublicId(filename);
		
		
	
		contactService.saveContact(contact);
		session.setAttribute("message",Message.builder()
				.content("You have Successfully Added new Contact.")
				.type(MessageType.green)
				.build());
		
		return "redirect:/user/contacts/add";
		
	}
	
	//view Contacts
	@GetMapping
	public String viewContact(
			@RequestParam(value = "page", defaultValue = "0")int page,
			@RequestParam(value = "size", defaultValue = "2")int size,
			@RequestParam(value = "sort", defaultValue = "name")String sort,
			@RequestParam(value = "direction", defaultValue ="asc") String direction,
			Model model, Authentication authentication) {
		String email=Helper.getEmailOfLoggedUser(authentication);
		
		User user=userService.findByEmail(email);
		Page<Contact> pageContacts=contactService.getByUser(user,page,size,sort,direction);
		model.addAttribute("pageContacts",pageContacts);
		model.addAttribute("pageSize",AppConstants.PAGE_SIZE);
		
//		pageContacts.getTotalPages()
		return "user/contacts";
	}
	@GetMapping("/search")
	public String searchHandler(
			@RequestParam("field") String field,
			@RequestParam("keyword") String keyword,
			@RequestParam(value = "page", defaultValue = "0")int page,
			@RequestParam(value = "size", defaultValue = "2")int size,
			@RequestParam(value="sort", defaultValue = "name") String sort,
			@RequestParam(value = "order", defaultValue = "asc") String order,
			Model model,
			Authentication authentication
			) {
		
		//get logged in user email
		String email = Helper.getEmailOfLoggedUser(authentication);
		User user=userService.findByEmail(email);
		Page<Contact> pageContacts=null;
	
		if(page<0) {
			page=0;
		}
		
		if(field.equalsIgnoreCase("name")) {
			pageContacts=contactService.searchByName(user, keyword, size	, page, sort, order);
		}else if(field.equalsIgnoreCase("phone")) {
			pageContacts =contactService.searchByPhone(user, keyword, size, page, sort, order);
		}else if(field.equalsIgnoreCase("email")) {
			pageContacts =contactService.searchByEmail(user,keyword, size, page, sort, order);
		}else {
			pageContacts=contactService.searchAll(user, keyword, size, page, sort, order);
		}
		model.addAttribute("pageContacts",pageContacts);
		model.addAttribute("pageSize",AppConstants.PAGE_SIZE);
		model.addAttribute("field",field);
		model.addAttribute("keyword",keyword);
		return "user/search";
	}
	
	
	//delete contact
	@GetMapping("/delete/{id}")
	public String deleteContact(@PathVariable String id,HttpSession session) {
		contactService.deleteContact(id);
		session.setAttribute("message", Message.builder().content("Contact Deleted Successfully!").type(MessageType.green).build());
		return "redirect:/user/contacts";
	}

	// ── Update Contact ──────────────────────────────────────────────────────────

	// GET: show pre-filled update form
	@GetMapping("/update/{id}")
	public String updateContactView(@PathVariable String id, Model model) {
		Contact contact = contactService.getById(id);

		// Map contact entity → ContactForm so Thymeleaf can bind th:object
		ContactForm contactForm = new ContactForm();
		contactForm.setName(contact.getName());
		contactForm.setEmail(contact.getEmail());
		contactForm.setPhoneNumber(contact.getPhoneNumber());
		contactForm.setAddress(contact.getAddress());
		contactForm.setDescription(contact.getDescription());
		contactForm.setWebSiteLink(contact.getWebSiteLink());
		contactForm.setLinkedInLink(contact.getLinkedInLink());
		contactForm.setFavorite(contact.isFavorite());

		model.addAttribute("contactForm", contactForm);
		model.addAttribute("contactId", id);
		model.addAttribute("existingPicture", contact.getPicture());
		return "user/update_contact";
	}

	// POST: process update form submission
	@PostMapping("/update/{id}")
	public String updateContact(
			@PathVariable String id,
			@ModelAttribute ContactForm contactForm,
			Authentication authentication,
			HttpSession session) {

		Contact existingContact = contactService.getById(id);

		existingContact.setName(contactForm.getName());
		existingContact.setEmail(contactForm.getEmail());
		existingContact.setPhoneNumber(contactForm.getPhoneNumber());
		existingContact.setAddress(contactForm.getAddress());
		existingContact.setDescription(contactForm.getDescription());
		existingContact.setWebSiteLink(contactForm.getWebSiteLink());
		existingContact.setLinkedInLink(contactForm.getLinkedInLink());
		existingContact.setFavorite(contactForm.isFavorite());

		// Only upload a new image if the user chose a file
		if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
			String newPublicId = UUID.randomUUID().toString();
			String newUrl = imageService.uploadImage(contactForm.getContactImage(), newPublicId);
			if (newUrl != null && !newUrl.isEmpty()) {
				existingContact.setPicture(newUrl);
				existingContact.setContactPublicId(newPublicId);
			}
		}

		contactService.updateContact(existingContact);

		session.setAttribute("message", Message.builder()
				.content("Contact updated successfully!")
				.type(MessageType.green)
				.build());

		return "redirect:/user/contacts";
	}
}
	