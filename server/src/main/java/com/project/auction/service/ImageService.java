package com.project.auction.service;

import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private PhotoRepository photoRepository;
    
    private final String uploadDir = "users_lots_photo/";  // Создай папку в resources/static/uploads/
    
    public Photo savePhoto(Long lotId, MultipartFile image){
        //Делаем запись в бд
        System.out.println("Делаем запись в бд");
        if (image == null || image.isEmpty()) {
            System.out.println("Фото null");
            return null;
        }
        UUID uuid = saveImage(image);
        return photoRepository.save(new Photo(lotId, uuid));    
    }

    public UUID saveImage(MultipartFile image) {
        try {
            // Генерируем UUID
            UUID uuid = UUID.randomUUID();
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            
            // Путь для сохранения
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(uuid.toString() + extension);
            
            // Сохраняем файл
            image.transferTo(filePath);
        
            return uuid; 
            
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить фото", e);
        }
    }
    
    // Для отдачи файла по UUID
    public ResponseEntity<Resource> getImage(String uuid, String contentType) {
        try {
            Path filePath = Paths.get(uploadDir + uuid);
            // Пробуем разные расширения
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
}
