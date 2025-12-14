package com.project.auction.service;

import com.project.auction.models.*;
import com.project.auction.pojo.LotResponse;
import com.project.auction.repository.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @Transactional
    public Page<LotResponse> getTrackedLots(Long userId, Pageable pageable) {
        Page<Object[]> rawPage = trackableItemRepository.findTrackedLotsByUserId(userId, pageable);
        Page<LotResponse> result = rawPage.map(row -> new LotResponse(
            (String)row[0], (String)row[1], (BigDecimal)row[2],
            (BigDecimal)row[3], (LocalDateTime)row[4], (LocalDateTime)row[5],
            ((Number)row[6]).longValue(), (UUID)row[7]));
        return result;
    }

    public boolean isTracked(Long userId, Long lotId){
        System.out.println("start isTrack service");
        return trackableItemRepository.findByUserIdAndLotId(userId, lotId).isPresent(); 
    }

}
