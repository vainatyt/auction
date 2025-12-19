package com.project.auction.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import java.io.FileNotFoundException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

import com.project.auction.service.ImageService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "testUser")
    void getImage_pngExists_returnsOkAndPng() throws Exception {
        Path dir = Paths.get("users_lots_photo");
        Files.createDirectories(dir);

        Path pngPath = dir.resolve("1.png");
        byte[] data = new byte[]{1, 2, 3};
        Files.write(pngPath, data);

        try {
            mockMvc.perform(get("/users_lots_photo/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(data));
        } finally {
            Files.deleteIfExists(pngPath);
        }
    }

    @Test
    @WithMockUser(username = "testUser")
    void getImage_jpgExists_returnsOkAndJpeg() throws Exception {
        Path dir = Paths.get("users_lots_photo");
        Files.createDirectories(dir);

        Path jpgPath = dir.resolve("2.jpg");
        byte[] data = new byte[]{4, 5, 6};
        Files.write(jpgPath, data);

        try {
            mockMvc.perform(get("/users_lots_photo/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(data));
        } finally {
            Files.deleteIfExists(jpgPath);
        }
    }

    @Test
    @WithMockUser(username = "testUser")
    void getImage_notExists_returns404() throws Exception {
        Path dir = Paths.get("users_lots_photo");
        Files.createDirectories(dir);

        // гарантируем отсутствие файлов
        Files.deleteIfExists(dir.resolve("999.jpg"));
        Files.deleteIfExists(dir.resolve("999.png"));

        mockMvc.perform(get("/users_lots_photo/999"))
            .andExpect(status().isNotFound());
    }
}
