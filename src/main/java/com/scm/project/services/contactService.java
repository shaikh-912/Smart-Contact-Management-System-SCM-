package com.scm.project.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
	
	//get contact by user
	Page<Contact> getByUser(User user,int page,int size, String sort,String sortDirection);
	
	//search by contact name
	Page<Contact> searchByName(User user,String keyword,int size,int page,String sort,String order);
	//search by contact Phone Number
	Page<Contact> searchByPhone(User user,String keyword,int size,int page,String sort,String order);
	//search by contact Email
	Page<Contact> searchByEmail(User user,String keyword,int size,int page,String sort,String order);
	
	//search All
	Page<Contact> searchAll(User user,String keyword,int size, int page, String sort, String order);

	
}
