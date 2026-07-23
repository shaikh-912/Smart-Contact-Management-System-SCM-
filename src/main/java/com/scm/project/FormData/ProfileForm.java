package com.scm.project.FormData;

import org.springframework.web.multipart.MultipartFile;

public class ProfileForm {

    private String name;
    private String phoneNumber;
    private String about;
    private MultipartFile profileImage;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public MultipartFile getProfileImage() { return profileImage; }
    public void setProfileImage(MultipartFile profileImage) { this.profileImage = profileImage; }
}
