package com.project.auction.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.auction.models.Photo;
import com.project.auction.repository.PhotoRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.io.IOException;

@Service
public class ImageService {
    
    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

    private final PhotoRepository photoRepository;
    private final String uploadDir;

    public ImageService(PhotoRepository photoRepository, 
                       @Value("${app.upload.dir:users_lots_photo/}") String uploadDir) {
        this.photoRepository = photoRepository;
        this.uploadDir = uploadDir;
    }
    
    public Photo savePhoto(Long lotId, MultipartFile image){
        if (image == null || image.isEmpty()) {
            log.debug("No image provided for lot {}", lotId);
            return null;
        }
        validateImage(image);
        UUID uuid = saveImage(image);
        Photo photo = new Photo(lotId, uuid);
        
        return photoRepository.save(photo);  
    }

    private void validateImage(MultipartFile image) {
        if (image.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Image size exceeds 5MB");
        }
        
        String contentType = image.getContentType();
        if (contentType == null || (!contentType.startsWith("image/"))) {
            throw new IllegalArgumentException("Invalid image format");
        }
    }

    public UUID saveImage(MultipartFile image) {
        try {
            UUID uuid = UUID.randomUUID();
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(uuid.toString() + extension);
            
            image.transferTo(filePath);
        
            return uuid; 
            
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить фото", e);
        }
    }
    
    public ResponseEntity<Resource> getImage(String uuid, String contentType) {
        try {
            Path filePath = Paths.get(uploadDir + uuid);
            if (!Files.exists(filePath)) {
                filePath = Paths.get(uploadDir + uuid + ".jpg");
                if (!Files.exists(filePath)) filePath = Paths.get(uploadDir + uuid + ".png");
            }
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "image/jpeg"))
                .body(resource);
                
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public void delete(Photo photo){
        String uuid = photoRepository.findUuidByLotId(photo.getLotId()).toString();
         try {
        // 1. Ищем файл (аналогично getImage)
        Path filePath = Paths.get(uploadDir + uuid);
        if (!Files.exists(filePath)) {
            filePath = Paths.get(uploadDir + uuid + ".jpg");
            if (!Files.exists(filePath)) {
                filePath = Paths.get(uploadDir + uuid + ".png");
            }
        }
        
        if (!Files.exists(filePath)) {
            throw new RuntimeException(String.format("Photo lot=%d not found", photo.getLotId()));
        }
        
        // 2. Удаляем файл
        Files.delete(filePath);
        log.info("Image deleted: {}", filePath);
        
        // 3. Удаляем запись из БД (предполагаю таблицу image_metadata)
        photoRepository.deleteByLotId(photo.getLotId());
            
        } catch (IOException e) {
            log.error("Failed to delete image {}: {}", uuid, e.getMessage());
            throw new RuntimeException(String.format("Failed to delete image lot=%d", photo.getLotId()));

        }
    }
}
