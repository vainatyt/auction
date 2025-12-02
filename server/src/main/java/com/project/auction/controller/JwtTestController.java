package com.project.auction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import com.project.auction.config.jwt.JwtUtils;
import com.project.auction.pojo.MessageResponse;

@RestController
@RequestMapping("/api")
public class JwtTestController {

    @Autowired
    JwtUtils jwtUtils;
    
    @GetMapping("/testjwt")
    public ResponseEntity<?> testjwt(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return ResponseEntity.ok(new MessageResponse("your username is " + username));
    } 
}
