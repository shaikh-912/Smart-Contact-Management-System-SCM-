package com.scm.project.services.serviceImplement;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scm.project.Entity.Contact;
import com.scm.project.Entity.User;
import com.scm.project.helper.ResourceNotFound;
import com.scm.project.repository.ContactRepo;
import com.scm.project.services.contactService;

@Service
public class contactImpl implements contactService{
	//constructor injection
	private final ContactRepo contactRepo;
	public contactImpl(ContactRepo contactRepo) {
		this.contactRepo=contactRepo;
	}

	@Override
	public Contact saveContact(Contact contact) {
		String id=UUID.randomUUID().toString();
		contact.setId(id);
		return contactRepo.save(contact);
		
	}

	@Override
	public Contact updateContact(Contact contact) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteContact(String id) {
		var contact= contactRepo.findById(id).orElseThrow(()->new ResourceNotFound("contact is not found with given ID"+id));
		contactRepo.delete(contact);
	}

	@Override
	public List<Contact> getAllContact() {
		return contactRepo.findAll();
	}

	@Override
	public Contact getById(String id) {
		return contactRepo.findById(id).orElseThrow(()-> new  ResourceNotFound("User not Found with given ID  :"+id));
	}

	@Override
	public List<Contact> searchContact(String name, String email, String phoneNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Contact> getByUserId(String id) {
		
		return contactRepo.findByUserId(id);
	}

}
