package com.project.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.auction.models.Lot;
import com.project.auction.models.User;
import com.project.auction.pojo.BuyLotRequest;
import com.project.auction.pojo.CreateLotRequest;
import com.project.auction.pojo.LotResponse;
import com.project.auction.repository.UserRepository;
import com.project.auction.service.LotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LotController.class)
@ActiveProfiles("test")
class LotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private LotService lotService;

    private User user;

    @BeforeEach
    void setupSecurity() {
        user = new User("testUser", "pwd", "test@test.com");
        user.setId(10L);

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getName()).thenReturn("testUser");

        SecurityContext context = Mockito.mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByName("testUser")).thenReturn(Optional.of(user));
    }

    @Test
    void createLot_validRequest_returns201() throws Exception {
        CreateLotRequest create = new CreateLotRequest();
        create.setGoodsName("Phone");
        create.setGoodsDescription("iPhone");
        create.setCurrentCost(new BigDecimal("1000.00"));
        create.setRateStep(new BigDecimal("50.00"));

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request",
                "request.json",
                "application/json",
                objectMapper.writeValueAsBytes(create)
        );
        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "img.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3}
        );

        mockMvc.perform(multipart("/lots/create")
                        .file(jsonPart)
                        .file(imagePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.message").value("Lot created successfully"));
    }

    @Test
    void createLot_invalidData_returns400() throws Exception {
        CreateLotRequest create = new CreateLotRequest();
        create.setGoodsName("Bad");
        MockMultipartFile jsonPart = new MockMultipartFile(
                "request",
                "request.json",
                "application/json",
                objectMapper.writeValueAsBytes(create)
        );

        Mockito.doThrow(new IllegalArgumentException("Bad data"))
               .when(lotService)
               .createLot(eq(10L), any(CreateLotRequest.class), any());

        mockMvc.perform(multipart("/lots/create")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Bad data"));
    }

    @Test
    void getMyLots_returnsPage() throws Exception {
        LotResponse lr = new LotResponse();
        lr.setId(1L);
        lr.setName("Phone");
        Page<LotResponse> page = new PageImpl<>(List.of(lr));

        when(lotService.findUserLotsWithMetadata(eq(10L), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/lots/getmy")
                        .param("page", "0")
                        .param("size", "10"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void getAllLots_returnsPage() throws Exception {
        LotResponse lr = new LotResponse();
        lr.setId(2L);
        lr.setName("TV");
        Page<LotResponse> page = new PageImpl<>(List.of(lr));

        when(lotService.findLotsWithMetadata(any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/lots/getall")
                        .param("page", "0")
                        .param("size", "10"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].id").value(2L));
    }

    @Test
    void getLot_found_returnsOk() throws Exception {
        LotResponse lr = new LotResponse();
        lr.setId(5L);
        lr.setName("Book");

        when(lotService.findLotWithMetadataById(5L)).thenReturn(lr);

        mockMvc.perform(get("/lots/{id}", 5L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    void getLot_notFound_returns404() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Not found"))
               .when(lotService).findLotWithMetadataById(99L);

        mockMvc.perform(get("/lots/{id}", 99L))
               .andExpect(status().isNotFound());
    }

    @Test
    void buyLot_validRequest_returnsCreatedLot() throws Exception {
        BuyLotRequest req = new BuyLotRequest();
        req.setLotId(1L);

        Lot lot = new Lot();
        lot.setId(1L);

        when(lotService.buyLot(eq(10L), any(BuyLotRequest.class))).thenReturn(lot);

        mockMvc.perform(post("/lots/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void buyLot_invalidRequest_returns400() throws Exception {
        BuyLotRequest req = new BuyLotRequest();
        req.setLotId(1L);

        Mockito.doThrow(new IllegalArgumentException("Bad"))
               .when(lotService).buyLot(eq(10L), any(BuyLotRequest.class));

        mockMvc.perform(post("/lots/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest());
    }
}
