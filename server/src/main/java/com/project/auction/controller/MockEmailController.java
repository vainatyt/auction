package com.project.auction.controller;

import com.project.auction.service.EmailDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/emails")
public class MockEmailController {
    private final List<EmailDto> mockEmails = new CopyOnWriteArrayList<>();
    
    @PostMapping("/mock")
    public ResponseEntity<Void> receiveMockEmail(@RequestBody EmailDto email) {
        mockEmails.add(email);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/list")
    public List<EmailDto> getMockEmails() {
        return new ArrayList<>(mockEmails);
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearEmails() {
        mockEmails.clear();
        return ResponseEntity.ok().build();
    }
}
