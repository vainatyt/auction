package com.project.auction.service;

import com.project.auction.models.Comment;
import com.project.auction.pojo.CreateCommentRequest;
import com.project.auction.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void createComment_returnsSavedComment() {
        // given
        CreateCommentRequest request = new CreateCommentRequest();
        request.setAddresseeId(5L);
        request.setRating(4);
        request.setReview("Great product!");

        Long commentatorId = 10L;

        Comment savedComment = new Comment();
        savedComment.setRating(4);
        savedComment.setReview("Great product!");
        savedComment.setDate(LocalDateTime.now());

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // when
        Comment result = commentService.createComment(request, commentatorId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(4);
        assertThat(result.getReview()).isEqualTo("Great product!");
        assertThat(result.getId().getCommentatorId()).isEqualTo(10L);
        assertThat(result.getId().getAddresseeId()).isEqualTo(5L);
    }

    @Test
    void createComment_setsCorrectCommentId() {
        // given
        CreateCommentRequest request = new CreateCommentRequest();
        request.setAddresseeId(20L);
        request.setRating(5);
        request.setReview("Perfect!");

        Long commentatorId = 15L;

        Comment savedComment = new Comment();
        savedComment.setRating(5);
        savedComment.setReview("Perfect!");

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // when
        Comment result = commentService.createComment(request, commentatorId);

        // then
        assertThat(result.getId().getCommentatorId()).isEqualTo(commentatorId);
        assertThat(result.getId().getAddresseeId()).isEqualTo(request.getAddresseeId());
    }
}
