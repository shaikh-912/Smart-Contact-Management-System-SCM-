package com.scm.project.services.serviceImplement;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
		Contact existingContact = contactRepo.findById(contact.getId())
				.orElseThrow(() -> new ResourceNotFound("Contact not found with ID: " + contact.getId()));
		existingContact.setName(contact.getName());
		existingContact.setEmail(contact.getEmail());
		existingContact.setPhoneNumber(contact.getPhoneNumber());
		existingContact.setAddress(contact.getAddress());
		existingContact.setDescription(contact.getDescription());
		existingContact.setWebSiteLink(contact.getWebSiteLink());
		existingContact.setLinkedInLink(contact.getLinkedInLink());
		existingContact.setFavorite(contact.isFavorite());
		// Only update picture if a new one was provided
		if (contact.getPicture() != null && !contact.getPicture().isEmpty()) {
			existingContact.setPicture(contact.getPicture());
			existingContact.setContactPublicId(contact.getContactPublicId());
		}
		return contactRepo.save(existingContact);
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

	@Override
	public Page<Contact> getByUser(User user,int page,int size,String sortBy,String Direction) {
		Sort sort=Direction.equals("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
		PageRequest pageable=PageRequest.of(page,size,sort);
		return contactRepo.findByUser(user,pageable);
	}

	@Override
	public Page<Contact> searchByName(User user, String keyword, int size, int page, String sortBy, String order) {
		Sort sort=order.equals("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
		PageRequest pageable=PageRequest.of(page,size,sort);
		return contactRepo.findByUserAndNameContaining(user,keyword, pageable);
	}

	@Override
	public Page<Contact> searchByPhone(User user, String keyword, int size, int page, String sortBy, String order) {
		Sort sort=order.equals("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
		PageRequest pageable=PageRequest.of(page,size,sort);
		return contactRepo.findByUserAndPhoneNumberContaining(user,keyword, pageable);
	}

	@Override
	public Page<Contact> searchByEmail(User user, String keyword, int size, int page, String sortBy, String order) {
		Sort sort=order.equals("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
		PageRequest pageable=PageRequest.of(page,size,sort);
		return contactRepo.findByUserAndEmailContaining(user,keyword, pageable);
	}

	@Override
	public Page<Contact> searchAll(User user, String keyword, int size, int page, String sortBy, String order) {
		Sort sort=order.equals("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
		PageRequest pageable=PageRequest.of(page, size,sort);
		
		return contactRepo.searchContacts(user, keyword, pageable);
	}

	

}
