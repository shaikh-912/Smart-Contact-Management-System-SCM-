package com.scm.project.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scm.project.Entity.Contact;
import com.scm.project.Entity.User;

@Repository
public interface ContactRepo extends JpaRepository<Contact	, String> {

	//custom finder method
	Page<Contact> findByUser(User user,Pageable pageable);

	// count helpers for dashboard stats
	long countByUser(User user);
	long countByUserAndFavorite(User user, boolean favorite);
	
	//custom query method
	@Query("select c from Contact c where c.user.Id = :userid")
	List<Contact> findByUserId(@Param("userid") String userid);
	
	//search query
	
	Page<Contact> findByUserAndNameContaining(User user,String keyword,Pageable pageable);
	Page<Contact> findByUserAndPhoneNumberContaining(User user,String keyworg, Pageable pageable);
	Page<Contact> findByUserAndEmailContaining(User user,String keyworg, Pageable pageable);
	
	@Query("""
			SELECT c FROM Contact c
			WHERE c.user = :user
			AND (
			LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
			OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
			OR c.phoneNumber LIKE CONCAT('%', :keyword, '%')
			)
			""")
			Page<Contact> searchContacts(User user,
			                             String keyword,
			                             Pageable pageable);
}
