package com.project.auction.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);
    
    private final String uploadDir;

    public ImageController() {
        this.uploadDir = "users_lots_photo/";
    }

    @GetMapping("/users_lots_photo/{uuid}")
    public ResponseEntity<Resource> getImage(@PathVariable String uuid) {
        log.debug("Serving image: {}", uuid);
        
        try {
            Resource resource = findImageResource(uuid);
            if (resource == null || !resource.exists() || !resource.isReadable()) {
                log.warn("Image not found: {}", uuid);
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                .contentType(resolveMediaType(uuid))
                .body(resource);
                
        }catch(IllegalArgumentException e){
            log.warn("Invalid image ",e.getMessage());
            return ResponseEntity.badRequest().build();
        }catch (Exception e) {
            log.error("Failed to serve image {}: {}", uuid, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
    }
    
    private Resource findImageResource(String uuid) throws Exception {
        // Пробуем .jpg
        Path jpgPath = Paths.get(uploadDir + uuid + ".jpg");
        if (Files.exists(jpgPath) && Files.isReadable(jpgPath)) {
            return new UrlResource(jpgPath.toUri());
        }
        
        // Пробуем .png
        Path pngPath = Paths.get(uploadDir + uuid + ".png");
        if (Files.exists(pngPath) && Files.isReadable(pngPath)) {
            return new UrlResource(pngPath.toUri());
        }
        
        return null;
    }
    
    private MediaType resolveMediaType(String uuid) {
        Path jpgPath = Paths.get(uploadDir + uuid + ".jpg");
        if (Files.exists(jpgPath)) {
            return MediaType.IMAGE_JPEG;
        }
        return MediaType.IMAGE_PNG;
    }
}
