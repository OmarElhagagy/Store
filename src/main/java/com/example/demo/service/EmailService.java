package com.example.demo.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
    
    void sendRegistrationConfirmationEmail(String to, String name);
}