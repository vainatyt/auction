package com.project.auction.controller;

import com.project.auction.models.Comment;
import com.project.auction.models.User;
import com.project.auction.pojo.CreateCommentRequest;
import com.project.auction.pojo.LotResponse;
import com.project.auction.repository.CommentRepository;
import com.project.auction.repository.UserRepository;
import com.project.auction.service.CommentService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    private final UserRepository userRepository;
    private final CommentService commentService;
    private final CommentRepository commentRepository;

    public CommentController(CommentService commentService,
                                UserRepository userRepository,
                                CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.commentService = commentService;
        this.commentRepository = commentRepository;
    }

    @PostMapping("/write")
    public ResponseEntity<Comment> create(@RequestBody CreateCommentRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRepository.findByName(username);
        Comment saved = commentService.createComment(request, user.get().getId());
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/getmy")
    public ResponseEntity<Page<Comment>> getMyComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByName(username)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"));

        try {
            Page<Comment> myCom = commentRepository.findByIdAddresseeId(user.getId(),PageRequest.of(page,size));
            return ResponseEntity.ok(myCom);
            
        } catch (RuntimeException e) {
            log.error("Failed to get lots for user {}", user.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load lots");
        }
    }

}
