package com.project.auction.controller;

import com.project.auction.models.Comment;
import com.project.auction.models.User;
import com.project.auction.pojo.CreateCommentRequest;
import com.project.auction.repository.UserRepository;
import com.project.auction.service.CommentService;
import com.project.auction.service.UserDetailsImpl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final UserRepository userRepository;
    private final CommentService commentService;

    public CommentController(CommentService commentService,
                                UserRepository userRepository) {
        this.userRepository = userRepository;
        this.commentService = commentService;
    }

    @PostMapping("/comments")
    public ResponseEntity<Comment> create(@RequestBody CreateCommentRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRepository.findByName(username);
        Comment saved = commentService.createComment(request, user.get().getId());
        return ResponseEntity.ok(saved);
    }   

}
