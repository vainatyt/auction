package com.project.auction.service;

import java.math.BigDecimal;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendLotFinishedMail(String to, Long lotId, BigDecimal finalPrice, boolean isOwner) {
        String subject = "Лот " + lotId + " завершён";
        String text;
        if (isOwner) {
            text = "Ваш лот " + lotId + " завершён. Финальная цена: " + finalPrice + ".";
        } else {
            text = "Отслеживаемый вами лот " + lotId + " завершён. Финальная цена: " + finalPrice + ".";
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}
