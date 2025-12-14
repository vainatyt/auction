package com.project.auction.service;

import com.project.auction.models.Comment;
import com.project.auction.models.CommentId;
import com.project.auction.pojo.CreateCommentRequest;
import com.project.auction.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Comment createComment(CreateCommentRequest request, Long commentatorId) {
        Comment comment = new Comment();
        comment.setId(new CommentId(commentatorId,request.getAddresseeId()));
        comment.setRating(request.getRating());
        comment.setReview(request.getReview());
        comment.setDate(LocalDateTime.now());

        return commentRepository.save(comment);
    }
}
