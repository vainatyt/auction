package com.project.auction.service;

import com.project.auction.models.*;
import com.project.auction.pojo.LotResponse;
import com.project.auction.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TrackableItemService {

    private static final Logger log = LoggerFactory.getLogger(TrackableItemService.class);
    
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
        log.debug("Adding lot {} to tracking for user {}", lotId, userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("User not found: id={}", userId);
                return  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
            });
        
        Lot lot = lotRepository.findById(lotId)
            .orElseThrow(() -> {
                log.warn("Lot not found: id={}", lotId);
                return  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
            });
        
        if (trackableItemRepository.findByUserIdAndLotId(userId, lotId).isEmpty()) {
            TrackableItem item = new TrackableItem(lot.getId(), user.getId());
            trackableItemRepository.save(item);
            log.info("Added lot {} to user {} tracking", lotId, userId);
        } else {
            log.debug("Lot {} already tracked by user {}", lotId, userId);
        }
    }

    @Transactional
    public void removeFromTracking(Long userId, Long lotId) {
        log.debug("Removing lot {} from tracking for user {}", lotId, userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("User not found: id={}", userId);
                return  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
            });
        
        Lot lot = lotRepository.findById(lotId)
            .orElseThrow(() -> {
                log.warn("Lot not found: id={}", lotId);
                return  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
            });

        trackableItemRepository.deleteByUserIdAndLotId(userId, lotId);
        log.info("Removed lot {} from user {} tracking", lotId, userId);
    }

    @Transactional(readOnly = true)
    public Page<LotResponse> getTrackedLots(Long userId, Pageable pageable) {
        log.debug("Getting tracked lots for user {}, page={}", userId, pageable.getPageNumber());
        
        userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("User not found: id={}", userId);
                return  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
            });
        
        Page<Object[]> rawPage = trackableItemRepository.findTrackedLotsByUserId(userId, pageable);
        Page<LotResponse> result = rawPage.map(row -> new LotResponse(
            (String)row[0], (String)row[1], (BigDecimal)row[2],
            (BigDecimal)row[3], (LocalDateTime)row[4], (LocalDateTime)row[5],
            ((Number)row[6]).longValue(), (UUID)row[7]));
        
        log.debug("Found {} tracked lots for user {}", result.getTotalElements(), userId);
        return result;
    }

    @Transactional(readOnly = true)
    public boolean isTracked(Long userId, Long lotId) {
        boolean result = trackableItemRepository.findByUserIdAndLotId(userId, lotId).isPresent();
        log.trace("User {} tracks lot {}: {}", userId, lotId, result);
        return result;
    }
}
