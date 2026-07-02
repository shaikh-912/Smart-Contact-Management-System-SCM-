package com.scm.project.services;

import java.util.List;

import com.scm.project.Entity.Contact;
import com.scm.project.Entity.User;

public interface contactService {
	//methods
	//save contact
	Contact saveContact(Contact contact);
	
	//updateContact
	Contact updateContact(Contact contact);
	
	//delete contact
	void deleteContact(String id);
	
	//fetch all contact
	List<Contact> getAllContact();
	
	//fetch contact by ID
	Contact getById(String id);
	
	//search contact
	List<Contact> searchContact(String name, String email, String phoneNumber);
	
	//get contact by user id
	List<Contact> getByUserId(String id);
	
}
