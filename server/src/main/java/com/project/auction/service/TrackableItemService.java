package com.project.auction.service;

import com.project.auction.models.*;
import com.project.auction.pojo.LotResponse;
import com.project.auction.repository.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        if (trackableItemRepository.findByUserIdAndLotId(userId, lotId).isEmpty()) {
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

        trackableItemRepository.deleteByUserIdAndLotId(userId, lotId);
    }

    public Page<LotResponse> getTrackedLots(Long userId, Pageable pageable) {
        return trackableItemRepository.findLotAndMetadataByUserId(userId,pageable);
    }
}
