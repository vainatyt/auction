package com.project.auction.service;

import org.springframework.data.domain.Pageable;

import java.util.List;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.project.auction.controller.LotController;
import com.project.auction.models.Mail;
import com.project.auction.models.MetaDataLot;
import com.project.auction.models.User;
import com.project.auction.pojo.BuyStatus;
import com.project.auction.pojo.LotResponse;
import com.project.auction.repository.MailRepository;

import jakarta.transaction.Transactional;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(LotController.class);
    private final MailRepository mailRepository;
    
    public MailService(MailRepository mailRepository) {
        this.mailRepository = mailRepository;
    }
    
    public Page<Mail> getMails(Long userId, Pageable pageable){
        try{
            return mailRepository.findByUserId(userId, pageable);
        } catch (DataAccessException e) {
            log.error("Database error get mails for userId: {}", userId, e);
            throw new ServiceException("Failed to fetch mails", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid pageable: {}", pageable, e);
            throw new IllegalArgumentException("Invalid pagination parameters", e);
        }
    }

    @Transactional
    public void createMail(Mail mail) {
        validateMail(mail);
        mailRepository.save(mail);
    }

    public void notifyBuyReq(User user, MetaDataLot metaDataLot, BuyStatus status) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        String message;
        switch (status) {
            case PROCESSED:
                message = String.format(
                    "Your price change request for \"%s\"  has been SUCCESSFULLY PROCESSED!",
                    metaDataLot.getName()
                );
                break;
            case REJECTED:
                message = String.format(
                    "Your price change request for \"%s\" was REJECTED. Please check the price or auction status.",
                    metaDataLot.getName()
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown status: " + status);
        }
        
        Mail mail = new Mail(message, "Price change request status: " + status.name(), user.getId());
        createMail(mail);
    }

    @Transactional
    public void notifyOwner(User owner, User buyer, LotResponse lotResponse){
        if (owner == null || lotResponse == null) {
            throw new IllegalArgumentException("Owner and lot response cannot be null");
        }
        if(buyer==null){
            buyer = new User();
            buyer.setName("no one");
        }
        
        String message = String.format(
            "User %s purchased your lot \"%s\" for %.2f. To contact %s, write to: %s",
            buyer.getName(),
            lotResponse.getName(),
            lotResponse.getCurrentCost(),
            buyer.getName(),
            buyer.getEmail()
        );
        Mail mail = new Mail(message,"Purchase notification",owner.getId());
        createMail(mail);
    }

    @Transactional
    public void notifyBuyer(User owner, User buyer, LotResponse lotResponse) {
        if (owner == null || buyer == null || lotResponse == null) {
            throw new IllegalArgumentException("Owner, buyer and lot response cannot be null");
        }
        
        if (buyer.getId() == null) {
            throw new IllegalArgumentException("Buyer ID is required");
        }
        String link = "http://localhost:3000/comment/write/" + owner.getId();
        String message = String.format(
            "Congratulations! You purchased lot \"%s\" for %.2f from seller %s. " +
            "Contact seller by email: %s.\n" +
            "Write comment for them %s",
            lotResponse.getName(),
            lotResponse.getCurrentCost(),
            owner.getName(),
            owner.getEmail(),
            link
        );
        
        Mail mail = new Mail( message, "Purchase confirmation", buyer.getId());
        createMail(mail);
    }

    @Transactional
    public void notifyTrack(List<User> users, LotResponse lotResponse){
        if (lotResponse == null) {
            throw new IllegalArgumentException("lot response cannot be null");
        }
        for (User user: users){
            if (user == null) {
                log.warn("user is null, can not notify");
                continue;
            }
        
            String message = String.format(
                "Auction completed!\n\n" +
                "The lot \"%s\" you were tracking has ended.\n\n" +
                "Final price: %.2f\n",
                lotResponse.getName(),
                lotResponse.getCurrentCost()
            );
            Mail mail = new Mail( message, "Tracked auction completed", user.getId());
            createMail(mail);
        }
    }

    private void validateMail(Mail mail) {
        if (mail == null) {
            throw new IllegalArgumentException("Mail cannot be null");
        }
        
        if (mail.getUserId() == null) {
            throw new IllegalArgumentException("userId is required");
        }
        
        if (mail.getTitle() == null || mail.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("title is required");
        }
        
        if (mail.getMessage() == null || mail.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("message is required");
        }

        if (mail.getTitle().length() > 255) {
            throw new IllegalArgumentException("Title too long (max 255 chars)");
        }
        
        if (mail.getMessage().length() > 5000) {
            throw new IllegalArgumentException("Message too long (max 5000 chars)");
        }
    }

}
