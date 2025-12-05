package com.project.auction.service;

import com.project.auction.models.*;
import com.project.auction.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrackableItemService {

    private final TrackableItemRepository trackableItemRepository;
    private final UserRepository userRepository;
    private final LotRepository lotRepository;

    public TrackableItemService(TrackableItemRepository trackableItemRepository,
                                UserRepository userRepository,
                                LotRepository lotRepository) {
        this.trackableItemRepository = trackableItemRepository;
        this.userRepository = userRepository;
        this.lotRepository = lotRepository;
    }

    @Transactional
    public void addToTracking(Long userId, Long lotId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new RuntimeException("Lot not found"));

        // не добавлять дубликаты
        if (trackableItemRepository.findByUserAndLot(user, lot).isEmpty()) {
            TrackableItem item = new TrackableItem(lot.getId(), user.getId());
            trackableItemRepository.save(item);
        }
    }

    @Transactional
    public void removeFromTracking(Long userId, Long lotId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new RuntimeException("Lot not found"));

        trackableItemRepository.deleteByUserAndLot(user, lot);
    }

    public List<TrackableItem> getTrackedLots(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return trackableItemRepository.findByUser(user);
    }
}
