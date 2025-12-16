package com.project.auction.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final EmailSender emailSender;
    
    public MailService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }
    
    public void sendLotFinishedMail(String to, Long lotId, BigDecimal finalPrice, boolean isOwner) {
        String subject = "Лот " + lotId + " завершён";
        String text = isOwner ? 
            "Ваш лот " + lotId + " завершён. Финальная цена: " + finalPrice + "." :
            "Отслеживаемый вами лот " + lotId + " завершён. Финальная цена: " + finalPrice + ".";
        
        emailSender.send(to, subject, text);
    }
}
