package com.example.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        String subject = "Verify your email - LuckyLoot";
        String verificationLink = "http://localhost:8081/auth/verify?token=" + token;
        String body = "Hi,\n\nPlease click the link below to verify your email:\n" + verificationLink + "\n\nThank you!";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("your_email@gmail.com");

        mailSender.send(message);
    }
}
