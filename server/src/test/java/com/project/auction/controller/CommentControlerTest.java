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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
class CommentControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @MockitoBean
    private CommentService commentService;  // ← сервис комментариев
    
    @MockitoBean
    private UserRepository userRepository;
    
    @Test
    @WithMockUser(username = "testUser")
    void createComment_returnsSavedComment() throws Exception {
    // Мокаем пользователя
    User testUser = new User("testUser", "pwd", "test@test.com");
    testUser.setId(10L);
    when(userRepository.findByName("testUser")).thenReturn(Optional.of(testUser));
    
    // ✅ ИСПРАВЛЕНО - используйте CommentId
    CommentId commentId = new CommentId();  // ← Создайте CommentId объект
    commentId.setCommentatorId(1L);  // или commentId = new CommentId(1L);
    
    Comment comment = new Comment();
    comment.setId(commentId);  // ← Передайте CommentId
    comment.setReview("Test comment");
    
    when(commentService.createComment(any(CreateCommentRequest.class), eq(10L)))
    .thenReturn(comment);

    
    CreateCommentRequest request = new CreateCommentRequest();
    request.setReview("Test comment");
    request.setAddresseeId(1L);
    
    mockMvc.perform(post("/comments/write")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id.commentatorId").value(1L));

}
}
