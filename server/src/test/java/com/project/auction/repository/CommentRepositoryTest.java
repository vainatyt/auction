package com.project.auction.repository;

import com.project.auction.models.Comment;
import com.project.auction.models.CommentId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByAddresseeId_returnsCommentsAboutUser() {
        CommentId id = new CommentId();
        id.setCommentatorId(10L);
        id.setAddresseeId(1L);

        Comment c1 = new Comment(id, 5, "good", LocalDateTime.now());
        commentRepository.save(c1);

        List<Comment> result = commentRepository.findByAddresseeId(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReview()).isEqualTo("good");
        assertThat(result.get(0).getAddresseeId()).isEqualTo(1L);
    }

    @Test
    void findByCommentatorId_returnsCommentsWrittenByUser() {
        CommentId id = new CommentId();
        id.setCommentatorId(10L);
        id.setAddresseeId(2L);

        Comment c1 = new Comment(id, 4, "nice", LocalDateTime.now());
        commentRepository.save(c1);

        List<Comment> result = commentRepository.findByCommentatorId(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReview()).isEqualTo("nice");
        assertThat(result.get(0).getCommentatorId()).isEqualTo(10L);
    }
}
