package com.project.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.auction.models.User;
import com.project.auction.pojo.CreateLotRequest;
import com.project.auction.service.LotService;
import com.project.auction.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LotController.class)
@ActiveProfiles("test")
class LotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LotService lotService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpSecurity() {
        // кладём в SecurityContext пользователя с именем "user"
        var auth = new UsernamePasswordAuthenticationToken("user", "pwd");
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setId(1L);
        user.setName("user");
        Mockito.when(userRepository.findByName("user"))
               .thenReturn(Optional.of(user));
    }

    @Test
    void createLot_shouldReturn201() throws Exception {
        CreateLotRequest request = new CreateLotRequest();
        request.setGoodsName("Test lot");
        request.setGoodsDescription("Test description");
        request.setCurrentCost(new BigDecimal("1000.00"));
        request.setRateStep(new BigDecimal("50.00"));

        // заполни остальные поля…

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "photo.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image".getBytes()
        );

        mockMvc.perform(multipart("/lots/create")
                    .file(jsonPart)
                    .file(imagePart))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.message").value("Lot created successfully"));

        Mockito.verify(lotService)
               .createLot(eq(1L), any(CreateLotRequest.class), any());
    }
}
