package com.project.auction.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class RealEmailSender implements EmailSender {
    private final JavaMailSender mailSender;
    
    public RealEmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Override
    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
