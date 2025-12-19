package com.project.auction.controller;

import com.project.auction.models.User;
import com.project.auction.pojo.LotResponse;
import com.project.auction.repository.UserRepository;
import com.project.auction.service.TrackableItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
    "spring.security.enabled=false"
})
@WithMockUser(username = "testUser", roles = {"USER"})
class TrackableItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrackableItemService trackableItemService;

    @MockitoBean
    private UserRepository userRepository;

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

        when(userRepository.findByName("testUser"))
                .thenReturn(Optional.of(user));
    }

    @Test
    void add_notTracked_returns201() throws Exception {
        when(trackableItemService.isTracked(10L, 1L)).thenReturn(false);

        mockMvc.perform(post("/track/add/{lotId}", 1L))
               .andExpect(status().isCreated());
    }

    @Test
    void add_alreadyTracked_returns409() throws Exception {
        when(trackableItemService.isTracked(10L, 1L)).thenReturn(true);

        mockMvc.perform(post("/track/add/{lotId}", 1L))
               .andExpect(status().isConflict());
    }

    @Test
    void remove_tracked_returns204() throws Exception {
        when(trackableItemService.isTracked(10L, 1L)).thenReturn(true);

        mockMvc.perform(delete("/track/remove/{lotId}", 1L))
               .andExpect(status().isNoContent());
    }

    @Test
    void remove_notTracked_returns404() throws Exception {
        when(trackableItemService.isTracked(10L, 1L)).thenReturn(false);

        mockMvc.perform(delete("/track/remove/{lotId}", 1L))
               .andExpect(status().isNotFound());
    }

    @Test
    void getForUser_returnsPage() throws Exception {
        LotResponse lr = new LotResponse();
        lr.setId(5L);
        lr.setName("Phone");
        Page<LotResponse> page = new PageImpl<>(List.of(lr));

        when(trackableItemService.getTrackedLots(eq(10L), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/track/getmy")
                        .param("page", "0")
                        .param("size", "10"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].id").value(5L));
    }

    @Test
    void isTracked_returnsTrue() throws Exception {
        when(trackableItemService.isTracked(10L, 7L)).thenReturn(true);

        mockMvc.perform(get("/track/tracked/{lotId}", 7L))
               .andExpect(status().isOk())
               .andExpect(content().string("true"));
    }

    @Test
    void isTracked_invalidLot_returns400() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("bad"))
               .when(trackableItemService).isTracked(10L, 99L);

        mockMvc.perform(get("/track/tracked/{lotId}", 99L))
               .andExpect(status().isBadRequest());
    }

    
}
