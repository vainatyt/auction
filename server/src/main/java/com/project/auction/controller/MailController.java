package com.project.auction.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.project.auction.models.Mail;
import com.project.auction.models.User;
import com.project.auction.repository.UserRepository;
import com.project.auction.service.MailService;

@RestController
public class MailController {
    private static final Logger log = LoggerFactory.getLogger(LotController.class);
    private final MailService mailService;
    private final UserRepository userRepository;

    MailController(MailService mailService,
                    UserRepository userRepository){
        this.mailService = mailService;
        this.userRepository = userRepository;
    }

    @GetMapping("/mails")
    public ResponseEntity<Page<Mail>> getMails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByName(username)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"));
        try{
            Page<Mail> mails = mailService.getMails(user.getId(), PageRequest.of(page, size));
            return ResponseEntity.ok(mails);
        }
        catch(ServiceException e){
            log.error("Failed to get mails for user {}", user.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
