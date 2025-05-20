package com.example.demo.service.impl;

import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Value("${app.frontend-url}")
    private String frontendUrl;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n\n"
                + frontendUrl + "/reset-password?token=" + token + "\n\n"
                + "If you did not request a password reset, please ignore this email.");
        
        mailSender.send(message);
    }

    @Override
    public void sendRegistrationConfirmationEmail(String to, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Welcome to Our E-commerce Store");
        message.setText("Dear " + name + ",\n\n"
                + "Thank you for registering with our e-commerce store. Your account has been successfully created.\n\n"
                + "You can now login to your account at " + frontendUrl + "/login\n\n"
                + "Best regards,\nThe E-commerce Team");
        
        mailSender.send(message);
    }
} 