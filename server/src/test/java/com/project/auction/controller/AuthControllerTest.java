package com.project.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.auction.models.Lot;
import com.project.auction.models.User;
import com.project.auction.pojo.CreateLotRequest;
import com.project.auction.service.LotService;
import com.project.auction.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

<<<<<<< HEAD
@WebMvcTest(LotController.class)
@ActiveProfiles("test")
class AuthControllerTest {

=======
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
class AuthControllerTest {
    
>>>>>>> e9f7391f028d91fe8ecc049c65882f9faffe2704
    @Autowired
    private MockMvc mockMvc;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @MockitoBean
    private LotService lotService;
    
    @MockitoBean
    private UserRepository userRepository;
    

    @Test
    @WithMockUser(username = "testUser")
    void createLot_shouldReturn201() throws Exception {
        User testUser = new User("testUser", "pwd", "test@test.com");
        testUser.setId(10L);
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(testUser));
        
        when(lotService.createLot(eq(10L), any(), any())).thenReturn(new Lot());
        
        CreateLotRequest request = new CreateLotRequest();
        request.setGoodsName("Test Phone");
        request.setGoodsDescription("iPhone");
        request.setCurrentCost(new BigDecimal("1000.00"));
        request.setRateStep(new BigDecimal("50.00"));
        
       MockMultipartFile requestFile = new MockMultipartFile(
        "request",           // ← имя части
        "request.json",      // ← filename
        "application/json",  // ← contentType
        objectMapper.writeValueAsBytes(request)  // ← данные
    );
    
    MockMultipartFile imageFile = new MockMultipartFile(
        "image",             // ← имя части
        "test.jpg",          // ← filename
        "image/jpeg",        // ← contentType
        new byte[]{1, 2, 3}  // ← данные
    );
    
    mockMvc.perform(multipart("/lots/create")
            .file(requestFile)
            .file(imageFile))
        .andExpect(status().isCreated());
    }

}
