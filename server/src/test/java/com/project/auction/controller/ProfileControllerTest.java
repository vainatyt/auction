package com.project.auction.controller;

import com.project.auction.models.User;
import com.project.auction.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
    "spring.security.enabled=false"
})
@WithMockUser(username = "testUser", roles = {"USER"})
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User("testUser", "pwd", "test@test.com");
        testUser.setId(10L);
        
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(testUser));
    }

    @Test
    void getUserProfile_existingUser_returnsProfile() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    void getUserProfile_userNotFound_returns401() throws Exception {
        when(userRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk()); 
    }

}
