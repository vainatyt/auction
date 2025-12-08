package com.project.auction.controller;

import com.project.auction.models.TrackableItem;
import com.project.auction.models.User;
import com.project.auction.pojo.LotResponse;
import com.project.auction.repository.UserRepository;
import com.project.auction.service.TrackableItemService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/track")
public class TrackableItemController {

    private final TrackableItemService trackableItemService;

    private final UserRepository userRepository;

    public TrackableItemController(TrackableItemService trackableItemService,
                                    UserRepository userRepository){
        this.trackableItemService = trackableItemService;
        this.userRepository = userRepository;
    }

    @PostMapping("/add/{lotId}")
    public ResponseEntity<Void> add(@PathVariable Long lotId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRepository.findByName(username); 
        trackableItemService.addToTracking(user.get().getId(), lotId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{lotId}")
    public ResponseEntity<Void> remove(@PathVariable Long lotId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRepository.findByName(username); 
        trackableItemService.removeFromTracking(user.get().getId(), lotId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getmy")
    public ResponseEntity<Page<LotResponse>> getForUser(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRepository.findByName(username);                    
        return ResponseEntity.ok(trackableItemService.getTrackedLots(user.get().getId(), PageRequest.of(page, size)));
    }

}
