package com.project.auction.controller;

import com.project.auction.config.jwt.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JwtTestController.class)
@ActiveProfiles("test")
class JwtTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    void testjwt_withValidBearerToken_returnsUsername() throws Exception {
        String token = "fake-jwt";
        String username = "testUser";

        when(jwtUtils.getUserNameFromJwtToken(anyString()))
                .thenReturn(username);

        mockMvc.perform(get("/api/testjwt")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message")
                       .value("your username is " + username));
    }

    @Test
    void testjwt_withoutAuthorizationHeader_returns401() throws Exception {
        mockMvc.perform(get("/api/testjwt"))
               .andExpect(status().isUnauthorized())
               .andExpect(content().string("Missing or invalid Authorization header"));
    }

    @Test
    void testjwt_withInvalidAuthorizationHeader_returns401() throws Exception {
        mockMvc.perform(get("/api/testjwt")
                        .header("Authorization", "Token something"))
               .andExpect(status().isUnauthorized())
               .andExpect(content().string("Missing or invalid Authorization header"));
    }
}
