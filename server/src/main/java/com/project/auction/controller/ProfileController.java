package com.project.auction.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.project.auction.models.User;
import com.project.auction.pojo.ProfileResponse;
import com.project.auction.repository.UserRepository;

@RestController
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);
    
    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getUserProfile() {   
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User user = userRepository.findByName(username)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"));
        
        try {
            ProfileResponse profile = new ProfileResponse(user.getName(), user.getEmail());
            log.debug("Profile loaded for user {}", username);
            return ResponseEntity.ok(profile);
            
        } catch (RuntimeException e) {
            log.error("Failed to load profile for user {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
