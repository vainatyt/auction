package com.project.auction.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.auction.models.User;
import com.project.auction.pojo.ProfileResponse;
import com.project.auction.repository.UserRepository;

@RestController
public class ProfileController {

    @Autowired
	UserRepository userRespository;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRespository.findByName(username);
        return ResponseEntity.ok(new ProfileResponse(user.get().getName(),user.get().getEmail()));
    }
}
