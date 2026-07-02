package com.scm.project.FormData;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserForm {
    @NotBlank(message = "Name Field is required")
    private String name;
    @NotBlank(message = "email Field is required")
    @Email(message = "Invalid email address")
    private String email;
    @NotBlank(message = "password is required")
    @Size(min = 4 , message = "minimum 4 Character Required")
    private String password;
    @NotBlank(message = "phone Number is required")
    @Size(min=10,message = "invalid phone number")
    private String phoneNumber;
    @NotBlank(message="about is Required")
    private String about;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}

}
