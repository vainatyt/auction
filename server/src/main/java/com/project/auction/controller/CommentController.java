package com.project.auction.controller;

import com.project.auction.models.Comment;
import com.project.auction.pojo.CreateCommentRequest;
import com.project.auction.service.CommentService;
import com.project.auction.service.UserDetailsImpl;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // создание комментария, пока без security
    @PostMapping("/comments")
    public ResponseEntity<Comment> create(@RequestBody CreateCommentRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    Long commentatorId = userDetails.getId(); // или аналогичный метод
    Comment saved = commentService.createComment(request, commentatorId);
    return ResponseEntity.ok(saved);
}

}
