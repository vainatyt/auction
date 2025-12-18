package com.project.auction.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
@ActiveProfiles("test")
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Path uploadDir;

    @BeforeEach
    void setUp() throws Exception {
        // создаём временную директорию users_lots_photo рядом с проектом
        uploadDir = Paths.get("users_lots_photo");
        Files.createDirectories(uploadDir);
        // чистим все файлы перед каждым тестом
        Files.list(uploadDir).forEach(p -> {
            try { Files.deleteIfExists(p); } catch (Exception ignored) {}
        });
    }

    @Test
    void getImage_jpgExists_returnsOkAndJpeg() throws Exception {
        String uuid = "test-jpg";
        Path jpgPath = uploadDir.resolve(uuid + ".jpg");
        Files.write(jpgPath, new byte[]{1, 2, 3}); // фиктивные данные

        mockMvc.perform(get("/users_lots_photo/{uuid}", uuid))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    void getImage_pngExists_returnsOkAndPng() throws Exception {
        String uuid = "test-png";
        Path pngPath = uploadDir.resolve(uuid + ".png");
        Files.write(pngPath, new byte[]{4, 5, 6});

        mockMvc.perform(get("/users_lots_photo/{uuid}", uuid))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    void getImage_notExists_returns404() throws Exception {
        String uuid = "missing";

        mockMvc.perform(get("/users_lots_photo/{uuid}", uuid))
               .andExpect(status().isNotFound());
    }
}
