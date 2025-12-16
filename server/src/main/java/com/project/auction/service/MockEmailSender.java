package com.project.auction.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;

@Service
@Profile({"dev", "test", "default"})
public class MockEmailSender implements EmailSender {
    
    private final RestTemplate restTemplate;
    
    public MockEmailSender() {
        this.restTemplate = new RestTemplate();
    }
    
    @Override
    public void send(String to, String subject, String text) {
        send(to, subject, text, null, null);
    }
    
    // Основной метод с данными о создателе лота
    public void send(String to, String subject, String text, Long ownerId, String ownerName) {
        EmailDto email = new EmailDto(to, subject, text, LocalDateTime.now(), ownerId, ownerName);
        
        try {
            restTemplate.postForEntity(
                "http://localhost:8080/api/emails/mock", 
                email, 
                Void.class
            );
            System.out.println("✅ Mock email отправлен: " + subject); // Для отладки
        } catch (Exception e) {
            System.err.println("❌ Mock email не отправлен: " + e.getMessage());
            // Не прерываем выполнение приложения
        }
    }
}
