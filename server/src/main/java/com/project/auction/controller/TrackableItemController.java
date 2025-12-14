package com.project.auction.controller;

import com.project.auction.models.User;
import com.project.auction.pojo.LotResponse;
import com.project.auction.repository.UserRepository;
import com.project.auction.service.TrackableItemService;

import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;

@RestController
@RequestMapping("/track")
public class TrackableItemController {

    private final TrackableItemService trackableItemService;

    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(TrackableItemController.class);

    public TrackableItemController(TrackableItemService trackableItemService,
                                    UserRepository userRepository){
        this.trackableItemService = trackableItemService;
        this.userRepository = userRepository;
    }

    @PostMapping("/add/{lotId}")
    public ResponseEntity<Void> add(@PathVariable Long lotId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User user = userRepository.findByName(username)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"));
        
        try {
            if (trackableItemService.isTracked(user.getId(), lotId)) {
                log.info("Lot {} already tracked by user {}", lotId, username);
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            trackableItemService.addToTracking(user.getId(), lotId);
            log.info("User {} added lot {} to tracking", username, lotId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid lotId {} for user {}", lotId, username, e);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Failed to add tracking: user={}, lot={}", username, lotId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/remove/{lotId}")
    public ResponseEntity<Void> remove(@PathVariable Long lotId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User user = userRepository.findByName(username)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"));
        
        try {
            if (!trackableItemService.isTracked(user.getId(), lotId)) {
                log.info("User {} not tracking lot {}", username, lotId);
                return ResponseEntity.notFound().build();
            }
            
            trackableItemService.removeFromTracking(user.getId(), lotId);
            log.info("User {} removed lot {} from tracking", username, lotId);
            return ResponseEntity.noContent().build();
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid lotId {} for user {}", lotId, username, e);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Failed to remove tracking: user={}, lot={}", username, lotId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getmy")
    public ResponseEntity<Page<LotResponse>> getForUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByName(username)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"));
        
        try {
            Page<LotResponse> trackLots = trackableItemService
                .getTrackedLots(user.getId(), PageRequest.of(page, size));
            return ResponseEntity.ok(trackLots);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid pagination params: page={}, size={}", page, size, e);
            return ResponseEntity.badRequest()
                .body(Page.empty(PageRequest.of(0, 10)));
                
        } catch (RuntimeException e) {
            log.error("Failed to get tracked lots for user {}", username, e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load tracked lots");
        }
    }


    @GetMapping("/tracked/{lotId}")
    public ResponseEntity<Boolean> isTracked(@PathVariable("lotId") Long lotId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User user = userRepository.findByName(username)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"));
        
        try {
            boolean isTracked = trackableItemService.isTracked(user.getId(), lotId);
            log.debug("User {} tracking lot {}: {}", username, lotId, isTracked);
            return ResponseEntity.ok(isTracked);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid lotId {} for user {}", lotId, username, e);
            return ResponseEntity.badRequest().build();
            
        } catch (RuntimeException e) {
            log.error("Failed to check tracking: user={}, lot={}", username, lotId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
