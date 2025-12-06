package com.project.auction.controller;

import com.project.auction.models.TrackableItem;
import com.project.auction.service.TrackableItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/track")
public class TrackableItemController {

    private final TrackableItemService trackableItemService;

    public TrackableItemController(TrackableItemService trackableItemService) {
        this.trackableItemService = trackableItemService;
    }

    @PostMapping("/{userId}/{lotId}")
    public ResponseEntity<Void> add(@PathVariable Long userId,
                                    @PathVariable Long lotId) {
        trackableItemService.addToTracking(userId, lotId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/{lotId}")
    public ResponseEntity<Void> remove(@PathVariable Long userId,
                                       @PathVariable Long lotId) {
        trackableItemService.removeFromTracking(userId, lotId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<TrackableItem>> getForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(trackableItemService.getTrackedLots(userId));
    }
}
