package com.project.auction.controller;

import java.nio.file.Path;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;

import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class ImageController {
    
    private final String uploadDir = "users_lots_photo/";
    
    @GetMapping("/users_lots_photo/{uuid}")
    public ResponseEntity<Resource> getImage(@PathVariable String uuid) {
        System.out.println("get photo: "+uuid);
        try {
            // Пробуем .jpg, .png
            Path filePath = Paths.get(uploadDir + uuid + ".jpg");
            if (!Files.exists(filePath)) {
                filePath = Paths.get(uploadDir + uuid + ".png");
            }
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
