package com.project.auction.service;

import com.project.auction.models.Comment;
import com.project.auction.models.CommentId;
import com.project.auction.models.User;
import com.project.auction.pojo.CreateCommentRequest;
import com.project.auction.repository.CommentRepository;
import com.project.auction.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Comment createComment(CreateCommentRequest request, Long commentatorId) {
        Comment comment = new Comment();
        comment.setId(new CommentId(commentatorId,request.getAddresseeId()));
        comment.setRating(request.getRating());
        comment.setReview(request.getReview());
        comment.setDate(Instant.now());

        return commentRepository.save(comment);
    }
}
