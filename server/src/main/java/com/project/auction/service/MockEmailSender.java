package com.project.auction.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;

@Service
@Profile({"dev", "test", "default"})
public class MockEmailSender implements EmailSender {
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public void send(String to, String subject, String text) {
        EmailDto email = new EmailDto(to, subject, text, LocalDateTime.now());
        try {
            restTemplate.postForEntity("http://localhost:3000/api/emails/mock", email, Void.class);
        } catch (Exception e) {
            System.err.println("Mock email не отправлен: " + e.getMessage());
        }
    }
}
