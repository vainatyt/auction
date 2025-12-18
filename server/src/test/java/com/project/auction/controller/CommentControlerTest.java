package com.project.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.auction.models.Comment;
import com.project.auction.models.CommentId;
import com.project.auction.models.User;
import com.project.auction.pojo.CreateCommentRequest;
import com.project.auction.repository.UserRepository;
import com.project.auction.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@ActiveProfiles("test")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setupSecurityContext() {
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getName()).thenReturn("testUser");

        SecurityContext context = Mockito.mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }

    @Test
    void createComment_returnsSavedComment() throws Exception {
        // given
       CreateCommentRequest request = new CreateCommentRequest();
        request.setAddresseeId(1L);
        request.setRating(5);
        request.setReview("Nice lot");

        User user = new User("testUser", "pwd", "test@test.com");
        user.setId(10L);

        Comment saved = new Comment();

        CommentId commentId = new CommentId();
        commentId.setCommentatorId(10L);
        commentId.setAddresseeId(1L);
        saved.setId(commentId);
        saved.setRating(5);      // если есть такое поле
        saved.setReview("Nice lot");

        when(userRepository.findByName("testUser")).thenReturn(Optional.of(user));
        when(commentService.createComment(any(CreateCommentRequest.class), eq(10L)))
                .thenReturn(saved);

        // when/then
        mockMvc.perform(post("/comments/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(100L))
               .andExpect(jsonPath("$.text").value("Nice lot"));
    }
}
