package com.scm.project.services;

public interface MailService {
    void sendEmail(String to, String subject, String body);
}
