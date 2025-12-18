package com.project.auction.controller;

import com.project.auction.models.User;
import com.project.auction.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(ProfileController.class)
@ActiveProfiles("test")
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setupSecurity() {
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getName()).thenReturn("testUser");

        SecurityContext context = Mockito.mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void getUserProfile_existingUser_returnsProfile() throws Exception {
        User user = new User("testUser", "pwd", "test@test.com");
        user.setId(10L);

        when(userRepository.findByName("testUser"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(get("/profile")
                        .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.username").value("testUser"))
               .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void getUserProfile_userNotFound_returns401() throws Exception {
        when(userRepository.findByName("testUser"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/profile"))
               .andExpect(status().isUnauthorized());
    }
}
