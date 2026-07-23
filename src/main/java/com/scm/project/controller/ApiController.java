package com.scm.project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scm.project.Entity.Contact;
import com.scm.project.services.contactService;

@RestController
@RequestMapping("/api")
public class ApiController {

	private final contactService contactService;
	public ApiController(contactService contactService){
		this.contactService=contactService;
	}
	
	@GetMapping("/contacts/{contactId}")
	public Contact getContacts(@PathVariable String contactId) {
		return contactService.getById(contactId);
	}
}
