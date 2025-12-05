package com.project.auction.service;

import com.project.auction.models.Comment;
import com.project.auction.models.User;
import com.project.auction.pojo.CreateCommentRequest;
import com.project.auction.repository.CommentRepository;
import com.project.auction.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        User commentator = userRepository.findById(commentatorId)
                .orElseThrow(() -> new RuntimeException("Commentator not found"));
        userRepository.findById(request.getAddresseeId())
                .orElseThrow(() -> new RuntimeException("Addressee not found"));

        Comment comment = new Comment();
        comment.setCommentatorId(commentator.getId()); 
        comment.setAddresseeId(commentator.getId());
        comment.setRating(request.getRating());
        comment.setReview(request.getReview());
        comment.setDate(LocalDateTime.now());

        return commentRepository.save(comment);
    }
}
