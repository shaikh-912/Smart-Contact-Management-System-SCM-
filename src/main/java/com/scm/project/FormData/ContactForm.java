package com.scm.project.FormData;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactForm {

	private String name;
	
	private String email;
	
	private String phoneNumber;
	
	private String address;
	
	private String description;
	
	private boolean favorite;
	
	private String webSiteLink;
	
	private String linkedInLink;
	
	private MultipartFile contactImage;
}
