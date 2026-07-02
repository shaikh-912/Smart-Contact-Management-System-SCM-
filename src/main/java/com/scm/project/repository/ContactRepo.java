package com.scm.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scm.project.Entity.Contact;
import com.scm.project.Entity.User;

@Repository
public interface ContactRepo extends JpaRepository<Contact	, String> {

	//custom finder method
	List<Contact> findByUser(User user);
	
	//custom query method
	@Query("select c from Contact c where c.user.Id =: userid")
	List<Contact> findByUserId(@Param("userid") String userid);
}
